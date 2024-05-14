package net.risesoft.schema;

import org.hibernate.integrator.api.integrator.Y9TenantHibernateInfoHolder;

import lombok.RequiredArgsConstructor;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * JPA 更新器
 * 
 * @author shidaobang
 * @date 2023/11/20
 * @since 9.6.3
 */
@RequiredArgsConstructor
public class JpaSchemaUpdater implements SchemaUpdater {

    @Override
    public void doUpdate(String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9TenantHibernateInfoHolder.schemaUpdate(Y9Context.getEnvironment());
    }
}
