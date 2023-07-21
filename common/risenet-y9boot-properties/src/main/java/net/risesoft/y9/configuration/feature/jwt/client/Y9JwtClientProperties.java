package net.risesoft.y9.configuration.feature.jwt.client;

import lombok.Getter;
import lombok.Setter;

/**
 * jwt 客户端配置
 *
 * @author liansen
 * @date 2022/09/26
 */
@Getter
@Setter
public class Y9JwtClientProperties {
    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * sso服务url
     */
    private String ssoServerUrl;

    /**
     * 服务器名称
     */
    private String serverName;

    /**
     * 解密密钥
     */
    private String decryptionKey;

    /**
     * 加密密钥
     */
    private String signKey;

    /**
     * 受保护 url 正则
     */
    private String protectedUrlPatterns;

}
