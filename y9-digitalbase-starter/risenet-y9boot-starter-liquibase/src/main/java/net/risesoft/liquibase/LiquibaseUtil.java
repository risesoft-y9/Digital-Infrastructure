package net.risesoft.liquibase;

import javax.sql.DataSource;

import org.springframework.boot.liquibase.autoconfigure.DataSourceClosingSpringLiquibase;
import org.springframework.core.io.ResourceLoader;

import com.zaxxer.hikari.HikariDataSource;

import net.risesoft.y9.configuration.feature.liquibase.Y9LiquibaseProperties;

import liquibase.integration.spring.SpringLiquibase;

public class LiquibaseUtil {

    public static SpringLiquibase getSpringLiquibase(HikariDataSource dataSource, Y9LiquibaseProperties properties,
        ResourceLoader resourceLoader, boolean isTenant) {
        DataSource migrateDataSource = getMigrateDataSource(dataSource);
        SpringLiquibase liquibase =
            dataSource == migrateDataSource ? new SpringLiquibase() : new DataSourceClosingSpringLiquibase();
        liquibase.setDataSource(migrateDataSource);
        liquibase.setResourceLoader(resourceLoader);
        liquibase.setChangeLog(isTenant ? properties.getTenantChangeLog() : properties.getPublicChangeLog());
        liquibase.setClearCheckSums(properties.isClearChecksums());
        liquibase.setContexts(properties.getContexts());
        // liquibase.setDefaultSchema(properties.getDefaultSchema());
        // liquibase.setLiquibaseSchema(properties.getLiquibaseSchema());
        liquibase.setLiquibaseTablespace(properties.getLiquibaseTablespace());
        liquibase.setDatabaseChangeLogTable(properties.getDatabaseChangeLogTable());
        liquibase.setDatabaseChangeLogLockTable(properties.getDatabaseChangeLogLockTable());
        liquibase.setDropFirst(properties.isDropFirst());
        liquibase.setShouldRun(isTenant ? properties.isTenantEnabled() : properties.isPublicEnabled());
        liquibase.setLabelFilter(properties.getLabels());
        liquibase.setChangeLogParameters(properties.getParameters());
        liquibase.setRollbackFile(properties.getRollbackFile());
        liquibase.setTestRollbackOnUpdate(properties.isTestRollbackOnUpdate());
        liquibase.setTag(properties.getTag());
        return liquibase;
    }

    public static DataSource getMigrateDataSource(HikariDataSource dataSource) {
        // String url = dataSource.getJdbcUrl();
        // if (url.contains("jdbc:kingbase8")) {
        // // 人大金仓数据库需特殊处理
        // DataSourceBuilder<?> builder = DataSourceBuilder.derivedFrom(dataSource).type(SimpleDriverDataSource.class);
        // url = url.replace("jdbc:kingbase8", "jdbc:postgresql");
        // builder.url(url);
        // builder.driverClassName("org.postgresql.Driver");
        // return builder.build();
        // }
        return dataSource;
    }
}
