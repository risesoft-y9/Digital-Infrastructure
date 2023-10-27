package org.apereo.cas.web.y9.config;

import java.util.List;

import org.apereo.cas.web.CasWebSecurityConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shidaobang
 * @date 2023/09/21
 * @since 9.7.0
 */
@Configuration
public class Y9WebEndpointConfiguration {

    @Bean
    public CasWebSecurityConfigurer<Void> y9ResourceConfigurer() {
        return new CasWebSecurityConfigurer<>() {
            @Override
            public List<String> getIgnoredEndpoints() {
                return List.of("/y9static", "/api");
            }
        };
    }
}
