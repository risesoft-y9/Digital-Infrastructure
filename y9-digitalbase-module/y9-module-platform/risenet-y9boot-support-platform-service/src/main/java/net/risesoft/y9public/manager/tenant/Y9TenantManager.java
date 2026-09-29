package net.risesoft.y9public.manager.tenant;

import java.util.Optional;

import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9public.entity.tenant.Y9Tenant;

/**
 * 租户 Manager
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
public interface Y9TenantManager {

    /**
     * 根据id获取租户信息
     *
     * @param id 租户id
     * @return {@link Y9Tenant}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Tenant getById(String id);

    /**
     * 新增租户
     *
     * @param y9Tenant 租户信息
     * @return {@link Y9Tenant}
     * @throws Y9NotFoundException 父租户id对应的记录不存在的情况
     */
    Y9Tenant insert(Y9Tenant y9Tenant);

    /**
     * 更新租户
     *
     * @param y9Tenant 租户信息
     * @return {@link Y9Tenant}
     * @throws Y9NotFoundException 租户id或父租户id对应的记录不存在的情况
     */
    Y9Tenant update(Y9Tenant y9Tenant);

    /**
     * 如果当前是单租户则返回
     *
     * @return {@code Optional<Y9Tenant> }
     */
    Optional<Y9Tenant> findIfSingleTenant();

}
