package net.risesoft.y9.configuration.feature;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9.configuration.feature.jwt.Y9JwtProperties;
import net.risesoft.y9.configuration.feature.log.Y9LogProperties;
import net.risesoft.y9.configuration.feature.oauth2.Y9Oauth2Properties;
import net.risesoft.y9.configuration.feature.sso.Y9SsoClientProperties;

/**
 * 特性、功能配置
 *
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
public class Y9FeatureProperties {

    /**
     * 日志
     */
    @NestedConfigurationProperty
    private Y9LogProperties log = new Y9LogProperties();

    /**
     * sso
     */
    @NestedConfigurationProperty
    private Y9SsoClientProperties sso = new Y9SsoClientProperties();

    /**
     * oauth2
     */
    @NestedConfigurationProperty
    private Y9Oauth2Properties oauth2 = new Y9Oauth2Properties();

    /**
     * jwt
     */
    @NestedConfigurationProperty
    private Y9JwtProperties jwt = new Y9JwtProperties();

}
