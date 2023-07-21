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
    Y9DataSource createTenantDefaultDataSource(String shortName, Integer tenantType, String systemName);

    String buildTenantDataSourceName(String shortName, Integer tenantType);

    Y9DataSource createTenantDefaultDataSource(String dbName);

    Y9DataSource save(Y9DataSource y9DataSource);

    void dropTenantDefaultDataSource(String dataSourceId, String dbName);

    void delete(String id);
}
