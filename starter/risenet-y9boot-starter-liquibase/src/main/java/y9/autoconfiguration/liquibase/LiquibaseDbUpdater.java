package y9.autoconfiguration.liquibase;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

import liquibase.exception.LiquibaseException;

/**
 * liquibase 更新器
 * 
 * @author shidaobang
 * @date 2023/11/20
 * @since 9.6.3
 */
@Slf4j
public class LiquibaseDbUpdater extends DbUpdater {

    private final Y9MultiTenantSpringLiquibase y9MultiTenantSpringLiquibase;

    public LiquibaseDbUpdater(Y9TenantDataSourceLookup y9TenantDataSourceLookup,
        Y9MultiTenantSpringLiquibase y9MultiTenantSpringLiquibase, TenantDataInitializer tenantDataInitializer) {
        super(y9TenantDataSourceLookup, tenantDataInitializer);
        this.y9MultiTenantSpringLiquibase = y9MultiTenantSpringLiquibase;
    }

    @Override
    protected void doUpdate(String tenantId) {
        try {
            y9MultiTenantSpringLiquibase.update(tenantId);
        } catch (LiquibaseException e) {
            LOGGER.warn("更新租户数据结构时发生异常", e);
        }
    }
}
