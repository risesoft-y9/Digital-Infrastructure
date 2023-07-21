package net.risesoft.y9.configuration.app.y9soa;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Y9SoaProperties {

    private String systemName = "risesoa";
    private String sendMessageIpPrefix = "10.169.17";
    private String allowedUsers = "systemadmin,admin,huangguoliang";
    private String topGuid = "{00000000-0000-0000-0000-000000000000}";
    private String defaultTenant;
    private String defaultUser;
    private String defaultPassword;
    private String proxyUrl = "http://yun.szlh.gov.cn/platform/admin/index";

    private String emailUsername = "yjpt@szlh.gov.cn";
    private String emailPassword = "lhq123456";
    private String emailSmtpHost = "mail.sz.gov.cn";

    private String indexView = "index";

}
