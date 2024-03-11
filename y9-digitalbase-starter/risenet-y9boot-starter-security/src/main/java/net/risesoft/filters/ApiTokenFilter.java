package net.risesoft.filters;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.feature.security.api.Y9ApiProperties;
import net.risesoft.y9.exception.Y9UnauthorizedException;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * API令牌过滤器
 *
 * @author shidaobang
 * @date 2024/01/08
 */
@RequiredArgsConstructor
public class ApiTokenFilter implements Filter {

    private final Y9Properties y9Properties;

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        try {
            String accessToken = getAccessTokenFromRequest(httpRequest);
            checkAccessToken(accessToken);
        } catch (Y9UnauthorizedException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(Y9JsonUtil.writeValueAsString(Y9Result.failure(e.getCode(), e.getMessage())));
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean checkAccessToken(String accessToken) {
        Y9ApiProperties y9ApiProperties = y9Properties.getFeature().getSecurity().getApi();
        String clientId = y9ApiProperties.getClientId();
        String clientSecret = y9ApiProperties.getClientSecret();
        String introspectionUri = y9ApiProperties.getTokenIntrospectionUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret, StandardCharsets.UTF_8);

        URI uri = URI.create(introspectionUri + "?token=" + accessToken);
        RequestEntity<?> requestEntity = new RequestEntity<>(headers, HttpMethod.POST, uri);
        ResponseEntity<Oauth2Introspection> responseEntity =
            restTemplate.exchange(requestEntity, Oauth2Introspection.class);
        Oauth2Introspection oauth2Introspection = responseEntity.getBody();
        if (!oauth2Introspection.isActive()) {
            throw new Y9UnauthorizedException(GlobalErrorCodeEnum.ACCESS_TOKEN_EXPIRED.getCode(),
                GlobalErrorCodeEnum.ACCESS_TOKEN_EXPIRED.getDescription());
        }
        return true;
    }

    private String getAccessTokenFromRequest(HttpServletRequest request) {
        // 从请求参数或请求头中获取令牌 如果两者中都没有则抛出异常
        String accessToken = request.getParameter("access_token");
        if (StringUtils.isBlank(accessToken)) {
            String authHeader = request.getHeader("Authorization");
            if (StringUtils.isNotBlank(authHeader) && authHeader.startsWith("Bearer ")) {
                accessToken = authHeader.substring("Bearer ".length());
            }
        }
        if (StringUtils.isBlank(accessToken)) {
            throw new Y9UnauthorizedException(GlobalErrorCodeEnum.ACCESS_TOKEN_NOT_FOUND.getCode(),
                GlobalErrorCodeEnum.ACCESS_TOKEN_NOT_FOUND.getDescription());
        }
        return accessToken;
    }

    @Data
    public static class Oauth2Introspection {

        private boolean active;

        private String attr;

        private String sub;

        private String scope;

        private long iat;

        private long exp;

        private String realmName;

        private String uniqueSecurityName;

        private String tokenType;

        private String aud;

        private String iss;

        @JsonProperty("client_id")
        private String clientId;

        @JsonProperty("grant_type")
        private String grantType;
    }
}
