package net.risesoft.y9.configuration.app.y9cms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Y9CmsProperties {

    private String systemName = "risecms7";

    private String userId;

    private String tenantId;

    private String zwdtUrl;
    private String tzggUrl;
    private String sqdtUrl;

    /**
     * 是否保存至数据中心
     */
    private Boolean dataCenterSwitch = false;

}
