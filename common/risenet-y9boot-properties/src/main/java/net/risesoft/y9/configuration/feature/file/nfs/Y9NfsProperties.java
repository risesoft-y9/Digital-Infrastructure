package net.risesoft.y9.configuration.feature.file.nfs;

import lombok.Getter;
import lombok.Setter;

/**
 * nfs 存储文件配置
 *
 * @author liansen
 * @date 2022/09/26
 */
@Getter
@Setter
public class Y9NfsProperties {
    /**
     * 主机名
     */
    private String hostname;
    /**
     * 导出路径
     */
    private String exportedPath;
}
