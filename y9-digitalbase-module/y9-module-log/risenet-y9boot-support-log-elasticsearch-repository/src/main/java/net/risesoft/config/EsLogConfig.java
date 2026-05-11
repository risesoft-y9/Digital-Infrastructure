package net.risesoft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.risesoft.util.EsIndexYear;

@Configuration
public class EsLogConfig {

    @Bean
    public EsIndexYear esIndexYear() {
        return new EsIndexYear();
    }
}
