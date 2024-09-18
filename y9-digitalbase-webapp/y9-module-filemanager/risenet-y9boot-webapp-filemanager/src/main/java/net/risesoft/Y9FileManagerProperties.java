package net.risesoft;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Y9FileManagerProperties
 *
 * @author shidaobang
 * @date 2022/5/31
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "y9.app.file-manager", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class Y9FileManagerProperties {

    /**
     * 文件服务器的根目录
     */
    private String fileRoot = "d:/y9config/y9filestore";

}
