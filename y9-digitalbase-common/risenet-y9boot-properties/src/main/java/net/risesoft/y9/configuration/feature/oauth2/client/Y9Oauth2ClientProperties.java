package net.risesoft.y9.configuration.feature.oauth2.client;

import lombok.Getter;
import lombok.Setter;

/**
 * oauth2 客户端配置
 *
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
public class Y9Oauth2ClientProperties {
    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 客户端秘钥
     */
    private String clientSecret;

    /**
     * 范围
     */
    private String scope;

    /**
     * 响应类型
     */
    private String responseType;

    /**
     * pkce
     */
    private String pkce;

    /**
     * 授权 uri
     */
    private String authorizationUri;

    /**
     * 令牌 uri
     */
    private String accessTokenUri;

    /**
     * 获取用户信息 uri
     */
    private String profileUri;

    /**
     * 根 uri
     */
    private String rootUri;

    /**
     * 受保护的 url 正则
     */
    private String protectedUrlPatterns;

}
