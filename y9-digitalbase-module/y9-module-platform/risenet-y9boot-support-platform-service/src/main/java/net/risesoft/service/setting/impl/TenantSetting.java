package net.risesoft.service.setting.impl;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9.configuration.app.y9platform.Y9PlatformProperties;

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
    private String userDefaultPassword = Y9PlatformProperties.USER_PASSWORD_DEFAULT;

    /** 岗位名称格式 */
    private String positionNameTemplate = Y9PlatformProperties.POSITION_NAME_TEMPLATE_DEFAULT;

    @Override
    public String getPrefix() {
        return "y9.app.platform.";
    }
}
