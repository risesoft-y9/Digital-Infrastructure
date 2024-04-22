package net.risesoft.y9.configuration.feature.file.local;

import lombok.Getter;
import lombok.Setter;

/**
 * 本地文件存储属性
 *
 * @author shidaobang
 * @date 2024/04/22
 */
@Getter
@Setter
public class Y9LocalProperties {

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 本地文件存储基础路径
     */
    private String basePath = "/software";

}
