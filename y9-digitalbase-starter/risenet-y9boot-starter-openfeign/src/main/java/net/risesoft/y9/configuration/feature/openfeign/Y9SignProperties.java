package net.risesoft.y9.configuration.feature.openfeign;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * 签名属性
 *
 * @author shidaobang
 * @date 2024/12/02
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "y9.feature.openfeign.sign", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class Y9SignProperties {

    private boolean enabled = false;

    private String appId = "";

    private String appSecret = "";

}
