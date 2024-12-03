package net.risesoft.y9.configuration.feature.openfeign;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * 接口调用(API 属性)
 *
 * @author shidaobang
 * @date 2024/01/09
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "y9.feature.openfeign.token", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class Y9TokenProperties {

    /** 接口调用令牌是否为接口必须的参数 */
    private boolean enabled = false;

    /** 客户端ID */
    private String clientId = "clientid";

    /** 客户端密码 */
    private String clientSecret = "secret";

    /** 获取令牌URL */
    private String tokenUrl = "http://localhost:7055/sso/oauth2.0/accessToken";
}
