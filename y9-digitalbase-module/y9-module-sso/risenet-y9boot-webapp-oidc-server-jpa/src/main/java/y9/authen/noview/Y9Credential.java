package y9.authen.noview;

import org.apereo.cas.authentication.credential.AbstractCredential;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Y9Credential extends AbstractCredential {
	private static final long serialVersionUID = -6710007659431302397L;

	//根据loginType，可以保存loginName、mobile、caid、email等
	private String username;

	private boolean rememberMe;

	// person实体类的许多属性都可以作为登录时的凭证，如身份证、邮箱、用户名、CA序列号、手机号、社保号、医保号等
	// 这里保存的是属性名称，如loginName、mobile、caid、email等
	private String loginType = "loginName";

	private String tenantShortName;

	//切换部门时，可以提交本参数
	private String deptId;

	//切换岗位时，可以提交本参数
	private String positionId;

	// noLoginScreen = "true"表示不弹出登录窗口，直接验证login接口接收的参数。
	private String noLoginScreen = "true";

	private String systemName;

	private String screenDimension;

	private String userAgent;

	private String clientIp;

	private String clientMac;

	private String clientDiskId;

	private String clientHostName;

	@Override
	public String getId() {
		return this.username;
	}
}
