package net.risesoft.y9.configuration.feature.file.wps;

import lombok.Getter;
import lombok.Setter;

/**
 * wps 云文档配置
 * @author liansen
 * @date 2022/09/26
 */
@Getter
@Setter
public class WPSProperties {

    /**
     * yun路径
     */
    private String yunUrl = "http://yun.test.cn";

    /**
     * 图表url
     */
    private String graphUrl = "http://yun.test.cn/graph";

    /**
     * 应用程序id
     */
    private String appId = "82eac9cb-3c1e-4ead-0000-000000000002";

    /**
     * 应用程序秘钥
     */
    private String appSecret = "0vWYUIerHYir";
    
}
