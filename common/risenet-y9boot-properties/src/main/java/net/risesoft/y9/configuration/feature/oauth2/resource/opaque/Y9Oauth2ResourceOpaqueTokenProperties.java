package net.risesoft.y9.configuration.feature.oauth2.resource.opaque;

import lombok.Getter;
import lombok.Setter;

/**
 * oauth2 资源服务器 OpaqueToken 配置
 *
 * @author shidaobang
 * @date 2022/09/30
 */
@Getter
@Setter
public class Y9Oauth2ResourceOpaqueTokenProperties {

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 客户密钥
     */
    private String clientSecret;

    /**
     * 自省检测 uri，可以查询给定访问令牌的状态
     */
    private String introspectionUri;

    /**
     * 用户信息 uri
     */
    private String profileUri;
    
}
