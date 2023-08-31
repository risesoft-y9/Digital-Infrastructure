package net.risesoft.y9public.service.user;

import java.util.List;
import java.util.Optional;

import net.risesoft.y9public.entity.user.Y9User;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9UserService {

    /**
     * 验证caid
     *
     * @param personId
     * @param caid ca唯一标识
     * @return boolean
     */
    boolean checkCaidAvailability(String personId, String caid);

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
     * 根据登录名称和租户id进行查询
     *
     * @param loginName 登录名
     * @param tenantId 租户id
     * @return {@link Y9User}
     */
    Optional<Y9User> findByLoginNameAndTenantId(String loginName, String tenantId);

    /**
     * 根据 人员id 和 租户id 查找用户
     *
     * @param personId 人员id
     * @param tenantId 租户id
     * @return {@link Y9User}
     */
    Optional<Y9User> findByPersonIdAndTenantId(String personId, String tenantId);

    /**
     * 查询所有用户信息
     *
     * @return {@link List}<{@link Y9User}>
     */
    List<Y9User> listAll();

    /**
     * 根据guidPath查找用户
     *
     * @param guidPath id路径
     * @return {@link List}<{@link Y9User}>
     */
    List<Y9User> listByGuidPathLike(String guidPath);

    /**
     * 根据登录名称进行查询
     *
     * @param loginName 登录名
     * @return {@link List}<{@link Y9User}>
     */
    List<Y9User> listByLoginName(String loginName);

    /**
     * 根据租户id查找用户
     *
     * @param tenantId 租户id
     * @return {@link List}<{@link Y9User}>
     */
    List<Y9User> listByTenantId(String tenantId);

    /**
     * 向用户总表里添加一个用户
     *
     * @param orgUser 用户对象
     * @return {@link Y9User}
     */
    Y9User save(Y9User orgUser);

    /**
     * 同步
     */
    void sync();

    /**
     * 根据tenantId,tenantName，tenantLoginName进行更新
     *
     * @param tenantId 租户id
     * @param tenantName 租户名
     * @param tenantShortName 租户英文名
     */
    void updateByTenantId(String tenantId, String tenantName, String tenantShortName);
}
