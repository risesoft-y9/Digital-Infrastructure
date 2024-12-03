package net.risesoft.y9.configuration.feature.apiacl;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * api访问控制属性
 *
 * @author shidaobang
 * @date 2024/12/02
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "y9.feature.api-access-control", ignoreInvalidFields = true,
    ignoreUnknownFields = true)
public class Y9ApiAccessControlProperties {

    private List<String> urlPatterns = List.of("/services/rest/*");

    @NestedConfigurationProperty
    private BlackListProperties blackList = new BlackListProperties();

    @NestedConfigurationProperty
    private WhiteListProperties whiteList = new WhiteListProperties();

    @NestedConfigurationProperty
    private SignProperties sign = new SignProperties();

    @NestedConfigurationProperty
    private TokenProperties token = new TokenProperties();

    @Getter
    @Setter
    public static class TokenProperties {

        private boolean enabled = false;

        /** 客户端ID */
        private String clientId = "clientid";

        /** 客户端密码 */
        private String clientSecret = "secret";

        /** 令牌自检URI */
        private String tokenIntrospectionUri = "http://localhost:7055/sso/oauth2.0/introspect";

        /** 保护的 url 模式 */
        private List<String> urlPatterns = Arrays.asList("/services/rest/*");

    }

    @Getter
    @Setter
    public static class BlackListProperties {

        private boolean enabled = false;

    }

    @Getter
    @Setter
    public static class WhiteListProperties {

        private boolean enabled = false;

    }

    @Getter
    @Setter
    public static class SignProperties {

        private boolean enabled = false;

    }
}
