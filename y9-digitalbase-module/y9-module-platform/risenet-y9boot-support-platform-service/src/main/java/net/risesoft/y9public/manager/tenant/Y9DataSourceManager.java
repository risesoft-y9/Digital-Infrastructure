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

    Y9DataSource getById(String id);

    Y9DataSource createDataSourceIfNotExists(String tenantShortName, String systemName, String specifyId);

    Y9DataSource createDataSourceIfNotExists(String dbName, String specifyId);

    void delete(String id);

    void dropTenantDefaultDataSource(String dataSourceId);

    Y9DataSource save(Y9DataSource y9DataSource);

}
