package net.risesoft.model.platform.org;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 密码强度要求。 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordStrength implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 密码最小长度，配置范围为 6～32。 */
    private Integer minLength;

    /** 密码是否必须包含大写英文字母 A～Z。 */
    private boolean requireUppercase;

    /** 密码是否必须包含小写英文字母 a～z。 */
    private boolean requireLowercase;

    /** 密码是否必须包含数字 0～9。 */
    private boolean requireDigit;

    /** 密码是否必须包含 ASCII 英文标点符号，空格和中文标点不计入。 */
    private boolean requireSymbol;
}
