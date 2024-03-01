package net.risesoft.y9.configuration.app.y9itemadmin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Y9ItemAdminProperties {

    private String comment;

    private String freeFlowKey = "ziyouliucheng";

    private String systemCnName;

    private String systemName;

    private Boolean smsSwitch = false;

    private Boolean msgSwitch = false;// 消息提醒开关

    private Boolean weiXinSwitch = false;// 微信提醒开关

    private String weiXinUrl;

}
