package net.risesoft.y9.configuration.feature.file.rest;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * rest 方式存储文件配置
 *
 * @author liansen
 * @date 2022/09/26
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "y9.feature.file.rest", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class Y9RestFileProperties {

    /**
     * 是否启用
     */
    private boolean enabled = false;

    /**
     * 文件管理url
     */
    private String fileManagerUrl = "http://localhost:8888/fileManager";

}
