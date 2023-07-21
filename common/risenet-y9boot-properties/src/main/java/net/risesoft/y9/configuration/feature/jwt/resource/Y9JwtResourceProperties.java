package net.risesoft.y9.configuration.feature.jwt.resource;

import lombok.Getter;
import lombok.Setter;

/**
 * jwt 资源服务器配置
 *
 * @author shidaobang
 * @date 2022/09/30
 */
@Getter
@Setter
public class Y9JwtResourceProperties {

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 受保护url模式
     */
    private String protectedUrlPatterns;

    /**
     * 解密密钥
     */
    private String decryptionKey;

    /**
     * 加密密钥
     */
    private String signKey;

}
