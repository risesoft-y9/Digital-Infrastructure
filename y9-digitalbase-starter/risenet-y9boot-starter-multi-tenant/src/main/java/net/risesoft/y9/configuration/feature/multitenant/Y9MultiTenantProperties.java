package net.risesoft.y9.configuration.feature.multitenant;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 多租户配置属性
 *
 * @author shidaobang
 * @date 2024/05/13
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "y9.feature.multi-tenant", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class Y9MultiTenantProperties {

    private MultiTenantEventSourceEnum eventSource = MultiTenantEventSourceEnum.KAFKA;

    @Getter
    @AllArgsConstructor
    public enum MultiTenantEventSourceEnum {
        DB, KAFKA
    }

}
