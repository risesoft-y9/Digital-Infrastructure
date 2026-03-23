package net.risesoft.y9.configuration.feature.systemregistration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * 系统在数字底座中注册配置属性
 *
 * @author shidaobang
 * @since 9.6.10
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "y9.feature.system-registration", ignoreInvalidFields = true,
    ignoreUnknownFields = true)
public class Y9SystemRegistrationProperties {

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 系统 JSON 位置
     */
    private String location = "classpath:system.json";
}
