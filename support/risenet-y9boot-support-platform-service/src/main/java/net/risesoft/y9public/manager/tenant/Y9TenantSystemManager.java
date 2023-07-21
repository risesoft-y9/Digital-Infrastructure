package net.risesoft.y9public.manager.tenant;

/**
 * 租户系统 Manager
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
public interface Y9TenantSystemManager {
    void deleteBySystemId(String systemId);

    void delete(String id);

    String getDataSourceIdByTenantIdAndSystemId(String tenantId, String systemId);
}
