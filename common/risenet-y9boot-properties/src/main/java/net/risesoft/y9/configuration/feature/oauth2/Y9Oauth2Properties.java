package net.risesoft.y9.configuration.feature.oauth2;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9.configuration.feature.oauth2.client.Y9Oauth2ClientProperties;
import net.risesoft.y9.configuration.feature.oauth2.resource.Y9Oauth2ResourceProperties;

/**
 * oauth2 配置
 * 
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
public class Y9Oauth2Properties {

    @NestedConfigurationProperty
    private Y9Oauth2ClientProperties client = new Y9Oauth2ClientProperties();

    @NestedConfigurationProperty
    private Y9Oauth2ResourceProperties resource = new Y9Oauth2ResourceProperties();
}
