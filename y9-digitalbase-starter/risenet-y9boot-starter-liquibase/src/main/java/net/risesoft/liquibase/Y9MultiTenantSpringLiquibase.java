package net.risesoft.liquibase;

import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ResourceLoader;

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
    private final ResourceLoader resourceLoader;

    @Override
    public void afterPropertiesSet() throws Exception {
        updateAll();
    }

    /**
     * 更新所有租户数据源的表结构
     *
     * @throws LiquibaseException - all Liquibase exceptions.
     */
    public void updateAll() throws LiquibaseException {
        Map<String, DruidDataSource> dataSources = y9TenantDataSourceLookup.getDataSources();
        for (Map.Entry<String, DruidDataSource> stringDruidDataSourceEntry : dataSources.entrySet()) {
            DruidDataSource dataSource = stringDruidDataSourceEntry.getValue();
            update(dataSource);
        }
    }

    private void update(DruidDataSource dataSource) throws LiquibaseException {
        SpringLiquibase liquibase =
            LiquibaseUtil.getSpringLiquibase(dataSource, this.properties, this.resourceLoader, true);
        liquibase.afterPropertiesSet();
    }

    /**
     * 更新单个租户数据源的表结构
     *
     * @param tenantId 租户id
     * @throws LiquibaseException - all Liquibase exceptions.
     */
    public void update(String tenantId) throws LiquibaseException {
        // 方法暴露出去 工程中可调用执行
        DruidDataSource dataSource = (DruidDataSource)y9TenantDataSourceLookup.getDataSource(tenantId);
        update(dataSource);
    }

}
