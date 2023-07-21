package net.risesoft.api.org;

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
public interface AuthenticateApi {

    /**
     * 根据用户名和密码认证
     *
     * @param tenantShortName 租户英文名称
     * @param loginName 登录名称
     * @param password 登录密码
     * @return Message
     * @since 9.6.0
     */
    Message authenticate3(String tenantShortName, String loginName, String password);

    /**
     * 根据手机号和密码认证
     *
     * @param tenantShortName 租户英文名称
     * @param mobile 手机号
     * @param password 密码
     * @return Message
     * @since 9.6.0
     */
    Message authenticate5(String tenantShortName, String mobile, String password);
}
