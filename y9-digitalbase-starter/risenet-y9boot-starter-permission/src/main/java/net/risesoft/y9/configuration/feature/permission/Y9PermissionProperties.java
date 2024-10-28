package net.risesoft.y9.configuration.feature.permission;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

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

    @NestedConfigurationProperty
    private HasRolesProperties hasRoles = new HasRolesProperties();

    @NestedConfigurationProperty
    private HasPositionsProperties hasPositions = new HasPositionsProperties();

    @NestedConfigurationProperty
    private HasAuthoritiesProperties hasAuthorities = new HasAuthoritiesProperties();

    @NestedConfigurationProperty
    private IsAnyManagerProperties isAnyManager = new IsAnyManagerProperties();

    public static class HasRolesProperties {
        /**
         * 是否启用
         */
        private boolean enabled;
    }

    public static class HasPositionsProperties {
        /**
         * 是否启用
         */
        private boolean enabled;
    }

    public static class HasAuthoritiesProperties {
        /**
         * 是否启用
         */
        private boolean enabled;
    }

    public static class IsAnyManagerProperties {
        /**
         * 是否启用
         */
        private boolean enabled;
    }

}
