package net.risesoft.y9public.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.configuration.feature.file.rest.Y9RestFileProperties;
import net.risesoft.y9public.service.StoreService;
import net.risesoft.y9public.service.impl.RestStoreServiceImpl;

@Configuration
@ConditionalOnProperty(name = "y9.feature.file.rest.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class RestStoreConfiguration {

    @Bean
    public Y9RestFileProperties y9RestFileProperties() {
        return new Y9RestFileProperties();
    }

    @Bean
    public StoreService restStoreService(Y9RestFileProperties y9RestFileProperties) {
        LOGGER.info("RestStoreConfiguration init. fileManagerUrl:{}", y9RestFileProperties.getFileManagerUrl());
        return new RestStoreServiceImpl(y9RestFileProperties);
    }

}
