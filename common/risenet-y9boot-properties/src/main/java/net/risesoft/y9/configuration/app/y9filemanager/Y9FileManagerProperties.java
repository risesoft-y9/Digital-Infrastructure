package net.risesoft.y9.configuration.app.y9filemanager;

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
public class Y9FileManagerProperties {

    /**
     * 文件服务器的根目录
     */
    private String fileRoot = "d:/y9config/y9filestore";

}
