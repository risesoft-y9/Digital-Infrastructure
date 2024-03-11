
package y9.autoconfiguration.tenant;

import javax.sql.DataSource;

import jakarta.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import com.zaxxer.hikari.HikariDataSource;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSource;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

@Configuration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(JpaProperties.class)
@EnableTransactionManagement(proxyTargetClass = true, mode = AdviceMode.ASPECTJ)
@EnableJpaRepositories(basePackages = {"${y9.feature.jpa.packagesToScanRepositoryTenant}"},
    includeFilters = {@ComponentScan.Filter(classes = JpaRepository.class, type = FilterType.ASSIGNABLE_TYPE)},
    entityManagerFactoryRef = "rsTenantEntityManagerFactory", transactionManagerRef = "rsTenantTransactionManager")
public class SpringMultiTenantConfiguration {

    // @ConfigurationProperties("spring.datasource.hikari.tenantDefault")
    @Bean("defaultDataSource")
    @ConditionalOnMissingBean(name = "defaultDataSource")
    public HikariDataSource defaultDataSource(Environment environment) {
        HikariDataSource dataSource = new HikariDataSource();

        // 比如：spring.datasource.hikari.tenantDefault=spring.datasource.hikari.flowable
        String tenantDefault = environment.getProperty("spring.datasource.hikari.tenantDefault", String.class);
        if (!StringUtils.hasText(tenantDefault)) {
            tenantDefault = "spring.datasource.hikari.y9-public";
        }
        String prefix = tenantDefault + ".";

        String driverClassName = environment.getProperty(prefix + "driverClassName", String.class);
        String jdbcUrl = environment.getProperty(prefix + "jdbcUrl", String.class);
        String username = environment.getProperty(prefix + "username", String.class);
        String password = environment.getProperty(prefix + "password", String.class);
        Integer maxActive = environment.getProperty(prefix + "maximumPoolSize", Integer.class);
        Integer minIdle = environment.getProperty(prefix + "minimumIdle", Integer.class);

        if (jdbcUrl != null) {
            dataSource.setJdbcUrl(jdbcUrl);
        }
        if (username != null) {
            dataSource.setUsername(username);
        }
        if (password != null) {
            dataSource.setPassword(password);
        }
        if (driverClassName != null) {
            dataSource.setDriverClassName(driverClassName);
        }
        if (minIdle != null) {
            dataSource.setMinimumIdle(minIdle);
        }
        if (maxActive != null) {
            dataSource.setMaximumPoolSize(maxActive);
        }

        return dataSource;
    }

    @Bean("jdbcTemplate4Tenant")
    @ConditionalOnMissingBean(name = "jdbcTemplate4Tenant")
    public JdbcTemplate jdbcTemplate4Tenant(@Qualifier("y9TenantDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean
    @ConditionalOnMissingBean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Primary
    @Bean({"rsTenantEntityManagerFactory", "entityManagerFactory"})
    public LocalContainerEntityManagerFactoryBean rsTenantEntityManagerFactory(
        @Qualifier("y9TenantDataSource") DataSource ds, JpaProperties jpaProperties, Environment environment) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName("y9Tenant");
        em.setDataSource(ds);
        em.setJpaVendorAdapter(jpaVendorAdapter());

        String basePackages = environment.getProperty("y9.feature.jpa.packagesToScanEntityTenant");
        em.setPackagesToScan(basePackages.split(","));
        em.setJpaPropertyMap(jpaProperties.getProperties());
        return em;
    }

    @Primary
    @Bean({"rsTenantTransactionManager", "transactionManager"})
    public PlatformTransactionManager
        rsTenantTransactionManager(@Qualifier("rsTenantEntityManagerFactory") EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    @ConditionalOnMissingBean(value = Y9Context.class)
    public Y9Context y9Context() {
        return new Y9Context();
    }

    @ConfigurationProperties("spring.datasource.hikari.y9-public")
    @Bean(name = {"y9PublicDS"})
    @ConditionalOnMissingBean(name = "y9PublicDS")
    public HikariDataSource y9PublicDS() {
        HikariDataSource dataSource = new HikariDataSource();
        return dataSource;
    }

    @Primary
    @Bean("y9TenantDataSource")
    @ConditionalOnMissingBean(name = "y9TenantDataSource")
    public DataSource y9TenantDataSource(@Qualifier("defaultDataSource") HikariDataSource defaultDataSource,
        @Qualifier("y9TenantDataSourceLookup") Y9TenantDataSourceLookup y9TenantDataSourceLookup) {
        return new Y9TenantDataSource(defaultDataSource, y9TenantDataSourceLookup);
    }

    @Bean("y9TenantDataSourceLookup")
    public Y9TenantDataSourceLookup y9TenantDataSourceLookup(@Qualifier("y9PublicDS") HikariDataSource ds,
        Environment environment) {
        return new Y9TenantDataSourceLookup((HikariDataSource)ds, environment.getProperty("y9.systemName"));
    }

    @Bean
    public OnY9MultiTenantApplicationReady onY9MultiTenantApplicationReady() {
        return new OnY9MultiTenantApplicationReady();
    }

}