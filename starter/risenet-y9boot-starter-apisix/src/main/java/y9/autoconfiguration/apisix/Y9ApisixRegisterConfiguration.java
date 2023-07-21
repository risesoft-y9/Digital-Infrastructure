package y9.autoconfiguration.apisix;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;

import y9.apisix.OnY9ApisixRegisterApplicationReady;
import y9.apisix.endpoint.Y9ApisixEndpoint;
import y9.apisix.register.Y9RegisterByApisixRestApi;

@Configuration
@EnableConfigurationProperties(Y9Properties.class)
@ConditionalOnProperty(name = "y9.feature.apisix.enabled", havingValue = "true")
public class Y9ApisixRegisterConfiguration {
    
    @Bean
    @DependsOn("y9Context")
    public Y9RegisterByApisixRestApi y9RegisterByApisixRestApi() {
        return new Y9RegisterByApisixRestApi();
    }
    
    @Bean
    @DependsOn("y9Context")
    public OnY9ApisixRegisterApplicationReady onY9ApisixRegisterApplicationReady(Y9RegisterByApisixRestApi y9RegisterByApisixRestApi) {
        return new OnY9ApisixRegisterApplicationReady(y9RegisterByApisixRestApi);
    }

    @Bean
    public Y9ApisixEndpoint y9ApisixEndpoint(Y9RegisterByApisixRestApi y9RegisterByApisixRestApi) {
        return new Y9ApisixEndpoint(y9RegisterByApisixRestApi);
    }

    @Bean
    @ConditionalOnMissingBean(value = Y9Context.class)
    public Y9Context y9Context() {
        return new Y9Context();
    }
}
