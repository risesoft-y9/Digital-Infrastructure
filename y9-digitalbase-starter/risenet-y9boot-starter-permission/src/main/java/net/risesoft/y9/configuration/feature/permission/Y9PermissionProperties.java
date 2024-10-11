package net.risesoft.y9.configuration.feature.permission;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * 权限属性
 *
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "y9.feature.permission", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class Y9PermissionProperties {

    /**
     * 是否启用
     */
    private boolean enabled;
}
