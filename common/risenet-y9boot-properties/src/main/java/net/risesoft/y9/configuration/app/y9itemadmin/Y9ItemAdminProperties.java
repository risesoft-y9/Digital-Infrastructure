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

    private String weekend2WorkingDay;

    private String workHolidayPropFile;

    private String workingDay2Holiday;

    private Boolean smsSwitch = false;

    private Boolean msgSwitch = false;// 消息提醒开关

    private String formType = "2";

    private String tenantId;

    private String workOrderItemId;

    private String workOrderIndex;

    private Boolean weiXinSwitch = false;// 微信提醒开关

    private String weiXinUrl;

}
