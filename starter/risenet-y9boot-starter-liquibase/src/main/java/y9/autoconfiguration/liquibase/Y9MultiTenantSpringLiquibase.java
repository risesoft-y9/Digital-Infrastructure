package y9.autoconfiguration.liquibase;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.RequiredArgsConstructor;

import net.risesoft.y9.configuration.feature.liquibase.Y9LiquibaseProperties;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;

/**
 * @author shidaobang
 * @date 2023/10/18
 * @since 9.6.3
 */
@RequiredArgsConstructor
public class Y9MultiTenantSpringLiquibase implements InitializingBean {

    private final Y9TenantDataSourceLookup y9TenantDataSourceLookup;
    private final Y9LiquibaseProperties properties;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, DruidDataSource> dataSources = y9TenantDataSourceLookup.getDataSources();
        for (Map.Entry<String, DruidDataSource> stringDruidDataSourceEntry : dataSources.entrySet()) {
            DruidDataSource dataSource = stringDruidDataSourceEntry.getValue();
            update(dataSource);
        }
    }

    private void update(DataSource dataSource) throws LiquibaseException {
        SpringLiquibase liquibase = getSpringLiquibase(dataSource);
        liquibase.afterPropertiesSet();
    }

    public void update(String tenantId) throws LiquibaseException {
        // 方法暴露出去 工程中可调用执行
        DataSource dataSource = y9TenantDataSourceLookup.getDataSource(tenantId);
        update(dataSource);
    }

    private SpringLiquibase getSpringLiquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(this.properties.getTenantChangeLog());
        liquibase.setClearCheckSums(this.properties.isClearChecksums());
        liquibase.setContexts(this.properties.getContexts());
        liquibase.setDefaultSchema(this.properties.getDefaultSchema());
        liquibase.setLiquibaseSchema(this.properties.getLiquibaseSchema());
        liquibase.setLiquibaseTablespace(this.properties.getLiquibaseTablespace());
        liquibase.setDatabaseChangeLogTable(this.properties.getDatabaseChangeLogTable());
        liquibase.setDatabaseChangeLogLockTable(this.properties.getDatabaseChangeLogLockTable());
        liquibase.setDropFirst(this.properties.isDropFirst());
        liquibase.setShouldRun(this.properties.isTenantEnabled());
        liquibase.setLabels(this.properties.getLabels());
        liquibase.setChangeLogParameters(this.properties.getParameters());
        liquibase.setRollbackFile(this.properties.getRollbackFile());
        liquibase.setTestRollbackOnUpdate(this.properties.isTestRollbackOnUpdate());
        liquibase.setTag(this.properties.getTag());
        return liquibase;
    }
}
