package net.risesoft.model.platform.org;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 普通用户密码有效性，仅检查默认密码和有效期，不追溯检查密码强度。 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordValidity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** defaultPasswordChangeRequired 和 expired 均为 false 时为 true */
    private boolean valid;

    /** 已启用默认密码强制修改，且当前密码与租户当前默认密码匹配 */
    private boolean defaultPasswordChangeRequired;

    /** 已超过有效期；启用有效期但无密码时间记录时也视为过期 */
    private boolean expired;
}
