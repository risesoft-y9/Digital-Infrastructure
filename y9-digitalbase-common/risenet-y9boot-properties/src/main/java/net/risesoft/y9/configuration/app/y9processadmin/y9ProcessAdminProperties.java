package net.risesoft.y9.configuration.app.y9processadmin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class y9ProcessAdminProperties {

    private String systemName = "processAdmin";

    private String systemCnName = "流程管理器";

    private String baseUrl = "http://127.0.0.1:7055/processAdmin";

    private String freeFlowKey = "ziyouliucheng";

    private Boolean todoSwitch = false;// 统一待办开关

    private Boolean dataCenterSwitch = false;// 数据中心开关

    private Boolean weiXinSwitch = false;// 微信提醒开关

    private Boolean cooperationStateSwitch = false;// 协作状态开关

    private Boolean msgSwitch = false;// 消息提醒开关

    private Boolean entrustSwitch = false;

    private Boolean messagePushSwitch = false;

    private Boolean pushSwitch = false;

    private Boolean smsSwitch = false;

    private String weiXinUrl;

    private Boolean dzxhSmsSwitch = false;// 地灾短信开关

    private String dzxhTenantId;// 地灾租户id

}
