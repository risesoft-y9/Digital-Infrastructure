package org.apereo.cas.web.y9.config;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.support.oauth.OAuth20Constants;
import org.apereo.cas.web.CasWebSecurityConfigurer;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * @author shidaobang
 * @date 2023/09/21
 * @since 9.7.0
 */
@Configuration(proxyBeanMethods = false)
public class Y9CasWebSecurityConfigurer {

    @Bean
    public CasWebSecurityConfigurer<Void> y9ResourceConfigurer() {
        return new CasWebSecurityConfigurer<>() {
            @Override
            public List<String> getIgnoredEndpoints() {
                return List.of("/y9static", "/api");
            }
        };
    }
    
    /*@Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public CasWebSecurityConfigurer<Void> oauth20ProtocolEndpointConfigurer() {
        return new CasWebSecurityConfigurer<>() {
            @Override
            public List<String> getIgnoredEndpoints() {
                return List.of(StringUtils.prependIfMissing(OAuth20Constants.BASE_OAUTH20_URL, "/"), "/y9static", "/api");
            }
        };
    }*/
}
