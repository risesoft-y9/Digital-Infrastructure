package y9.oauth2.resource.filter;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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

            ResponseEntity<OAuth20IntrospectionAccessTokenResponse> introspectEntity = null;
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

            OAuth20IntrospectionAccessTokenResponse introspectionResponse = introspectEntity.getBody();
            if (!introspectionResponse.isActive()) {
                setResponse(response, HttpStatus.UNAUTHORIZED, GlobalErrorCodeEnum.ACCESS_TOKEN_EXPIRED);
                return;
            }

            UserInfo userInfo = Y9JsonUtil.readValue(introspectionResponse.getAttr(), UserInfo.class);
            if (userInfo == null) {
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

    private String getIpAddr(HttpServletRequest request) {
        String addr = null;

        String[] addrHeader = {"X-Real-IP", "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP"};
        for (String header : addrHeader) {
            if (StringUtils.isEmpty(addr) || "unknown".equalsIgnoreCase(addr)) {
                addr = request.getHeader(header);
            } else {
                break;
            }
        }

        if (StringUtils.isEmpty(addr) || "unknown".equalsIgnoreCase(addr)) {
            addr = request.getRemoteAddr();
        } else {
            int i = addr.indexOf(",");
            if (i > 0) {
                addr = addr.substring(0, i);
            }
        }
        return addr;
    }

    private ResponseEntity<OAuth20IntrospectionAccessTokenResponse> invokeIntrospectEndpoint(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(y9Oauth2ResourceProperties.getOpaque().getClientId(),
            y9Oauth2ResourceProperties.getOpaque().getClientSecret(), StandardCharsets.UTF_8);

        URI uri = URI.create(y9Oauth2ResourceProperties.getOpaque().getIntrospectionUri() + "?token=" + accessToken);
        RequestEntity<?> requestEntity = new RequestEntity<>(headers, HttpMethod.POST, uri);
        ResponseEntity<OAuth20IntrospectionAccessTokenResponse> responseEntity =
            this.restTemplate.exchange(requestEntity, OAuth20IntrospectionAccessTokenResponse.class);
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
        userInfo.setY9Roles((String)map.get("roles"));
        userInfo.setPositions((String)map.get("positions"));
        userInfo.setPositionId((String)map.get("positionId"));
        return userInfo;
    }
}
