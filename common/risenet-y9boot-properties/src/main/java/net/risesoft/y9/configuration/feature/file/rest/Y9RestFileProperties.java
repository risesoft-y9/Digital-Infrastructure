package net.risesoft.y9.configuration.feature.file.rest;

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
public class Y9RestFileProperties {

    /**
     * 文件管理url
     */
    private String fileManagerUrl = "http://localhost:8888/fileManager";

}
