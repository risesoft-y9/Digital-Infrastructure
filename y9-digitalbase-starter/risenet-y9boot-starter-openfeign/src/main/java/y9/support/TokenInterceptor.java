package y9.support;

import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import net.risesoft.y9.configuration.feature.api.Y9ApiProperties;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * 将令牌放入请求中
 *
 * @author shidaobang
 * @date 2024/01/09
 */
@RequiredArgsConstructor
public class TokenInterceptor implements RequestInterceptor {

    private static final String KEY_PREFIX = "y9-api-token:";

    private final Y9ApiProperties y9ApiProperties;
    private final RedisTemplate<String, String> redisTemplate;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String accessToken = getAccessTokenFromCache();
        if (StringUtils.isBlank(accessToken)) {
            accessToken = getAndCacheAccessToken();
        }
        if (StringUtils.isNotBlank(accessToken)) {
            // 将 access_token 往下游服务传
            requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        }
    }

    private String getAccessTokenFromCache() {
        String clientId = y9ApiProperties.getClientId();
        String key = KEY_PREFIX + clientId;
        return redisTemplate.opsForValue().get(key);
    }

    private String getAndCacheAccessToken() {
        String accessTokenUri = y9ApiProperties.getTokenUrl();
        String clientId = y9ApiProperties.getClientId();
        String clientSecret = y9ApiProperties.getClientSecret();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        URI uri = URI.create(accessTokenUri + "?client_id=" + clientId + "&client_secret=" + clientSecret
            + "&grant_type=client_credentials");
        RequestEntity<?> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);
        ResponseEntity<OAuth2AccessToken> responseEntity =
            restTemplate.exchange(requestEntity, OAuth2AccessToken.class);
        OAuth2AccessToken oAuth2AccessToken = responseEntity.getBody();
        String accessToken = oAuth2AccessToken.getAccessToken();
        String key = KEY_PREFIX + clientId;
        redisTemplate.opsForValue().set(key, accessToken, 1, TimeUnit.HOURS);

        return accessToken;
    }

    @Data
    public static class OAuth2AccessToken implements Serializable {
        private static final long serialVersionUID = 8242278854463512207L;

        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("expires_in")
        private Integer expiresIn;

        @JsonProperty("refresh_token")
        private String refreshToken;

        private String scope;

    }

}
