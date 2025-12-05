package net.risesoft.schema;

import org.hibernate.integrator.api.integrator.Y9TenantHibernateInfoHolder;

import lombok.RequiredArgsConstructor;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

/**
 * JPA 更新器
 * 
 * @author shidaobang
 * @date 2023/11/20
 * @since 9.6.3
 */
@RequiredArgsConstructor
public class JpaSchemaUpdater implements SchemaUpdater {

    private final Y9TenantDataSourceLookup y9TenantDataSourceLookup;

    @Override
    public void updateByTenant(String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9TenantHibernateInfoHolder.schemaUpdate(Y9Context.getEnvironment());
    }

    @Override
    public void updateAllTenants() {
        for (String tenantId : y9TenantDataSourceLookup.getDataSources().keySet()) {
            Y9LoginUserHolder.setTenantId(tenantId);
            Y9TenantHibernateInfoHolder.schemaUpdate(Y9Context.getEnvironment());
        }
    }
}
