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

    /** 用户默认密码 */
    private String userDefaultPassword;

    /** 普通用户默认密码是否强制修改 */
    private boolean userDefaultPasswordChangeRequired;

    /** 普通用户密码有效期（天），0 为永久 */
    private int userPasswordMaxAge;

    /** 普通用户密码最小长度，配置范围为 6～32 */
    private int userPasswordMinLength;

    private boolean userPasswordRequireUppercase;

    private boolean userPasswordRequireLowercase;

    private boolean userPasswordRequireDigit;

    /** 必须包含英文标点符号 */
    private boolean userPasswordRequireSymbol;

    /** 岗位名称格式 */
    private String positionNameTemplate;

    public TenantSetting() {
        this(new TenantDefaultProperties());
    }

    public TenantSetting(TenantDefaultProperties tenantDefault) {
        this.userDefaultPassword = tenantDefault.getUserDefaultPassword();
        this.userDefaultPasswordChangeRequired = tenantDefault.isUserDefaultPasswordChangeRequired();
        this.userPasswordMaxAge = tenantDefault.getUserPasswordMaxAge();
        this.userPasswordMinLength = tenantDefault.getUserPasswordMinLength();
        this.userPasswordRequireUppercase = tenantDefault.isUserPasswordRequireUppercase();
        this.userPasswordRequireLowercase = tenantDefault.isUserPasswordRequireLowercase();
        this.userPasswordRequireDigit = tenantDefault.isUserPasswordRequireDigit();
        this.userPasswordRequireSymbol = tenantDefault.isUserPasswordRequireSymbol();
        this.positionNameTemplate = tenantDefault.getPositionNameTemplate();
    }

    @Override
    public String getPrefix() {
        return "y9.app.platform.tenant-default.";
    }
}
