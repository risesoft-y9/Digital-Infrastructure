package net.risesoft.y9public.manager.tenant;

import net.risesoft.y9public.entity.tenant.Y9DataSource;

/**
 * 数据源 Manager
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
public interface Y9DataSourceManager {
    String buildDataSourceName(String shortName, Integer tenantType, String systemName);

    Y9DataSource createTenantDefaultDataSource(String shortName, Integer tenantType, String systemName);

    Y9DataSource createTenantDefaultDataSource(String dbName);

    Y9DataSource createTenantDefaultDataSource(String dbName, String specifyId);

    void delete(String id);

    void dropTenantDefaultDataSource(String dataSourceId, String dbName);

    Y9DataSource save(Y9DataSource y9DataSource);
}
