package net.risesoft.api.platform.v0.org;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.platform.Message;

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
@Deprecated
public interface AuthenticateApi {

    /**
     * 用户登录名密码认证
     *
     * @param tenantShortName 租户英文名称
     * @param loginName 登录名称
     * @param password 登录密码
     * @return Message
     * @since 9.6.0
     */
    @RequestMapping("/authenticate3")
    Message authenticate3(@RequestParam("tenantShortName") @NotBlank String tenantShortName,
        @RequestParam("loginName") @NotBlank String loginName,
        @RequestParam("base64EncodedPassword") @NotBlank String password);

    /**
     * 用户手机号码密码认证
     *
     * @param tenantShortName 租户英文名称
     * @param mobile 手机号
     * @param password 密码
     * @return Message
     * @since 9.6.0
     */
    @RequestMapping("/authenticate5")
    Message authenticate5(@RequestParam("tenantShortName") @NotBlank String tenantShortName,
        @RequestParam("mobile") @NotBlank String mobile,
        @RequestParam("base64EncodedPassword") @NotBlank String password);

}
