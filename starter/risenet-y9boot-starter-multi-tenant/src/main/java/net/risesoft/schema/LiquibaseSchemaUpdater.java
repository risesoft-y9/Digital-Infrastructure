package net.risesoft.schema;

import org.springframework.kafka.core.KafkaTemplate;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.init.TenantDataInitializer;
import net.risesoft.liquibase.Y9MultiTenantSpringLiquibase;
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
public class LiquibaseSchemaUpdater extends SchemaUpdater {

    private final Y9MultiTenantSpringLiquibase y9MultiTenantSpringLiquibase;

    public LiquibaseSchemaUpdater(Y9TenantDataSourceLookup y9TenantDataSourceLookup,
        KafkaTemplate<String, String> y9KafkaTemplate, Y9MultiTenantSpringLiquibase y9MultiTenantSpringLiquibase,
        TenantDataInitializer tenantDataInitializer) {
        super(y9TenantDataSourceLookup, tenantDataInitializer, y9KafkaTemplate);
        this.y9MultiTenantSpringLiquibase = y9MultiTenantSpringLiquibase;
    }

    @Override
    protected void doUpdate(String tenantId) throws LiquibaseException {
        y9MultiTenantSpringLiquibase.update(tenantId);
    }
}
