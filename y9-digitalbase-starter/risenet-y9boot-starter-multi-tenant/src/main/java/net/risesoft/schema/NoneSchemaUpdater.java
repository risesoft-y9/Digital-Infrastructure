package net.risesoft.schema;

import org.springframework.kafka.core.KafkaTemplate;

import net.risesoft.init.TenantDataInitializer;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

/**
 * @author shidaobang
 * @date 2023/12/07
 * @since 9.6.3
 */
public class NoneSchemaUpdater extends SchemaUpdater {

    public NoneSchemaUpdater(Y9TenantDataSourceLookup y9TenantDataSourceLookup,
        TenantDataInitializer tenantDataInitializer, KafkaTemplate<String, String> y9KafkaTemplate) {
        super(y9TenantDataSourceLookup, tenantDataInitializer, y9KafkaTemplate);
    }

    @Override
    protected void doUpdate(String tenantId) throws Exception {

    }
}
