package net.risesoft.service.setting.impl;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9.configuration.app.y9platform.tenantdefault.TenantDefaultProperties;

/**
 * 租户设置
 *
 * @author shidaobang
 * @date 2024/04/02
 */
@Getter
@Setter
public class TenantSetting extends AbstractSetting implements Serializable {

    private static final long serialVersionUID = 6275047619882074132L;

    /** 创建或重置账号时使用的默认密码。 */
    private String defaultPassword;

    /** 人员是否要求修改与租户当前默认密码相同的密码。 */
    private boolean defaultPasswordChangeRequired;

    /** 人员密码有效期，单位天，配置范围为 0～365，0 为永久。 */
    private int passwordMaxAge;

    /** 密码最小长度，配置范围为 6～32 */
    private int passwordMinLength;

    /** 密码是否必须包含大写英文字母 A～Z。 */
    private boolean passwordRequireUppercase;

    /** 密码是否必须包含小写英文字母 a～z。 */
    private boolean passwordRequireLowercase;

    /** 密码是否必须包含数字 0～9。 */
    private boolean passwordRequireDigit;

    /** 密码是否必须包含 ASCII 英文标点符号，空格和中文标点不计入。 */
    private boolean passwordRequireSymbol;

    /** 岗位名称格式 */
    private String positionNameTemplate;

    /** 使用内置默认配置初始化租户设置。 */
    public TenantSetting() {
        this(new TenantDefaultProperties());
    }

    /**
     * 使用指定默认配置初始化租户设置，作为读取租户已保存设置时的兜底值。
     *
     * @param tenantDefault 租户默认配置
     */
    public TenantSetting(TenantDefaultProperties tenantDefault) {
        this.defaultPassword = tenantDefault.getDefaultPassword();
        this.defaultPasswordChangeRequired = tenantDefault.isDefaultPasswordChangeRequired();
        this.passwordMaxAge = tenantDefault.getPasswordMaxAge();
        this.passwordMinLength = tenantDefault.getPasswordMinLength();
        this.passwordRequireUppercase = tenantDefault.isPasswordRequireUppercase();
        this.passwordRequireLowercase = tenantDefault.isPasswordRequireLowercase();
        this.passwordRequireDigit = tenantDefault.isPasswordRequireDigit();
        this.passwordRequireSymbol = tenantDefault.isPasswordRequireSymbol();
        this.positionNameTemplate = tenantDefault.getPositionNameTemplate();
    }

    @Override
    public String getPrefix() {
        return "y9.app.platform.tenant-default.";
    }
}
