package net.risesoft.schema;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.liquibase.Y9MultiTenantSpringLiquibase;

import liquibase.exception.LiquibaseException;

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

    @Override
    public void doUpdate(String tenantId) throws LiquibaseException {
        y9MultiTenantSpringLiquibase.update(tenantId);
    }
}
