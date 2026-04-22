package net.risesoft.y9.configuration.feature.audit;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * 审计日志配置
 *
 * @author shidaobang
 * @date 2026/04/22
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "y9.feature.audit", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class Y9AuditProperties {

    private boolean enabled;

}
