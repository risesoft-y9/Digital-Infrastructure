package net.risesoft.schema;

import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.liquibase.Y9MultiTenantSpringLiquibase;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

/**
 * liquibase 更新器
 * 
 * @author shidaobang
 * @date 2023/11/20
 * @since 9.6.3
 */
@Slf4j
@RequiredArgsConstructor
public class LiquibaseSchemaUpdater implements SchemaUpdater {

    private final Y9MultiTenantSpringLiquibase y9MultiTenantSpringLiquibase;
    private final Y9TenantDataSourceLookup y9TenantDataSourceLookup;

    @Override
    public void updateByTenant(String tenantId) {
        DataSource dataSource = y9TenantDataSourceLookup.getDataSource(tenantId);
        y9MultiTenantSpringLiquibase.update(dataSource);
    }

    @Override
    public void updateAllTenants() {
        for (String tenantId : y9TenantDataSourceLookup.getDataSources().keySet()) {
            DataSource dataSource = y9TenantDataSourceLookup.getDataSources().get(tenantId);
            y9MultiTenantSpringLiquibase.update(dataSource);
        }
    }
}
