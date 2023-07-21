package net.risesoft.y9.configuration.feature.jwt;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9.configuration.feature.jwt.client.Y9JwtClientProperties;
import net.risesoft.y9.configuration.feature.jwt.resource.Y9JwtResourceProperties;

/**
 * jwt 配置
 *
 * @author shidaobang
 * @date 2022/09/30
 */
@Getter
@Setter
public class Y9JwtProperties {

    /**
     * 客户端
     */
    @NestedConfigurationProperty
    private Y9JwtClientProperties client = new Y9JwtClientProperties();

    /**
     * 资源
     */
    @NestedConfigurationProperty
    private Y9JwtResourceProperties resource = new Y9JwtResourceProperties();

}
