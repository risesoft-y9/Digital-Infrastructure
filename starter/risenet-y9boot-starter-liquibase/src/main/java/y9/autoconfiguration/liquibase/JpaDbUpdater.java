package y9.autoconfiguration.liquibase;

import org.hibernate.integrator.api.integrator.Y9TenantHibernateInfoHolder;

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
public class JpaDbUpdater extends DbUpdater {
    public JpaDbUpdater(Y9TenantDataSourceLookup y9TenantDataSourceLookup,
        TenantDataInitializer tenantDataInitializer) {
        super(y9TenantDataSourceLookup, tenantDataInitializer);
    }

    @Override
    protected void doUpdate(String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9TenantHibernateInfoHolder.schemaUpdate(Y9Context.getEnvironment());
    }
}
