package y9;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true, mode = AdviceMode.ASPECTJ)
@EnableJpaRepositories(basePackages = {"y9"},
    includeFilters = {@ComponentScan.Filter(classes = JpaRepository.class, type = FilterType.ASSIGNABLE_TYPE)},
    entityManagerFactoryRef = "rsPublicEntityManagerFactory", transactionManagerRef = "rsPublicTransactionManager")
public class JpaPublicConfiguration {

    @Autowired
    JpaProperties jpaProperties;

    @ConfigurationProperties("spring.datasource.hikari.y9-public")
    @Bean("y9PublicDS")
    @Primary
    public HikariDataSource y9PublicDS() {
        return new HikariDataSource();
    }

    @Bean
    @Primary
    public JpaVendorAdapter rsJpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean rsPublicEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName("y9Public");
        em.setDataSource(y9PublicDS());
        em.setJpaVendorAdapter(rsJpaVendorAdapter());
        em.setPackagesToScan("y9");
        em.setJpaPropertyMap(jpaProperties.getProperties());
        return em;
    }

    @Primary
    @Bean
    public PlatformTransactionManager rsPublicTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(rsPublicEntityManagerFactory().getObject());
        return transactionManager;
    }

}
