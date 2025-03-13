package net.risesoft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.risesoft.util.EsIndexDate;

@Configuration
public class EsLogConfig {

    @Bean
    public EsIndexDate esIndexDate() {
        return new EsIndexDate();
    }

}
