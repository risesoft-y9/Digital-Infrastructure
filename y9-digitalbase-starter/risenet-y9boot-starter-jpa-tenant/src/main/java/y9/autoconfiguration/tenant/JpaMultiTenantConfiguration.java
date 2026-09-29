
package y9.autoconfiguration.tenant;

import static net.risesoft.consts.JpaTenantConsts.TENANT_TRANSACTION_MANAGER;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
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
@EnableConfigurationProperties({JpaProperties.class, HibernateProperties.class})
@EnableTransactionManagement(proxyTargetClass = true, mode = AdviceMode.ASPECTJ)
@Y9EnableJpaRepositories(basePackages = {"${y9.feature.jpa.packagesToScanRepositoryTenant}"},
    includeFilters = {@ComponentScan.Filter(classes = JpaRepository.class, type = FilterType.ASSIGNABLE_TYPE)},
    entityManagerFactoryRef = "rsTenantEntityManagerFactory", transactionManagerRef = TENANT_TRANSACTION_MANAGER)
@Slf4j
public class JpaMultiTenantConfiguration {

    @Bean("defaultDataSource")
    @ConditionalOnMissingBean(name = "defaultDataSource")
    public DruidDataSource defaultDataSource(Environment environment) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        dataSource.setName("defaultDataSource");

        // 比如：spring.datasource.druid.tenantDefault=spring.datasource.druid.y9-public
        String prefix = environment.getProperty("spring.datasource.druid.tenantDefault", String.class);
        if (!StringUtils.hasText(prefix)) {
            prefix = "spring.datasource.druid.y9-public";
        }

        Binder binder = Binder.get(environment);
        binder.bind(prefix, Bindable.ofInstance(dataSource));

        return dataSource;
    }

    @Bean("jdbcTemplate4Tenant")
    @ConditionalOnMissingBean(name = "jdbcTemplate4Tenant")
    public JdbcTemplate jdbcTemplate4Tenant(@Qualifier("y9TenantDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean
    @ConditionalOnMissingBean
    public JpaVendorAdapter jpaVendorAdapter(JpaProperties jpaProperties) {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(jpaProperties.isShowSql());
        if (jpaProperties.getDatabase() != null) {
            adapter.setDatabase(jpaProperties.getDatabase());
        }
        if (jpaProperties.getDatabasePlatform() != null) {
            adapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());
        }
        adapter.setGenerateDdl(jpaProperties.isGenerateDdl());
        return adapter;
    }

    @Primary
    @Bean({"rsTenantEntityManagerFactory", "entityManagerFactory"})
    public LocalContainerEntityManagerFactoryBean rsTenantEntityManagerFactory(
        @Qualifier("y9TenantDataSource") DataSource ds, JpaProperties jpaProperties,
        HibernateProperties hibernateProperties, JpaVendorAdapter jpaVendorAdapter, Environment environment) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName("y9Tenant");
        em.setDataSource(ds);
        em.setJpaVendorAdapter(jpaVendorAdapter);

        String basePackages = environment.getProperty("y9.feature.jpa.packagesToScanEntityTenant");
        em.setPackagesToScan(basePackages.split(","));
        em.setJpaPropertyMap(
            hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings()));
        // EntityManagerFactory 初始化过程不触发表结构更新及数据库元数据的获取
        em.getJpaPropertyMap().put("hibernate.hbm2ddl.auto", "none");
        em.getJpaPropertyMap().put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        return em;
    }

    @Primary
    @Bean({TENANT_TRANSACTION_MANAGER, "transactionManager"})
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

    @ConfigurationProperties("spring.datasource.druid.y9-public")
    @Bean(name = {"y9PublicDS"})
    @ConditionalOnMissingBean(name = "y9PublicDS")
    public DruidDataSource y9PublicDS() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = {"jdbcTemplate4Public"})
    @ConditionalOnMissingBean(name = "jdbcTemplate4Public")
    public JdbcTemplate jdbcTemplate4Public(@Qualifier("y9PublicDS") DruidDataSource y9PublicDS) {
        return new JdbcTemplate(y9PublicDS);
    }

    @Primary
    @Bean("y9TenantDataSource")
    @ConditionalOnMissingBean(name = "y9TenantDataSource")
    public DataSource y9TenantDataSource(@Qualifier("defaultDataSource") DruidDataSource defaultDataSource,
        @Qualifier("y9TenantDataSourceLookup") Y9TenantDataSourceLookup y9TenantDataSourceLookup) {
        return new Y9TenantDataSource(defaultDataSource, y9TenantDataSourceLookup);
    }

    @Bean("y9TenantDataSourceLookup")
    @ConditionalOnMissingBean(name = "y9TenantDataSourceLookup")
    public Y9TenantDataSourceLookup y9TenantDataSourceLookup(@Qualifier("y9PublicDS") DruidDataSource ds,
        Environment environment) {
        return new Y9TenantDataSourceLookup(ds, environment.getProperty("y9.systemName"));
    }
}