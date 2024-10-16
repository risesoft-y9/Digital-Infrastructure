package net.risesoft.service.org;

import net.risesoft.model.platform.AuthenticateResult;
import net.risesoft.model.platform.Message;

/**
 * 认证服务
 *
 * @author shidaobang
 * @date 2024/05/23
 */
public interface AuthService {

    /**
     * 用户认证
     *
     * @param loginName 登录名
     * @param password 密码
     * @return {@link Message}
     */
    Message authenticate(String loginName, String password);

    /**
     * 用户认证
     *
     * @param tenantName 租户名
     * @param loginName 登录名
     * @param password 密码
     * @return {@link Message}
     */
    Message authenticate2(String tenantName, String loginName, String password);

    /**
     * 用户认证
     *
     * @param loginName 登录名
     * @param base64EncodedPassword base编码过的密码
     * @return {@link Message}
     */
    AuthenticateResult authenticate3(String loginName, String base64EncodedPassword);

    /**
     * 用户认证
     *
     * @param tenantShortName 租户英文名
     * @param loginName 登录名
     * @param password 密码
     * @return {@link Message}
     */
    Message authenticate3(String tenantShortName, String loginName, String password);

    /**
     * 用户认证
     *
     * @param tenantShortName 租户英文名
     * @param loginName 登录名
     * @return {@link Message}
     */
    Message authenticate4(String tenantShortName, String loginName);

    /**
     * 用户认证
     *
     * @param mobile 手机号
     * @param base64EncodedPassword base编码过的密码
     * @return {@link Message}
     */
    AuthenticateResult authenticate5(String mobile, String base64EncodedPassword);

    /**
     * 用户认证
     *
     * @param tenantShortName 租户英文名
     * @param mobile 手机号
     * @param password 密码
     * @return {@link Message}
     */
    Message authenticate5(String tenantShortName, String mobile, String password);

    /**
     * 用户认证
     *
     * @param tenantShortName 租户英文名
     * @param loginName 登录名
     * @param password 密码
     * @param parentId 父节点id
     * @return {@link Message}
     */
    Message authenticate6(String tenantShortName, String loginName, String password, String parentId);

}
