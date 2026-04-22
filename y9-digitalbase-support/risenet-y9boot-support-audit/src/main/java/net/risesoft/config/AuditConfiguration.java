package net.risesoft.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import net.risesoft.y9.configuration.feature.audit.Y9AuditProperties;

/**
 * 审计配置类
 *
 * @author shidaobang
 * @date 2026/04/22
 */
@Configuration
@EnableConfigurationProperties(Y9AuditProperties.class)
public class AuditConfiguration {

}
