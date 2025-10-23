package y9.autoconfiguration.tenant;

import java.util.Map;

import org.hibernate.integrator.api.integrator.Y9TenantHibernateInfoHolder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

@Slf4j
@RequiredArgsConstructor
public class Y9MultiTenantSchemaUpdaterOnApplicationReady implements ApplicationListener<ApplicationReadyEvent> {

    private final Y9TenantDataSourceLookup y9TenantDataSourceLookup;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOGGER.info("MultiTenant schemaUpdate...");

        Map<String, DruidDataSource> map = y9TenantDataSourceLookup.getDataSources();
        for (String tenantId : map.keySet()) {
            Y9LoginUserHolder.setTenantId(tenantId);
            Y9TenantHibernateInfoHolder.schemaUpdate(Y9Context.getEnvironment());
        }
    }

}
