package y9.autoconfiguration.liquibase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import com.zaxxer.hikari.HikariDataSource;

import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.feature.liquibase.Y9LiquibaseProperties;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

import y9.autoconfiguration.jpa.JpaPublicConfiguration;
import y9.autoconfiguration.tenant.SpringMultiTenantConfiguration;

import liquibase.integration.spring.SpringLiquibase;

/**
 * @author shidaobang
 * @date 2023/10/18
 * @since 9.6.3
 */
@Configuration
@AutoConfiguration(after = {SpringMultiTenantConfiguration.class, JpaPublicConfiguration.class},
    before = {LiquibaseAutoConfiguration.class})
@EnableConfigurationProperties(Y9Properties.class)
public class Y9LiquibaseAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "y9.feature.liquibase.tenant-enabled", havingValue = "true")
    public Y9MultiTenantSpringLiquibase y9MultiTenantSpringLiquibase(Y9TenantDataSourceLookup y9TenantDataSourceLookup,
        Y9Properties properties, ResourceLoader resourceLoader) {
        return new Y9MultiTenantSpringLiquibase(y9TenantDataSourceLookup, properties.getFeature().getLiquibase(),
            resourceLoader);
    }

    @Bean
    public SpringLiquibase liquibase(Y9Properties properties, @Qualifier("y9PublicDS") HikariDataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        Y9LiquibaseProperties liquibaseProperties = properties.getFeature().getLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(liquibaseProperties.getPublicChangeLog());
        liquibase.setClearCheckSums(liquibaseProperties.isClearChecksums());
        liquibase.setContexts(liquibaseProperties.getContexts());
        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
        liquibase.setLiquibaseSchema(liquibaseProperties.getLiquibaseSchema());
        liquibase.setLiquibaseTablespace(liquibaseProperties.getLiquibaseTablespace());
        liquibase.setDatabaseChangeLogTable(liquibaseProperties.getDatabaseChangeLogTable());
        liquibase.setDatabaseChangeLogLockTable(liquibaseProperties.getDatabaseChangeLogLockTable());
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
        liquibase.setShouldRun(liquibaseProperties.isPublicEnabled());
        liquibase.setLabels(liquibaseProperties.getLabels());
        liquibase.setChangeLogParameters(liquibaseProperties.getParameters());
        liquibase.setRollbackFile(liquibaseProperties.getRollbackFile());
        liquibase.setTestRollbackOnUpdate(liquibaseProperties.isTestRollbackOnUpdate());
        liquibase.setTag(liquibaseProperties.getTag());
        return liquibase;
    }

    @Bean
    @ConditionalOnBean(name = "y9MultiTenantSpringLiquibase")
    public DbUpdater liquibaseDbUpdater(Y9TenantDataSourceLookup y9TenantDataSourceLookup,
        Y9MultiTenantSpringLiquibase y9MultiTenantSpringLiquibase,
        @Autowired(required = false) TenantDataInitializer tenantDataInitializer) {
        return new LiquibaseDbUpdater(y9TenantDataSourceLookup, y9MultiTenantSpringLiquibase, tenantDataInitializer);
    }

    @Bean
    @ConditionalOnMissingBean(name = "liquibaseDbUpdater")
    public DbUpdater jpaDbUpdater(Y9TenantDataSourceLookup y9TenantDataSourceLookup,
        @Autowired(required = false) TenantDataInitializer tenantDataInitializer) {
        return new JpaDbUpdater(y9TenantDataSourceLookup, tenantDataInitializer);
    }

}
