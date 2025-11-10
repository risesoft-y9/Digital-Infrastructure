package net.risesoft.y9public.service.user;

import java.util.List;
import java.util.Optional;

import net.risesoft.model.user.UserInfo;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9UserService {

    /**
     * 根据id删除用户
     *
     * @param id 唯一标识
     */
    void delete(String id);

    /**
     * 根据租户id删除用户
     *
     * @param tenantId 租户id
     */
    void deleteByTenantId(String tenantId);

    /**
     * 根据 人员id 和 租户id 查找用户
     *
     * @param personId 人员id
     * @param tenantId 租户id
     * @return {@code Optional<}{@link UserInfo}{@code >}
     */
    Optional<UserInfo> findByPersonIdAndTenantId(String personId, String tenantId);

    /**
     * 查询所有用户信息
     *
     * @return {@code List<}{@link UserInfo}{@code >}
     */
    List<UserInfo> listAll();

    /**
     * 根据租户id查找用户
     *
     * @param tenantId 租户id
     * @return {@code List<}{@link UserInfo}{@code >}
     */
    List<UserInfo> listByTenantId(String tenantId);

    /**
     * 向用户总表里添加一个用户
     *
     * @param y9User 用户对象
     * @return {@link UserInfo}
     */
    UserInfo save(UserInfo y9User);

    /**
     * 根据tenantId,tenantName，tenantLoginName进行更新
     *
     * @param tenantId 租户id
     * @param tenantName 租户名
     * @param tenantShortName 租户英文名
     */
    void updateByTenantId(String tenantId, String tenantName, String tenantShortName);
}
