package net.risesoft.y9.configuration.feature.idgenerator;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * id 生成器配置
 *
 * @author liansen
 * @date 2022/09/26
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "y9.feature.id-generator", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class Y9IdGeneratorProperties {

    /**
     * 是否启用
     */
    private boolean enabled = false;

    /**
     * zookeeper 地址
     */
    private String zkAddress;

    /**
     * zookeeper 端口
     */
    private int zkPort;
}
