package net.risesoft.y9.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9.configuration.common.Y9CommonProperties;
import net.risesoft.y9.configuration.feature.Y9FeatureProperties;

/**
 * 配置属性
 * 
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "y9", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class Y9Properties {

    /** 系统名称 */
    private String systemName;

    /** 系统中文名称 */
    private String systemCnName;

    /** 上下文路径 */
    private String contextPath = "/";

    /** 服务器的内部IP，服务器之间通过内部IP访问，速度比较快 */
    private String internalIp = "/";

    /** 客户端ID */
    private String clientId = "clientid";

    /** 客户端密码 */
    private String clientSecret = "secret";

    /**
     * 通用配置
     */
    @NestedConfigurationProperty
    private Y9CommonProperties common = new Y9CommonProperties();

    /**
     * 特性、功能配置
     */
    @NestedConfigurationProperty
    private Y9FeatureProperties feature = new Y9FeatureProperties();
}
