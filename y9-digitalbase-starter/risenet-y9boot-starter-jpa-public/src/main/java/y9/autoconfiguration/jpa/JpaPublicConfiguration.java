package y9.autoconfiguration.jpa;

import javax.persistence.EntityManagerFactory;

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

import net.risesoft.y9.Y9Context;

import y9.jpa.extension.Y9EnableJpaRepositories;

@Configuration
@AutoConfigureBefore(DruidDataSourceAutoConfigure.class)
@EnableConfigurationProperties(JpaProperties.class)
@EnableTransactionManagement(proxyTargetClass = true, mode = AdviceMode.ASPECTJ)
@Y9EnableJpaRepositories(basePackages = {"${y9.feature.jpa.packagesToScanRepositoryPublic}"},
    includeFilters = {@ComponentScan.Filter(classes = JpaRepository.class, type = FilterType.ASSIGNABLE_TYPE)},
    entityManagerFactoryRef = "rsPublicEntityManagerFactory", transactionManagerRef = "rsPublicTransactionManager")
public class JpaPublicConfiguration {

    @Bean(name = {"jdbcTemplate4Public"})
    @ConditionalOnMissingBean(name = "jdbcTemplate4Public")
    public JdbcTemplate jdbcTemplate4Public(@Qualifier("y9PublicDS") DruidDataSource y9PublicDS) {
        return new JdbcTemplate(y9PublicDS);
    }

    @Bean
    @ConditionalOnMissingBean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean rsPublicEntityManagerFactory(
        @Qualifier("y9PublicDS") DruidDataSource y9PublicDS, JpaProperties jpaProperties, Environment environment) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName("y9Public");
        em.setDataSource(y9PublicDS);
        em.setJpaVendorAdapter(jpaVendorAdapter());

        String basePackages = environment.getProperty("y9.feature.jpa.packagesToScanEntityPublic");
        em.setPackagesToScan(basePackages.split(","));
        em.setJpaPropertyMap(jpaProperties.getProperties());
        return em;
    }

    @Bean
    public PlatformTransactionManager
        rsPublicTransactionManager(@Qualifier("rsPublicEntityManagerFactory") EntityManagerFactory emf) {
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
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return dataSource;
    }
}
