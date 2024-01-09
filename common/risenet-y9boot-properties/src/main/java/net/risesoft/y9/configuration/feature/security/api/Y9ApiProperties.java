package net.risesoft.y9.configuration.feature.security.api;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

/**
 * API属性
 *
 * @author shidaobang
 * @date 2024/01/08
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

    /** 令牌自检URI */
    private String tokenIntrospectionUri = "http://localhost:7055/sso/oauth2.0/introspect";

    /** 保护的 url 模式 */
    private List<String> urlPatterns = Arrays.asList("/services/rest/*");
}
