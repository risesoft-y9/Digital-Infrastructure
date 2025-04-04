package y9.oauth2.resource.filter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwk.InvalidPublicKeyException;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifier.BaseVerification;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.enums.platform.SexEnum;
import net.risesoft.exception.ErrorCode;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.feature.oauth2.resource.Y9Oauth2ResourceProperties;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9EnumUtil;

/**
 * @author dingzhaojun
 */
@Slf4j
public class Y9Oauth2ResourceFilter implements Filter {

    private final RestTemplate restTemplate = new RestTemplate();

    private final Y9Oauth2ResourceProperties y9Oauth2ResourceProperties;

    public Y9Oauth2ResourceFilter(Y9Oauth2ResourceProperties y9Oauth2ResourceProperties) {
        this.y9Oauth2ResourceProperties = y9Oauth2ResourceProperties;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

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

            if (introspectEntity.getStatusCode().value() != HttpStatus.OK.value()) {
                setResponse(response, HttpStatus.UNAUTHORIZED, GlobalErrorCodeEnum.ACCESS_TOKEN_VERIFICATION_FAILED);
                return;
            }

            OAuth20IntrospectionAccessTokenResponse introspectionResponse = introspectEntity.getBody();
            if (!introspectionResponse.isActive()) {
                setResponse(response, HttpStatus.UNAUTHORIZED, GlobalErrorCodeEnum.ACCESS_TOKEN_EXPIRED);
                return;
            }

            UserInfo userInfo = null;
            if (isJwtAccessToken(accessToken)) {
                DecodedJWT jwt = JWT.decode(accessToken);
                if (y9Oauth2ResourceProperties.getJwt().isValidationRequired() && !verify(jwt)) {
                    setResponse(response, HttpStatus.UNAUTHORIZED,
                            GlobalErrorCodeEnum.ACCESS_TOKEN_VERIFICATION_FAILED);
                    return;
                }
                userInfo = toUserInfo(jwt);
            } else {
                ResponseEntity<String> profileEntity = null;
                try {
                    profileEntity = invokeProfileEndpoint(accessToken);
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                    setResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, GlobalErrorCodeEnum.FAILURE);
                    return;
                }
                String profile = profileEntity.getBody();
                profile = profile.replace("[]", "\"\"");
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
            }

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            throw ex;
        } finally {
            Y9LoginUserHolder.clear();
        }
    }

    private boolean isJwtAccessToken(String accessToken) {
        return StringUtils.isNotBlank(accessToken) && accessToken.split("\\.").length == 3;
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

    public AbstractResource getResourceFrom(final String location) {
        if (location.toLowerCase().startsWith("file:")) {
            return new FileSystemResource(StringUtils.remove(location, "file:"));
        }
        if (location.toLowerCase().startsWith("classpath:")) {
            return new ClassPathResource(location.substring("classpath:".length()));
        }
        if (location.toLowerCase().startsWith("http")) {
            try {
                return new UrlResource(location);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private boolean verify(DecodedJWT jwt) {
        String kid = jwt.getKeyId();
        URL url = null;
        String location = y9Oauth2ResourceProperties.getJwt().getJwksLocation();
        AbstractResource resource = getResourceFrom(location);
        if (resource == null) {
            return false;
        } else {
            try {
                url = resource.getURL();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        }

        JwkProvider provider = new JwkProviderBuilder(url).cached(10, 24, TimeUnit.HOURS)
                .rateLimited(10, 1, TimeUnit.MINUTES).build();

        Jwk jwk = null;
        try {
            jwk = provider.get(kid);
        } catch (JwkException e) {
            e.printStackTrace();
            return false;
        }

        PublicKey publicKey = null;
        try {
            publicKey = jwk.getPublicKey();
        } catch (InvalidPublicKeyException e) {
            e.printStackTrace();
            return false;
        }

        Algorithm algorithm = null;
        switch (jwt.getAlgorithm()) {
            case "RS256":
                algorithm = Algorithm.RSA256((RSAPublicKey) publicKey);
                break;
            case "RS512":
                algorithm = Algorithm.RSA512((RSAPublicKey) publicKey);
        }
        try {
            algorithm.verify(jwt);
        } catch (SignatureVerificationException exception) {
            exception.printStackTrace();
            return false;
        }

        BaseVerification verification = (BaseVerification) JWT.require(algorithm);
        verification.withClaimPresence("tenantId");
        JWTVerifier verifier = verification.build();
        try {
            verifier.verify(jwt);
        } catch (JWTVerificationException exception) {
            exception.printStackTrace();
            return false;
        }

        return true;
    }

    private UserInfo toUserInfo(DecodedJWT jwt) {
        UserInfo userInfo = new UserInfo();
        userInfo.setCaid(jwt.getClaim("caid").asString());
        userInfo.setEmail(jwt.getClaim("email").asString());
        userInfo.setGuidPath(jwt.getClaim("guidPath").asString());
        userInfo.setDn(jwt.getClaim("dn").asString());
        userInfo.setLoginName(jwt.getClaim("loginName").asString());
        userInfo.setName(jwt.getClaim("name").asString());
        userInfo.setLoginType(jwt.getClaim("loginType").asString());
        userInfo.setMobile(jwt.getClaim("mobile").asString());
        userInfo.setOriginal(jwt.getClaim("original").asBoolean());
        userInfo.setOriginal(jwt.getClaim("original").asBoolean() != null && jwt.getClaim("original").asBoolean());
        userInfo.setOriginalId(jwt.getClaim("originalId").asString());
        userInfo.setParentId(jwt.getClaim("parentId").asString());
        userInfo.setPersonId(jwt.getClaim("personId").asString());
        userInfo.setPositionId(jwt.getClaim("positionId").asString());
        userInfo.setSex(Y9EnumUtil.valueOf(SexEnum.class, jwt.getClaim("sex").asInt()));
        userInfo.setTenantId(jwt.getClaim("tenantId").asString());
        userInfo.setTenantShortName(jwt.getClaim("tenantShortName").asString());
        userInfo.setTenantName(jwt.getClaim("tenantName").asString());
        userInfo.setIdNum(jwt.getClaim("idNum").asString());
        userInfo.setAvator(jwt.getClaim("avator").asString());
        userInfo.setPersonType(jwt.getClaim("personType").asString());
        //userInfo.setPassword(jwt.getClaim("password").asString());
        userInfo.setGlobalManager(jwt.getClaim("globalManager").asBoolean());
        userInfo.setManagerLevel(Y9EnumUtil.valueOf(ManagerLevelEnum.class, jwt.getClaim("managerLevel").asInt()));
        return userInfo;
    }
}
