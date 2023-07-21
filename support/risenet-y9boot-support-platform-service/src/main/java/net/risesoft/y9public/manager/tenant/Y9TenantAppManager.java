package net.risesoft.y9public.manager.tenant;

import net.risesoft.y9public.entity.tenant.Y9TenantApp;

/**
 * 租户应用 Manager
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
public interface Y9TenantAppManager {
    void deleteByAppId(String appId);

    Y9TenantApp getByTenantIdAndAppIdAndTenancy(String tenantId, String appId, Boolean tenancy);
}
