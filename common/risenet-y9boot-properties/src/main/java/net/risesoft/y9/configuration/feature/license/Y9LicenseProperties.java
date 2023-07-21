package net.risesoft.y9.configuration.feature.license;

import lombok.Getter;
import lombok.Setter;

/**
 * 授权配置
 *
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
public class Y9LicenseProperties {

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 磁盘中的授权文件
     */
    private String licenseFileOnDisk;

    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 授权字符串
     */
    private String licenseString;

    /**
     * 内部字符串
     */
    private String internalString;

    /**
     * 硬件 id
     */
    private String hardwareIdMethod;

    /**
     * 产品 id
     */
    private String productId;

    /**
     * 产品使用环境
     */
    private String productEdition;

    /**
     * 产品版本
     */
    private String productVersion;
    
}
