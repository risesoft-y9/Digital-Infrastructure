package net.risesoft.controller.setting.vo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 租户设置
 *
 * @author shidaobang
 * @date 2024/04/02
 */
@Getter
@Setter
public class TenantSettingVO implements Serializable {

    private static final long serialVersionUID = 6275047619882074132L;

    /** 用户默认密码 */
    private String userDefaultPassword;
}
