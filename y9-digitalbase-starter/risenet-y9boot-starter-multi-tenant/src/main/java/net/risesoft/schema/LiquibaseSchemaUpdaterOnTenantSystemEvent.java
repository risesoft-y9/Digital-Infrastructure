package net.risesoft.schema;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.liquibase.Y9MultiTenantSpringLiquibase;

/**
 * liquibase 更新器
 * 
 * @author shidaobang
 * @date 2023/11/20
 * @since 9.6.3
 */
@Slf4j
@RequiredArgsConstructor
public class LiquibaseSchemaUpdaterOnTenantSystemEvent implements SchemaUpdaterOnTenantSystemEvent {

    private final Y9MultiTenantSpringLiquibase y9MultiTenantSpringLiquibase;

    @Override
    public void doUpdate(String tenantId) {
        y9MultiTenantSpringLiquibase.update(tenantId);
    }
}
