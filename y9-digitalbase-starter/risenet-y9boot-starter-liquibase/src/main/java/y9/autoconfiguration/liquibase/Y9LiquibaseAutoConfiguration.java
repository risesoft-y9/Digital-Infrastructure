package y9.autoconfiguration.liquibase;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import com.zaxxer.hikari.HikariDataSource;

import net.risesoft.liquibase.LiquibaseUtil;
import net.risesoft.liquibase.Y9MultiTenantSpringLiquibase;
import net.risesoft.y9.configuration.feature.liquibase.Y9LiquibaseProperties;

import liquibase.integration.spring.SpringLiquibase;

/**
 * @author shidaobang
 * @date 2023/10/18
 * @since 9.6.3
 */
@Configuration
@AutoConfiguration(before = {LiquibaseAutoConfiguration.class})
@EnableConfigurationProperties(Y9LiquibaseProperties.class)
public class Y9LiquibaseAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "y9.feature.liquibase.tenant-enabled", havingValue = "true")
    public Y9MultiTenantSpringLiquibase y9MultiTenantSpringLiquibase(Y9LiquibaseProperties properties,
        ResourceLoader resourceLoader) {
        return new Y9MultiTenantSpringLiquibase(properties, resourceLoader);
    }

    @Bean
    @ConditionalOnBean(name = "y9PublicDS")
    public SpringLiquibase liquibase(Y9LiquibaseProperties properties, @Qualifier("y9PublicDS") HikariDataSource dataSource,
        ResourceLoader resourceLoader) {
        return LiquibaseUtil.getSpringLiquibase(dataSource, properties, resourceLoader, false);
    }

}
