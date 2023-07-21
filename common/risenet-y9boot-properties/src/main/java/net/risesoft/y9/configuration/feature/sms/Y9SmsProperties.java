package net.risesoft.y9.configuration.feature.sms;

import lombok.Getter;
import lombok.Setter;

/**
 * 短信配置
 *
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
public class Y9SmsProperties {

    /**
     * jtds 驱动
     */
    private String jtdsDriver = "net.sourceforge.jtds.jdbc.Driver";

    /**
     * jtds url
     */
    private String jtdsUrl = "jdbc:jtds:sqlserver://10.16.1.8:1433;databaseName=DB_CustomSMS";

    /**
     * jtds 用户名
     */
    private String jtdsUsername = "jcjsms";

    /**
     * jtds 密码
     */
    private String jtdsPassword = "4l7xUhYlg8j4GlYUiLclkE2uTRk0fp71";
    
}
