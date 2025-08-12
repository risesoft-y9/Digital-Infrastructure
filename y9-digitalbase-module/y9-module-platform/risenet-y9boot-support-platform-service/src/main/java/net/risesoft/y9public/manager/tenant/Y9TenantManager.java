package net.risesoft.y9public.manager.tenant;

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

}
