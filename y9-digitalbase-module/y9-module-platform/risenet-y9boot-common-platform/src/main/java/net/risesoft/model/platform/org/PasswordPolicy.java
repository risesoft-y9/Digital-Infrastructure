package net.risesoft.model.platform.org;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 普通用户所属租户的密码策略，供业务系统展示及校验提示使用。 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordPolicy implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 是否启用默认密码强制修改 */
    private boolean defaultPasswordChangeRequired;

    /** 有效期，单位天，0 为永久 */
    private int maxAge;

    /** 最小长度，默认为 6，配置范围为 6～32 */
    private Integer minLength;

    private boolean requireUppercase;

    private boolean requireLowercase;

    private boolean requireDigit;

    /** 是否必须包含 ASCII 英文标点符号 */
    private boolean requireSymbol;
}
