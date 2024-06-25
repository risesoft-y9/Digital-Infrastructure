package net.risesoft.y9.configuration.feature.multitenant;

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
public class Y9MultiTenantProperties {

    private MultiTenantEventSourceEnum eventSource = MultiTenantEventSourceEnum.KAFKA;

    @Getter
    @AllArgsConstructor
    public enum MultiTenantEventSourceEnum {
        DB, KAFKA;
    }

}
