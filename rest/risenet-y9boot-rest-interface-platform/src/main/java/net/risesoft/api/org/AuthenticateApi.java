package net.risesoft.api.org;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.AuthenticateResult;
import net.risesoft.pojo.Y9Result;

/**
 * 用户身份验证服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
public interface AuthenticateApi {

    /**
     * 用户登录名密码认证
     *
     * @param tenantShortName 租户英文名称
     * @param loginName 登录名称
     * @param base64EncodedPassword base编码过的密码
     * @return {@code Y9Result<AuthenticateResult>} 通用请求返回对象 - data 是认证通过返回的用户信息
     * @since 9.6.0
     */
    @RequestMapping("/authenticate3")
    Y9Result<AuthenticateResult> authenticate3(@RequestParam("tenantShortName") @NotBlank String tenantShortName,
        @RequestParam("loginName") @NotBlank String loginName,
        @RequestParam("base64EncodedPassword") @NotBlank String base64EncodedPassword);

    /**
     * 用户手机号码密码认证
     *
     * @param tenantShortName 租户英文名称
     * @param mobile 手机号
     * @param base64EncodedPassword base编码过的密码
     * @return {@code Y9Result<AuthenticateResult>} 通用请求返回对象 - data 是认证通过返回的用户信息
     * @since 9.6.0
     */
    @RequestMapping("/authenticate5")
    Y9Result<AuthenticateResult> authenticate5(@RequestParam("tenantShortName") @NotBlank String tenantShortName,
        @RequestParam("mobile") @NotBlank String mobile,
        @RequestParam("base64EncodedPassword") @NotBlank String base64EncodedPassword);

}
