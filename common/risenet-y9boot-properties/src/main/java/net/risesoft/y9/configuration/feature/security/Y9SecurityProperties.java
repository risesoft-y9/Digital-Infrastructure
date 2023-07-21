package net.risesoft.y9.configuration.feature.security;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9.configuration.feature.security.cors.Y9CorsProperties;
import net.risesoft.y9.configuration.feature.security.csrf.Y9CSRFProperties;
import net.risesoft.y9.configuration.feature.security.xss.Y9XSSProperties;

/**
 * 安全配置
 *
 * @author shidaobang
 * @date 2022/09/30
 */
@Getter
@Setter
public class Y9SecurityProperties {

    /**
     * 是否开启防御
     */
    private boolean enabled = true;

    @NestedConfigurationProperty
    private Y9XSSProperties xss = new Y9XSSProperties();

    @NestedConfigurationProperty
    private Y9CSRFProperties csrf = new Y9CSRFProperties();

    @NestedConfigurationProperty
    private Y9CorsProperties cors = new Y9CorsProperties();
    
}
