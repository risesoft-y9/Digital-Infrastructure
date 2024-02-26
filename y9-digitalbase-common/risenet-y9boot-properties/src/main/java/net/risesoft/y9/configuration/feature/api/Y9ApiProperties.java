package net.risesoft.y9.configuration.feature.api;

import lombok.Getter;
import lombok.Setter;

/**
 * API 属性
 *
 * @author shidaobang
 * @date 2024/01/09
 */
@Getter
@Setter
public class Y9ApiProperties {

    /** 接口调用令牌是否为接口必须的参数 */
    private boolean tokenRequired = false;

    /** 客户端ID */
    private String clientId = "clientid";

    /** 客户端密码 */
    private String clientSecret = "secret";

    /** 获取令牌URL */
    private String tokenUrl = "http://localhost:7055/sso/oauth2.0/accessToken";
}
