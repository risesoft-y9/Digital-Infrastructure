package net.risesoft.y9.configuration.app.y9useronline;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Y9UserOnlineProperties {

    /** console,mongo,redis */
    private String userOnlineSaveTarget = "console";

    /** 执行日志保存时间间隔 */
    private String ttl;

    private String indexView = "index";

    /** 同步执行服务器IP */
    private String scheduleServerIp;

    /** 租户id */
    private String tenantId;

}
