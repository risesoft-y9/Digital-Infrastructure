package y9.autoconfiguration.liquibase;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import com.alibaba.druid.pool.DruidDataSource;

import net.risesoft.liquibase.LiquibaseUtil;
import net.risesoft.liquibase.Y9MultiTenantSpringLiquibase;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

import liquibase.integration.spring.SpringLiquibase;

/**
 * @author shidaobang
 * @date 2023/10/18
 * @since 9.6.3
 */
@Configuration
@AutoConfiguration(before = {LiquibaseAutoConfiguration.class})
@EnableConfigurationProperties(Y9Properties.class)
public class Y9LiquibaseAutoConfiguration {

    @Bean
    @ConditionalOnBean(name = "y9TenantDataSourceLookup")
    @ConditionalOnProperty(name = "y9.feature.liquibase.tenant-enabled", havingValue = "true")
    public Y9MultiTenantSpringLiquibase y9MultiTenantSpringLiquibase(Y9TenantDataSourceLookup y9TenantDataSourceLookup,
                                                                     Y9Properties properties, ResourceLoader resourceLoader) {
        return new Y9MultiTenantSpringLiquibase(y9TenantDataSourceLookup, properties.getFeature().getLiquibase(),
            resourceLoader);
    }

    @Bean
    @ConditionalOnBean(name = "y9PublicDS")
    public SpringLiquibase liquibase(Y9Properties properties, @Qualifier("y9PublicDS") DruidDataSource dataSource,
        ResourceLoader resourceLoader) {
        return LiquibaseUtil.getSpringLiquibase(dataSource, properties.getFeature().getLiquibase(), resourceLoader,
            false);
    }

}
