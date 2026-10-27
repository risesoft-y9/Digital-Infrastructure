package net.risesoft.y9.configuration.app.y9platform.tenantdefault;

import lombok.Getter;
import lombok.Setter;

/**
 * 租户默认设置，租户未单独配置时使用。
 */
@Getter
@Setter
public class TenantDefaultProperties {

    public static final String USER_PASSWORD_DEFAULT = "Risesoft@2022";
    public static final String POSITION_NAME_TEMPLATE_DEFAULT =
        "#jobName.equals('无') ? #personNames : #personNames+ '（' + #jobName  + '）'";

    /**
     * 用户默认密码
     */
    private String userDefaultPassword = USER_PASSWORD_DEFAULT;

    /** 普通用户默认密码是否强制修改 */
    private boolean userDefaultPasswordChangeRequired = false;

    /** 普通用户密码有效期（天），0 为永久 */
    private int userPasswordMaxAge = 0;

    /** 普通用户密码最小长度，配置范围为 6～32 */
    private int userPasswordMinLength = 6;

    private boolean userPasswordRequireUppercase = false;

    private boolean userPasswordRequireLowercase = false;

    private boolean userPasswordRequireDigit = false;

    /** 必须包含英文标点符号 */
    private boolean userPasswordRequireSymbol = false;

    /**
     * 岗位名称格式，#jobName 会替换为职位名，#personNames 会替换为人员名称。<br>
     * 默认格式示例：张三（总经理），职位名为“无”时只显示人员名称。
     */
    private String positionNameTemplate = POSITION_NAME_TEMPLATE_DEFAULT;
}
