package y9.autoconfiguration.jpa;

import jakarta.persistence.EntityManagerFactory;
import y9.jpa.extension.Y9EnableJpaRepositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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

import com.zaxxer.hikari.HikariDataSource;

import net.risesoft.y9.Y9Context;

@Configuration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(JpaProperties.class)
@EnableTransactionManagement(proxyTargetClass = true, mode = AdviceMode.ASPECTJ)
@Y9EnableJpaRepositories(basePackages = {"${y9.feature.jpa.packagesToScanRepositoryDedicated}"},
    includeFilters = {@ComponentScan.Filter(classes = JpaRepository.class, type = FilterType.ASSIGNABLE_TYPE)},
    entityManagerFactoryRef = "rsDedicatedEntityManagerFactory",
    transactionManagerRef = JpaDedicatedConfiguration.TRANSACTION_MANAGER)
public class JpaDedicatedConfiguration {

    public static final String TRANSACTION_MANAGER = "rsDedicatedTransactionManager";

    @Autowired
    private Environment environment;

    @Autowired
    private JpaProperties jpaProperties;

    @Bean(name = {"jdbcTemplate4Dedicated"})
    @ConditionalOnMissingBean(name = "jdbcTemplate4Dedicated")
    public JdbcTemplate jdbcTemplate4Dedicated(@Qualifier("y9DedicatedDS") HikariDataSource y9DedicatedDS) {
        return new JdbcTemplate(y9DedicatedDS);
    }

    @Bean
    @ConditionalOnMissingBean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean
        rsDedicatedEntityManagerFactory(@Qualifier("y9DedicatedDS") HikariDataSource y9DedicatedDS) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName("y9Dedicated");
        em.setDataSource(y9DedicatedDS);
        em.setJpaVendorAdapter(jpaVendorAdapter());

        String basePackages = environment.getProperty("y9.feature.jpa.packagesToScanEntityDedicated");
        em.setPackagesToScan(basePackages.split(","));
        em.setJpaPropertyMap(jpaProperties.getProperties());
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
    public HikariDataSource y9DedicatedDS() {
        HikariDataSource dataSource = new HikariDataSource();
        String name = environment.getProperty("spring.application.name", String.class);
        String prefix = "spring.datasource.hikari." + name + ".";

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
}
