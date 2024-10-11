package net.risesoft.y9public.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.configuration.feature.file.local.Y9LocalProperties;
import net.risesoft.y9public.service.StoreService;
import net.risesoft.y9public.service.impl.LocalStoreServiceImpl;

/**
 * 本地文件存储配置类
 *
 * @author shidaobang
 * @date 2024/04/22
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "y9.feature.file.local.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class LocalStoreConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "y9.feature.file.local")
    public Y9LocalProperties y9LocalProperties() {
        return new Y9LocalProperties();
    }

    @Bean
    public StoreService localStoreService(Y9LocalProperties y9LocalProperties) {
        LOGGER.info("LocalStoreConfiguration init. basePath: {}", y9LocalProperties.getBasePath());
        return new LocalStoreServiceImpl(y9LocalProperties);
    }

}
