package net.risesoft.y9.configuration.app.y9flowable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Y9FlowableProperties {

    private String systemName;

    private String systemCnName;

    private String tenantId = "c425281829dc4d4496ddddf7fc0198d0";
    private Integer formType = 2;
    private String systemWorkOrderKey = "systemWorkOrder";
    private String workOrderItemId;
    private String dzxhTenantId;
    private String dzxhLyspItemId;
    private String dzxhLyspFormId;
    private String emailUrl = "https://www.youshengyun.com/sms/emailIndex";

}
