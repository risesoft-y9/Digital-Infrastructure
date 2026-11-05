package y9.autoconfiguration;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ClientCredentialsOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.risesoft.y9.configuration.feature.openfeign.Y9SignProperties;
import net.risesoft.y9.configuration.feature.openfeign.Y9TokenProperties;

import y9.support.SignInterceptor;
import y9.support.TokenInterceptor;
import y9.support.Y9ErrorDecoder;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;

/**
 * open feign 自动配置类
 *
 * @author dingzhaojun
 * @date 2022/02/15
 */
@Configuration(proxyBeanMethods = false)
@EnableFeignClients("y9.client")
@EnableConfigurationProperties({Y9TokenProperties.class, Y9SignProperties.class})
public class Y9FeignConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "errorDecoder")
    public ErrorDecoder errorDecoder(ObjectMapper objectMapper) {
        return new Y9ErrorDecoder(objectMapper);
    }

    @Bean
    @ConditionalOnProperty(name = "y9.feature.openfeign.token.enabled", havingValue = "true", matchIfMissing = false)
    public RequestInterceptor tokenInterceptor(Y9TokenProperties y9TokenProperties) {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("y9-openfeign")
            .clientId(y9TokenProperties.getClientId())
            .clientSecret(y9TokenProperties.getClientSecret())
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .tokenUri(y9TokenProperties.getTokenUrl())
            .build();
        InMemoryClientRegistrationRepository registrations =
            new InMemoryClientRegistrationRepository(clientRegistration);
        // 每个实例独立保存令牌；不注册为全局客户端仓库，避免影响应用已有的 OAuth2 配置。
        InMemoryOAuth2AuthorizedClientService authorizedClients =
            new InMemoryOAuth2AuthorizedClientService(registrations);
        AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
            new AuthorizedClientServiceOAuth2AuthorizedClientManager(registrations, authorizedClients);
        ClientCredentialsOAuth2AuthorizedClientProvider provider =
            new ClientCredentialsOAuth2AuthorizedClientProvider();
        provider.setAccessTokenResponseClient(tokenResponseClient());
        manager.setAuthorizedClientProvider(provider);
        return new TokenInterceptor(clientRegistration.getRegistrationId(), manager);
    }

    private DefaultClientCredentialsTokenResponseClient tokenResponseClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(10000);
        RestTemplate restTemplate = new RestTemplate(
            Arrays.asList(new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()));
        restTemplate.setRequestFactory(requestFactory);
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        DefaultClientCredentialsTokenResponseClient tokenResponseClient =
            new DefaultClientCredentialsTokenResponseClient();
        tokenResponseClient.setRestOperations(restTemplate);
        return tokenResponseClient;
    }

    @Bean
    @ConditionalOnProperty(name = "y9.feature.openfeign.sign.enabled", havingValue = "true", matchIfMissing = false)
    public RequestInterceptor signInterceptor(Y9SignProperties y9SignProperties) {
        return new SignInterceptor(y9SignProperties);
    }

}
