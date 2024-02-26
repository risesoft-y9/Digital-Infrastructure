package y9.autoconfiguration.tenant;

import java.util.Map;
import java.util.Set;

import org.hibernate.integrator.api.integrator.Y9TenantHibernateInfoHolder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

@Slf4j
public class OnY9MultiTenantApplicationReady implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOGGER.info("MultiTenant schemaUpdate...");

        ConfigurableApplicationContext ctx = event.getApplicationContext();
        Y9TenantDataSourceLookup y9TenantDataSourceLookup = ctx.getBean(Y9TenantDataSourceLookup.class);
        if (y9TenantDataSourceLookup != null) {
            Map<String, HikariDataSource> map = y9TenantDataSourceLookup.getDataSources();
            Set<String> list = map.keySet();
            for (String tenantId : list) {
                Y9LoginUserHolder.setTenantId(tenantId);
                Y9TenantHibernateInfoHolder.schemaUpdate(ctx.getEnvironment());
            }
        }
    }

}
