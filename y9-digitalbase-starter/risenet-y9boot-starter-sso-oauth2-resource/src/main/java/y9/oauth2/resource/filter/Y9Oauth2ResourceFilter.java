package y9.oauth2.resource.filter;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.enums.platform.SexEnum;
import net.risesoft.exception.ErrorCode;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.model.user.UserInfo;
import net.risesoft.model.user.UserProfile;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.feature.oauth2.resource.Y9Oauth2ResourceProperties;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9TopicConst;
import net.risesoft.y9.util.RemoteCallUtil;
import net.risesoft.y9.util.Y9EnumUtil;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;

/**
 *
 * @author dingzhaojun
 */
@Slf4j
public class Y9Oauth2ResourceFilter implements Filter {

    private final RestTemplate restTemplate = new RestTemplate();

    private final KafkaTemplate<String, Object> y9KafkaTemplate;
    private final Y9Properties y9Properties;
    private final Y9Oauth2ResourceProperties y9Oauth2ResourceProperties;

    public Y9Oauth2ResourceFilter(Y9Properties y9Properties, KafkaTemplate<String, Object> y9KafkaTemplate) {
        this.y9Properties = y9Properties;
        this.y9Oauth2ResourceProperties = y9Properties.getFeature().getOauth2().getResource();
        this.y9KafkaTemplate = y9KafkaTemplate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        try {
            HttpSession session = request.getSession(false);

            String accessToken = getAccessTokenFromRequest(request);
            if (StringUtils.isBlank(accessToken)) {
                setResponse(response, HttpStatus.UNAUTHORIZED, GlobalErrorCodeEnum.ACCESS_TOKEN_NOT_FOUND);
                return;
            }

            ResponseEntity<OAuth20IntrospectionAccessTokenSuccessResponse> introspectEntity = null;
            try {
                introspectEntity = invokeIntrospectEndpoint(accessToken);
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
                setResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, GlobalErrorCodeEnum.FAILURE);
                return;
            }

            if (introspectEntity.getStatusCodeValue() != HttpStatus.OK.value()) {
                setResponse(response, HttpStatus.UNAUTHORIZED, GlobalErrorCodeEnum.ACCESS_TOKEN_VERIFICATION_FAILED);
                return;
            }

            OAuth20IntrospectionAccessTokenSuccessResponse introspectionResponse = introspectEntity.getBody();
            if (!introspectionResponse.isActive()) {
                setResponse(response, HttpStatus.UNAUTHORIZED, GlobalErrorCodeEnum.ACCESS_TOKEN_EXPIRED);
                return;
            }

            UserInfo userInfo;
            if (isJwtAccessToken(accessToken)) {
                JWT jwt = JWTUtil.parseToken(accessToken);
                userInfo = jwt.getPayload().getClaimsJson().toBean(UserInfo.class);
            } else {
                if (StringUtils.isNotBlank(introspectionResponse.getAttr())) {
                    // 兼容修改过的 sso 服务 后期可移除
                    userInfo = Y9JsonUtil.readValue(introspectionResponse.getAttr(), UserInfo.class);
                } else {
                    ResponseEntity<String> profileEntity = null;
                    try {
                        profileEntity = invokeProfileEndpoint(accessToken);
                    } catch (Exception e) {
                        LOGGER.warn(e.getMessage(), e);
                        setResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, GlobalErrorCodeEnum.FAILURE);
                    }
                    String profile = profileEntity.getBody();
                    userInfo = Y9JsonUtil.readValue(profile, UserInfo.class);
                }
            }

            if (userInfo != null) {
                if (session == null) {
                    session = request.getSession(true);
                }
                session.setAttribute("access_token", accessToken);
                session.setAttribute("userInfo", userInfo);
                session.setAttribute("loginName", userInfo.getLoginName());
                session.setAttribute("positionId", userInfo.getPositionId());
                session.setAttribute("deptId", userInfo.getParentId());
                if (StringUtils.isNotBlank(userInfo.getPositionId())) {
                    Y9LoginUserHolder.setPositionId(userInfo.getPositionId());
                } else if (StringUtils.isNotBlank(userInfo.getPositions())) {
                    String[] postionList = userInfo.getPositions().split(",");
                    Y9LoginUserHolder.setPositionId(postionList[0]);
                }
                Y9LoginUserHolder.setTenantId(userInfo.getTenantId());
                Y9LoginUserHolder.setTenantName(userInfo.getTenantName());
                Y9LoginUserHolder.setTenantShortName(userInfo.getTenantShortName());
                Y9LoginUserHolder.setUserInfo(userInfo);

                if (y9Oauth2ResourceProperties.isSaveOnlineMessage()) {
                    remoteSaveUserOnline(userInfo);
                }
            }

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            throw ex;
        } finally {
            Y9LoginUserHolder.clear();
        }
    }

    private boolean isJwtAccessToken(String accessToken) {
        if (StringUtils.isNotBlank(accessToken) && accessToken.split("\\.").length == 3) {
            return true;
        }
        return false;
    }

    private String getAccessTokenFromRequest(final HttpServletRequest request) {
        // 从请求参数或请求头中获取令牌
        String accessToken = request.getParameter("access_token");
        if (StringUtils.isBlank(accessToken)) {
            String authHeader = request.getHeader("Authorization");
            if (StringUtils.isNotBlank(authHeader) && authHeader.startsWith("Bearer ")) {
                accessToken = authHeader.substring("Bearer ".length());
            }
        }

        return accessToken;
    }

    private ResponseEntity<OAuth20IntrospectionAccessTokenSuccessResponse>
        invokeIntrospectEndpoint(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(y9Oauth2ResourceProperties.getOpaque().getClientId(),
            y9Oauth2ResourceProperties.getOpaque().getClientSecret(), StandardCharsets.UTF_8);

        URI uri = URI.create(y9Oauth2ResourceProperties.getOpaque().getIntrospectionUri() + "?token=" + accessToken);
        RequestEntity<?> requestEntity = new RequestEntity<>(headers, HttpMethod.POST, uri);
        ResponseEntity<OAuth20IntrospectionAccessTokenSuccessResponse> responseEntity =
            this.restTemplate.exchange(requestEntity, OAuth20IntrospectionAccessTokenSuccessResponse.class);
        return responseEntity;
    }

    private ResponseEntity<String> invokeProfileEndpoint(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + accessToken);

        URI uri = URI.create(y9Oauth2ResourceProperties.getOpaque().getProfileUri() + "?access_token=" + accessToken);
        RequestEntity<?> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(requestEntity, String.class);
        return responseEntity;
    }

    private void remoteSaveUserOnline(UserInfo userInfo) {
        if (userInfo != null) {
            try {
                if (Objects.equals(y9Oauth2ResourceProperties.getOnlineMessagePushType(),
                    Y9Oauth2ResourceProperties.OnlineMessagePushType.KAFKA)) {
                    String jsonString = Y9JsonUtil.writeValueAsString(userInfo);
                    if (this.y9KafkaTemplate != null) {
                        this.y9KafkaTemplate.send(Y9TopicConst.Y9_USERONLINE_MESSAGE, jsonString);
                    }
                } else if (Objects.equals(y9Oauth2ResourceProperties.getOnlineMessagePushType(),
                    Y9Oauth2ResourceProperties.OnlineMessagePushType.API)) {
                    String userOnlineBaseUrl = y9Properties.getCommon().getUserOnlineBaseUrl();
                    String saveOnlineUrl = userOnlineBaseUrl + "/services/rest/userOnline/saveAsync";
                    List<NameValuePair> requestBody = RemoteCallUtil.objectToNameValuePairList(userInfo);
                    RemoteCallUtil.post(saveOnlineUrl, null, requestBody, Object.class);
                }
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
    }

    private void setResponse(HttpServletResponse response, HttpStatus httpStatus, ErrorCode errorCode) {
        response.addHeader("WWW-Authenticate", "Bearer realm=\"risesoft\"");
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            response.getWriter().write(Y9JsonUtil.writeValueAsString(Y9Result.failure(errorCode)));
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * cas.authn.oauth.userProfileViewType=FLAT(NESTED)，因此本方法不需要
     *
     * @param userProfile
     * @return
     */
    @SuppressWarnings("unused")
    private UserInfo toUserInfo(UserProfile userProfile) {
        UserInfo userInfo = new UserInfo();
        Map<String, Object> map = userProfile.getAttributes();
        userInfo.setCaid((String)map.get("caid"));
        userInfo.setEmail((String)map.get("email"));
        userInfo.setGuidPath((String)map.get("guidPath"));
        userInfo.setLoginName((String)map.get("loginName"));
        userInfo.setLoginType((String)map.get("loginType"));
        userInfo.setMobile((String)map.get("mobile"));
        userInfo.setOriginal(Boolean.parseBoolean(String.valueOf(map.get("original"))));
        userInfo.setOriginalId((String)map.get("originalId"));
        userInfo.setParentId((String)map.get("parentId"));
        userInfo.setPersonId((String)map.get("personId"));
        userInfo.setSex(Y9EnumUtil.valueOf(SexEnum.class, Integer.valueOf(String.valueOf(map.get("sex")))));
        userInfo.setTenantId((String)map.get("tenantId"));
        userInfo.setTenantShortName((String)map.get("tenantShortName"));
        userInfo.setTenantName((String)map.get("tenantName"));
        userInfo.setGlobalManager(Boolean.parseBoolean(String.valueOf(map.get("globalManager"))));
        userInfo.setAvator((String)map.get("avator"));
        userInfo.setRoles((String)map.get("roles"));
        userInfo.setPositions((String)map.get("positions"));
        userInfo.setPositionId((String)map.get("positionId"));
        return userInfo;
    }
}
