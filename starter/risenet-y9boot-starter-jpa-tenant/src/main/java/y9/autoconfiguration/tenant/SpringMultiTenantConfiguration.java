
package y9.autoconfiguration.tenant;

import java.sql.SQLException;
import java.util.Collections;
import java.util.StringTokenizer;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSource;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

import y9.jpa.extension.Y9EnableJpaRepositories;

@Configuration
@AutoConfigureBefore(DruidDataSourceAutoConfigure.class)
@EnableConfigurationProperties(JpaProperties.class)
@EnableTransactionManagement(proxyTargetClass = true, mode = AdviceMode.ASPECTJ)
@Y9EnableJpaRepositories(basePackages = {"${y9.feature.jpa.packagesToScanRepositoryTenant}"}, includeFilters = {@ComponentScan.Filter(classes = JpaRepository.class, type = FilterType.ASSIGNABLE_TYPE)}, entityManagerFactoryRef = "rsTenantEntityManagerFactory",
    transactionManagerRef = "rsTenantTransactionManager")
@Slf4j
public class SpringMultiTenantConfiguration {

    // @ConfigurationProperties("spring.datasource.druid.tenantDefault")
    @Bean("defaultDataSource")
    @ConditionalOnMissingBean(name = "defaultDataSource")
    public DruidDataSource defaultDataSource(Environment environment) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();

        // 比如：spring.datasource.druid.tenantDefault=spring.datasource.druid.y9-public
        String tenantDefault = environment.getProperty("spring.datasource.druid.tenantDefault", String.class);
        if (!StringUtils.hasText(tenantDefault)) {
            tenantDefault = "spring.datasource.druid.y9-public";
        }
        String prefix = tenantDefault + ".";

        String jdbcUrl = environment.getProperty(prefix + "url", String.class);
        String username = environment.getProperty(prefix + "username", String.class);
        String password = environment.getProperty(prefix + "password", String.class);
        Boolean testWhileIdle = environment.getProperty(prefix + "testWhileIdle", Boolean.class);
        Boolean testOnBorrow = environment.getProperty(prefix + "testOnBorrow", Boolean.class);
        Boolean testOnReturn = environment.getProperty(prefix + "testOnReturn", Boolean.class);
        String validationQuery = environment.getProperty(prefix + "validationQuery", String.class);
        Boolean useGlobalDataSourceStat = environment.getProperty(prefix + "useGlobalDataSourceStat", Boolean.class);
        String filters = environment.getProperty(prefix + "filters", String.class);
        Boolean clearFiltersEnable = environment.getProperty(prefix + "clearFiltersEnable", Boolean.class);
        Boolean resetStatEnable = environment.getProperty(prefix + "resetStatEnable", Boolean.class);
        Integer notFullTimeoutRetryCount = environment.getProperty(prefix + "notFullTimeoutRetryCount", Integer.class);
        Long timeBetweenEvictionRunsMillis = environment.getProperty(prefix + "timeBetweenEvictionRunsMillis", Long.class);
        Integer maxWaithThreadCount = environment.getProperty(prefix + "maxWaithThreadCount", Integer.class);
        Long maxWaitMillis = environment.getProperty(prefix + "maxWaitMillis", Long.class);
        Boolean failFast = environment.getProperty(prefix + "failFast", Boolean.class);
        Long phyTimeoutMillis = environment.getProperty(prefix + "phyTimeoutMillis", Long.class);
        Long phyMaxUseCount = environment.getProperty(prefix + "phyMaxUseCount", Long.class);
        Long minEvictableIdleTimeMillis = environment.getProperty(prefix + "minEvictableIdleTimeMillis", Long.class);
        Long maxEvictableIdleTimeMillis = environment.getProperty(prefix + "maxEvictableIdleTimeMillis", Long.class);
        Boolean keepAlive = environment.getProperty(prefix + "keepAlive", Boolean.class);
        Long keepAliveBetweenTimeMillis = environment.getProperty(prefix + "keepAliveBetweenTimeMillis", Long.class);
        Boolean poolPreparedStatements = environment.getProperty(prefix + "poolPreparedStatements", Boolean.class);
        Boolean initVariants = environment.getProperty(prefix + "initVariants", Boolean.class);
        Boolean initGlobalVariants = environment.getProperty(prefix + "initGlobalVariants", Boolean.class);
        Boolean useUnfairLock = environment.getProperty(prefix + "useUnfairLock", Boolean.class);
        String driverClassName = environment.getProperty(prefix + "driverClassName", String.class);
        Integer initialSize = environment.getProperty(prefix + "initialSize", Integer.class);
        Integer minIdle = environment.getProperty(prefix + "minIdle", Integer.class);
        Integer maxActive = environment.getProperty(prefix + "maxActive", Integer.class);
        Boolean killWhenSocketReadTimeout = environment.getProperty(prefix + "killWhenSocketReadTimeout", Boolean.class);
        String connectionProperties = environment.getProperty(prefix + "connectionProperties", String.class);
        Integer maxPoolPreparedStatementPerConnectionSize = environment.getProperty(prefix + "maxPoolPreparedStatementPerConnectionSize", Integer.class);
        String initConnectionSqls = environment.getProperty(prefix + "initConnectionSqls", String.class);

        if (jdbcUrl != null) {
            dataSource.setUrl(jdbcUrl);
        }
        if (username != null) {
            dataSource.setUsername(username);
        }
        if (password != null) {
            dataSource.setPassword(password);
        }
        if (testWhileIdle != null) {
            dataSource.setTestWhileIdle(testWhileIdle);
        }
        if (testOnBorrow != null) {
            dataSource.setTestOnBorrow(testOnBorrow);
        }
        if (testOnReturn != null) {
            dataSource.setTestOnReturn(testOnReturn);
        }
        if (validationQuery != null) {
            dataSource.setValidationQuery(validationQuery);
        }
        if (useGlobalDataSourceStat != null) {
            dataSource.setUseGlobalDataSourceStat(useGlobalDataSourceStat);
        }
        if (filters != null) {
            try {
                dataSource.setFilters(filters);
            } catch (SQLException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        if (clearFiltersEnable != null) {
            dataSource.setClearFiltersEnable(clearFiltersEnable);
        }
        if (resetStatEnable != null) {
            dataSource.setResetStatEnable(resetStatEnable);
        }
        if (notFullTimeoutRetryCount != null) {
            dataSource.setNotFullTimeoutRetryCount(notFullTimeoutRetryCount);
        }
        if (timeBetweenEvictionRunsMillis != null) {
            dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        }
        if (maxWaithThreadCount != null) {
            dataSource.setMaxWaitThreadCount(maxWaithThreadCount);
        }
        if (maxWaitMillis != null) {
            dataSource.setMaxWait(maxWaitMillis);
        }
        if (failFast != null) {
            dataSource.setFailFast(failFast);
        }
        if (phyTimeoutMillis != null) {
            dataSource.setPhyTimeoutMillis(phyTimeoutMillis);
        }
        if (phyMaxUseCount != null) {
            dataSource.setPhyMaxUseCount(phyMaxUseCount);
        }
        if (minEvictableIdleTimeMillis != null) {
            dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        }
        if (maxEvictableIdleTimeMillis != null) {
            dataSource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);
        }
        if (keepAlive != null) {
            dataSource.setKeepAlive(keepAlive);
        }
        if (keepAliveBetweenTimeMillis != null) {
            dataSource.setKeepAliveBetweenTimeMillis(keepAliveBetweenTimeMillis);
        }
        if (poolPreparedStatements != null) {
            dataSource.setPoolPreparedStatements(poolPreparedStatements);
        }
        if (initVariants != null) {
            dataSource.setInitVariants(initVariants);
        }
        if (initGlobalVariants != null) {
            dataSource.setInitGlobalVariants(initGlobalVariants);
        }
        if (useUnfairLock != null) {
            dataSource.setUseUnfairLock(useUnfairLock);
        }
        if (driverClassName != null) {
            dataSource.setDriverClassName(driverClassName);
        }
        if (initialSize != null) {
            dataSource.setInitialSize(initialSize);
        }
        if (minIdle != null) {
            dataSource.setMinIdle(minIdle);
        }
        if (maxActive != null) {
            dataSource.setMaxActive(maxActive);
        }
        if (killWhenSocketReadTimeout != null) {
            dataSource.setKillWhenSocketReadTimeout(killWhenSocketReadTimeout);
        }
        if (connectionProperties != null) {
            dataSource.setConnectionProperties(connectionProperties);
        }
        if (maxPoolPreparedStatementPerConnectionSize != null) {
            dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        }
        if (initConnectionSqls != null) {
            StringTokenizer tokenizer = new StringTokenizer(initConnectionSqls, ";");
            dataSource.setConnectionInitSqls(Collections.list(tokenizer));
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

    /*@Bean
    public OnY9MultiTenantApplicationReady onY9MultiTenantApplicationReady() {
        return new OnY9MultiTenantApplicationReady();
    }*/

    @Primary
    @Bean({"rsTenantEntityManagerFactory", "entityManagerFactory"})
    public LocalContainerEntityManagerFactoryBean rsTenantEntityManagerFactory(@Qualifier("y9TenantDataSource") DataSource ds, JpaProperties jpaProperties, Environment environment) {
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
    public PlatformTransactionManager rsTenantTransactionManager(@Qualifier("rsTenantEntityManagerFactory") EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    @ConditionalOnMissingBean(value = Y9Context.class)
    public Y9Context y9Context() {
        return new Y9Context();
    }

    @ConfigurationProperties("spring.datasource.druid.y9-public")
    @Bean(name = {"y9PublicDS"})
    @ConditionalOnMissingBean(name = "y9PublicDS")
    public DruidDataSource y9PublicDS() {
        return DruidDataSourceBuilder.create().build();
    }

    @Primary
    @Bean("y9TenantDataSource")
    public DataSource y9TenantDataSource(@Qualifier("defaultDataSource") DruidDataSource defaultDataSource, @Qualifier("y9TenantDataSourceLookup") Y9TenantDataSourceLookup y9TenantDataSourceLookup) {
        Y9TenantDataSource ds = new Y9TenantDataSource();
        ds.setDefaultDataSource(defaultDataSource);
        ds.setDataSourceLookup(y9TenantDataSourceLookup);
        return ds;
    }

    @Bean("y9TenantDataSourceLookup")
    public Y9TenantDataSourceLookup y9TenantDataSourceLookup(@Qualifier("y9PublicDS") DruidDataSource ds, Environment environment) {
        Y9TenantDataSourceLookup lookup = new Y9TenantDataSourceLookup();
        lookup.setPublicDataSource(ds);
        lookup.setSystemName(environment.getProperty("y9.systemName"));
        return lookup;
    }
    
    @Bean
    public OnY9MultiTenantApplicationReady onY9MultiTenantApplicationReady() {
        return new OnY9MultiTenantApplicationReady();
    }

}