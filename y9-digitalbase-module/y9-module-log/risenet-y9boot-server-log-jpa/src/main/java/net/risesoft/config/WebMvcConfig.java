package net.risesoft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.risesoft.y9.Y9Context;

@Configuration
public class WebMvcConfig {

    @Bean
    public Y9Context y9Context() {
        return new Y9Context();
    }

}
