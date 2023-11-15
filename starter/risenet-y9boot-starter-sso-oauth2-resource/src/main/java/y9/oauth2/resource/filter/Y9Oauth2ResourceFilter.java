package y9.oauth2.resource.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.exception.ErrorCode;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.model.AccessLog;
import net.risesoft.model.user.UserInfo;
import net.risesoft.model.user.UserProfile;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9TopicConst;
import net.risesoft.y9.util.InetAddressUtil;

/**
 *
 * @author dingzhaojun
 */
@Slf4j
public class Y9Oauth2ResourceFilter implements Filter {
    private WebApplicationContext ctx = null;
    private Environment env = null;
    private String serverIp = "";
    private String systemName = "";
    private String introspectionUri = "";
    private String profileUri = "";
    private boolean tokenCachedInSession = false;
    private String clientId = "";
    private String clientSecret = "";
    private boolean saveLogMessage = false;
    private boolean saveOnlineMessage = false;
    private RestTemplate restTemplate = new RestTemplate();
    private KafkaTemplate<String, Object> y9KafkaTemplate;

    private String buildExceptionMessage(Exception ex) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        ex.printStackTrace(printWriter);
        String s = stringWriter.toString();
        return s;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        // HttpSession session = request.getSession();

        UserInfo userInfo = null;
        long start = System.nanoTime();
        String errorMessage = "";
        String throwable = "";
        String success = "成功";
        String hostIp = "";
        long end = System.nanoTime();
        long elapsedTime = 0L;
        hostIp = getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");
        String url = request.getRequestURL().toString();

        if (this.env == null) {
            this.ctx = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
            this.env = this.ctx.getEnvironment();

            systemName = this.env.getProperty("y9.systemName");
            clientId = this.env.getProperty("y9.feature.oauth2.resource.opaque.client-id");
            clientSecret = this.env.getProperty("y9.feature.oauth2.resource.opaque.client-secret");
            introspectionUri = this.env.getProperty("y9.feature.oauth2.resource.opaque.introspection-uri");
            profileUri = this.env.getProperty("y9.feature.oauth2.resource.opaque.profile-uri");

            tokenCachedInSession = Boolean
                .parseBoolean(this.env.getProperty("y9.feature.oauth2.resource.opaque.tokenCachedInSession", "false"));

            // restTemplateBasicAuth = new RestTemplate();
            // restTemplateBasicAuth.getInterceptors().add(new BasicAuthenticationInterceptor(clientId, clientSecret,
            // StandardCharsets.UTF_8));
            boolean prop =
                this.env.getProperty("y9.feature.oauth2.resource.saveOnlineMessage", Boolean.class, Boolean.TRUE);
            if (prop) {
                saveOnlineMessage = true;
            }

            prop = this.env.getProperty("y9.feature.oauth2.resource.saveLogMessage", Boolean.class, Boolean.FALSE);
            if (prop) {
                saveLogMessage = true;
            }

            if (this.y9KafkaTemplate == null) {
                if (saveOnlineMessage || saveLogMessage) {
                    this.y9KafkaTemplate = ctx.getBean("y9KafkaTemplate", KafkaTemplate.class);
                }
            }
        }

        try {
            HttpSession session = request.getSession(false);
            String accessTokenInSession = session != null ? (String)session.getAttribute("access_token") : null;

            // 检查HttpHeader，是否存在"Authorization: Bearer ????"，或者access_token参数
            String accessToken = getAccessTokenFromRequest(request);
            if (accessToken != null) {
                boolean needVerifyToken = !tokenCachedInSession;
                if (needVerifyToken == false) {
                    if (!accessToken.equals(accessTokenInSession)) {
                        needVerifyToken = true;
                    }
                }
                if (needVerifyToken) {
                    ResponseEntity<OAuth20IntrospectionAccessTokenResponse> introspectEntity = null;
                    try {
                        introspectEntity = invokeIntrospectEndpoint(accessToken);
                    } catch (Exception ex) {
                        LOGGER.warn(ex.getMessage(), ex);
                        setResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, GlobalErrorCodeEnum.FAILURE);
                        return;
                    }

                    if (introspectEntity.getStatusCode().is2xxSuccessful()) {
                        OAuth20IntrospectionAccessTokenResponse introspectionResponse = introspectEntity.getBody();
                        if (!introspectionResponse.isActive()) {
                            setResponse(response, HttpStatus.UNAUTHORIZED, GlobalErrorCodeEnum.ACCESS_TOKEN_EXPIRED);
                            return;
                        }

                        String attr = null;
                        try {
                            attr = introspectionResponse.getAttr();
                            userInfo = Y9JsonUtil.readValue(attr, UserInfo.class);
                        } catch (Exception e) {
                            ResponseEntity<String> profileEntity = invokeProfileEndpoint(accessToken);
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

                            if (saveOnlineMessage) {
                                remoteSaveUserOnline(url, userInfo);
                            }
                            if (saveLogMessage) {

                            }
                        }
                    } else {
                        setResponse(response, HttpStatus.UNAUTHORIZED,
                            GlobalErrorCodeEnum.ACCESS_TOKEN_VERIFICATION_FAILED);
                        return;
                    }
                }

                filterChain.doFilter(request, response);
            } else {
                setResponse(response, HttpStatus.UNAUTHORIZED, GlobalErrorCodeEnum.ACCESS_TOKEN_NOT_FOUND);
            }
        } catch (Exception ex) {
            success = "出错";
            errorMessage = ex.getMessage();
            throwable = buildExceptionMessage(ex);
            throw ex;
        } finally {
            Y9LoginUserHolder.clear();
            end = System.nanoTime();
            elapsedTime = end - start;
            String y9aoplog = response.getHeader("y9aoplog");
            if (!"true".equals(y9aoplog)) {
                remoteSaveLog(url, userInfo, hostIp, elapsedTime, success, errorMessage, throwable, userAgent);
            }
        }
    }

    private String getAccessTokenFromRequest(final HttpServletRequest request) {
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

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.serverIp = InetAddressUtil.getLocalAddress().getHostAddress();
    }

    private ResponseEntity<OAuth20IntrospectionAccessTokenResponse> invokeIntrospectEndpoint(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(this.clientId, this.clientSecret, StandardCharsets.UTF_8);

        URI uri = URI.create(this.introspectionUri + "?token=" + accessToken);
        RequestEntity<?> requestEntity = new RequestEntity<>(headers, HttpMethod.POST, uri);
        ResponseEntity<OAuth20IntrospectionAccessTokenResponse> responseEntity =
            this.restTemplate.exchange(requestEntity, OAuth20IntrospectionAccessTokenResponse.class);
        return responseEntity;
    }

    private ResponseEntity<String> invokeProfileEndpoint(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + accessToken);

        URI uri = URI.create(this.profileUri + "?access_token=" + accessToken);
        RequestEntity<?> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(requestEntity, String.class);
        return responseEntity;
    }

    private void remoteSaveLog(String url, UserInfo userInfo, String hostIp, long elapsedTime, String success,
        String errorMessage, String throwable, String userAgent) {
        if (url.endsWith(".js") || url.endsWith(".css") || url.endsWith(".gif") || url.endsWith(".jpg")
            || url.endsWith(".png") || url.endsWith(".svg")) {
            // do nothing,skip
        } else {
            try {
                if (this.y9KafkaTemplate != null) {
                    AccessLog log = new AccessLog();
                    log.setLogLevel("RSLOG");
                    log.setLogTime(new Date());
                    log.setRequestUrl(url);
                    // log.setMethodName(url);
                    log.setElapsedTime(String.valueOf(elapsedTime));
                    log.setSuccess(success);
                    log.setLogMessage(errorMessage);
                    log.setThrowable(throwable);
                    log.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                    log.setServerIp(this.serverIp);
                    log.setUserHostIp(hostIp);
                    // log.setModularName();
                    // log.setOperateName("");
                    // log.setOperateType("url");
                    log.setUserAgent(userAgent);
                    log.setSystemName(this.systemName);
                    log.setUserId(userInfo.getParentId());
                    log.setUserName(userInfo.getLoginName());
                    log.setTenantId(userInfo.getTenantId());
                    log.setTenantName(userInfo.getTenantName());
                    log.setGuidPath(userInfo.getGuidPath());
                    log.setManagerLevel(String.valueOf(userInfo.getManagerLevel()));

                    String jsonString = Y9JsonUtil.writeValueAsString(log);
                    this.y9KafkaTemplate.send(Y9TopicConst.Y9_ACCESSLOG_MESSAGE, jsonString);
                }
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
    }

    private void remoteSaveUserOnline(String url, UserInfo userInfo) {
        if (userInfo != null) {
            try {
                String jsonString = Y9JsonUtil.writeValueAsString(userInfo);
                if (this.y9KafkaTemplate != null) {
                    this.y9KafkaTemplate.send(Y9TopicConst.Y9_USERONLINE_MESSAGE, jsonString);
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
        userInfo.setSex(Integer.parseInt(String.valueOf(map.get("sex"))));
        userInfo.setTenantId((String)map.get("tenantId"));
        userInfo.setTenantShortName((String)map.get("tenantShortName"));
        userInfo.setTenantName((String)map.get("tenantName"));
        userInfo.setGlobalManager(Boolean.valueOf(String.valueOf(map.get("globalManager"))));
        userInfo.setAvator((String)map.get("avator"));
        userInfo.setY9Roles((String)map.get("y9Roles"));
        userInfo.setPositions((String)map.get("positions"));
        userInfo.setPositionId((String)map.get("positionId"));
        return userInfo;
    }
}
