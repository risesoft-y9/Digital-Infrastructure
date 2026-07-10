package net.risesoft.y9public.manager.tenant;

import java.util.Optional;

import net.risesoft.y9public.entity.tenant.Y9Tenant;

/**
 * 租户 Manager
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
public interface Y9TenantManager {

    Y9Tenant getById(String id);

    Y9Tenant insert(Y9Tenant y9Tenant);

    Y9Tenant update(Y9Tenant y9Tenant);

    /**
     * 如果当前是单租户则返回
     *
     * @return {@code Optional<Y9Tenant> }
     */
    Optional<Y9Tenant> findIfSingleTenant();

}
