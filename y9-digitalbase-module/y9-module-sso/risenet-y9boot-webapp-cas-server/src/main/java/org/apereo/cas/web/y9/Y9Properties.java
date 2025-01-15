package org.apereo.cas.web.y9;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 配置属性
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/09/28
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "y9", ignoreInvalidFields = true)
@EnableConfigurationProperties(Y9Properties.class)
public class Y9Properties {
    /** 日志保存目标：kafka、jpa */
    private String loginInfoSaveTarget = "jpa";

    /** 服务器内部IP地址 */
    private String internalIp = "192.168.x.x,10.0.x.x,172.20.x.x";

    /** 非对称公钥 */
    private String encryptionRsaPublicKey = "";

    /** 非对称私钥 */
    private String encryptionRsaPrivateKey = "";
}
