package net.risesoft.y9.configuration.app.y9platform.tenantdefault;

import lombok.Getter;
import lombok.Setter;

/**
 * 租户默认设置，租户未单独配置时使用。
 */
@Getter
@Setter
public class TenantDefaultProperties {

    /** 未配置默认密码时使用的内置值。 */
    public static final String DEFAULT_PASSWORD = "Risesoft@2022";

    /** 未配置岗位名称格式时使用的内置 SpEL 模板。 */
    public static final String POSITION_NAME_TEMPLATE_DEFAULT =
        "#jobName.equals('无') ? #personNames : #personNames+ '（' + #jobName  + '）'";

    /**
     * 创建或重置账号时使用的默认密码。
     */
    private String defaultPassword = DEFAULT_PASSWORD;

    /** 人员是否要求修改与租户当前默认密码相同的密码，默认不要求。 */
    private boolean defaultPasswordChangeRequired = false;

    /** 人员密码有效期，单位天，配置范围为 0～365，0 为永久。 */
    private int passwordMaxAge = 0;

    /** 密码最小长度，配置范围为 6～32 */
    private int passwordMinLength = 6;

    /** 密码是否必须包含大写英文字母 A～Z，默认不要求。 */
    private boolean passwordRequireUppercase = false;

    /** 密码是否必须包含小写英文字母 a～z，默认不要求。 */
    private boolean passwordRequireLowercase = false;

    /** 密码是否必须包含数字 0～9，默认不要求。 */
    private boolean passwordRequireDigit = false;

    /** 密码是否必须包含 ASCII 英文标点符号，空格和中文标点不计入，默认不要求。 */
    private boolean passwordRequireSymbol = false;

    /**
     * 岗位名称格式，#jobName 会替换为职位名，#personNames 会替换为人员名称。<br>
     * 默认格式示例：张三（总经理），职位名为“无”时只显示人员名称。
     */
    private String positionNameTemplate = POSITION_NAME_TEMPLATE_DEFAULT;
}
