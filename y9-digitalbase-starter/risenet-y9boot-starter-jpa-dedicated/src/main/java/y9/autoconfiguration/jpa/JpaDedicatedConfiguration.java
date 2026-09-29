package y9.autoconfiguration.jpa;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

import lombok.RequiredArgsConstructor;

import net.risesoft.y9.Y9Context;

import y9.jpa.extension.Y9EnableJpaRepositories;

@Configuration
@AutoConfigureBefore(DruidDataSourceAutoConfigure.class)
@EnableConfigurationProperties({JpaProperties.class, HibernateProperties.class})
@EnableTransactionManagement(proxyTargetClass = true, mode = AdviceMode.ASPECTJ)
@Y9EnableJpaRepositories(basePackages = {"${y9.feature.jpa.packagesToScanRepositoryDedicated}"},
    includeFilters = {@ComponentScan.Filter(classes = JpaRepository.class, type = FilterType.ASSIGNABLE_TYPE)},
    entityManagerFactoryRef = "rsDedicatedEntityManagerFactory",
    transactionManagerRef = JpaDedicatedConfiguration.TRANSACTION_MANAGER)
@RequiredArgsConstructor
public class JpaDedicatedConfiguration {

    public static final String TRANSACTION_MANAGER = "rsDedicatedTransactionManager";

    private final Environment environment;

    private final JpaProperties jpaProperties;

    @Bean(name = {"jdbcTemplate4Dedicated"})
    @ConditionalOnMissingBean(name = "jdbcTemplate4Dedicated")
    public JdbcTemplate jdbcTemplate4Dedicated(@Qualifier("y9DedicatedDS") DruidDataSource y9DedicatedDS) {
        return new JdbcTemplate(y9DedicatedDS);
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

    @Bean
    public LocalContainerEntityManagerFactoryBean rsDedicatedEntityManagerFactory(
        @Qualifier("y9DedicatedDS") DruidDataSource y9DedicatedDS, HibernateProperties hibernateProperties,
        JpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName("y9Dedicated");
        em.setDataSource(y9DedicatedDS);
        em.setJpaVendorAdapter(jpaVendorAdapter);

        String basePackages = environment.getProperty("y9.feature.jpa.packagesToScanEntityDedicated");
        em.setPackagesToScan(basePackages.split(","));
        em.setJpaPropertyMap(
            hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings()));
        return em;
    }

    @Bean
    public PlatformTransactionManager
        rsDedicatedTransactionManager(@Qualifier("rsDedicatedEntityManagerFactory") EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    @ConditionalOnMissingBean(value = Y9Context.class)
    public Y9Context y9Context() {
        return new Y9Context();
    }

    @Bean(name = {"y9DedicatedDS"})
    @ConditionalOnMissingBean(name = "y9DedicatedDS")
    public DruidDataSource y9DedicatedDS() {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        dataSource.setName("y9DedicatedDS");

        String applicationName = environment.getRequiredProperty("spring.application.name");
        String prefix = "spring.datasource.druid." + applicationName;

        Binder binder = Binder.get(environment);
        binder.bind(prefix, Bindable.ofInstance(dataSource));

        return dataSource;
    }
}
