package net.risesoft.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.configuration.feature.file.Y9FileProperties;

@Configuration
@EnableConfigurationProperties(Y9FileProperties.class)
@Slf4j
public class FileConfiguration {
    public FileConfiguration() {
        LOGGER.info("init FileConfiguration. ");
    }
}
