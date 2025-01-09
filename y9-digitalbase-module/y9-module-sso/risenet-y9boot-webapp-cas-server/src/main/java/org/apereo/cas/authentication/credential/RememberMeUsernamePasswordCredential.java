package org.apereo.cas.authentication.credential;

import lombok.*;
import org.apereo.cas.authentication.RememberMeCredential;

/**
 * Handles both remember me services and username and password.
 *
 * @author Scott Battaglia
 * @since 3.2.1
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RememberMeUsernamePasswordCredential extends UsernamePasswordCredential implements RememberMeCredential {
    private static final long serialVersionUID = -6710007659431302397L;

    private boolean rememberMe;

    // person实体类的许多属性都可以作为登录时的凭证，如身份证、邮箱、用户名、CA序列号、手机号、社保号、医保号等
    // 这里保存的是属性名称，如loginName、mobile、caid、email等
    private String loginType = "loginName";

    private String tenantShortName;

    private String deptId;

    private String positionId;

    // noLoginScreen = "true"表示不弹出登录窗口，直接验证login接口接收的参数。
    private String noLoginScreen = "false";

    private String systemName;

    private String screenDimension;

    private String userAgent;

    private String clientIp;

    private String clientMac;

    private String clientDiskId;

    private String clientHostName;

    private String pwdEcodeType;

}
