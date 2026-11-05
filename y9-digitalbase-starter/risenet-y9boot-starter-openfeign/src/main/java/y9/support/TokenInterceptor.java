package y9.support;

import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;

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

    private final String clientRegistrationId;
    private final OAuth2AuthorizedClientManager authorizedClientManager;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String accessToken = getAccessToken();
        requestTemplate.removeHeader(HttpHeaders.AUTHORIZATION);
        requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
    }

    private synchronized String getAccessToken() {
        // 使用固定的应用身份，使同一实例的所有请求复用一个令牌，不依赖当前登录用户。
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)
            .principal(clientRegistrationId).build();
        // 串行执行检查和申请，避免同一实例在令牌过期时并发申请。
        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);
        Assert.state(authorizedClient != null, "Failed to authorize OpenFeign OAuth2 client: " + clientRegistrationId);
        return authorizedClient.getAccessToken().getTokenValue();
    }

}
