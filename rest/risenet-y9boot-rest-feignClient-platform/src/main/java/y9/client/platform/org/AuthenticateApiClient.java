package y9.client.platform.org;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.org.AuthenticateApi;
import net.risesoft.model.Message;

/**
 * 用户身份验证服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "AuthenticateApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}", path = "/services/rest/auth")
public interface AuthenticateApiClient extends AuthenticateApi {

    /**
     * 用户登录名密码认证
     *
     * @param tenantShortName 租户英文名称
     * @param loginName 登录名称
     * @param password 登录密码
     * @return Message
     * @since 9.6.0
     */
    @Override
    @RequestMapping("/authenticate3")
    Message authenticate3(@RequestParam("tenantShortName") String tenantShortName, @RequestParam("loginName") String loginName, @RequestParam("password") String password);

    /**
     * 用户手机号码密码认证
     *
     * @param tenantShortName 租户英文名称
     * @param mobile 手机号
     * @param password 密码
     * @return Message
     * @since 9.6.0
     */
    @Override
    @RequestMapping("/authenticate5")
    Message authenticate5(@RequestParam("tenantShortName") String tenantShortName, @RequestParam("mobile") String mobile, @RequestParam("password") String password);
}