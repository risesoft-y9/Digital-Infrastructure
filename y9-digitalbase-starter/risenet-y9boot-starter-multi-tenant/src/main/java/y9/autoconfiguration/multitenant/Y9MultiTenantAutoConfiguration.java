package y9.autoconfiguration.multitenant;

import org.hibernate.integrator.api.integrator.Y9TenantHibernateInfoHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.init.TenantAppInitializer;
import net.risesoft.init.TenantDataInitializer;
import net.risesoft.kafka.MessageCommonListener;
import net.risesoft.liquibase.Y9MultiTenantSpringLiquibase;
import net.risesoft.listener.TenantAppEventListener;
import net.risesoft.schema.JpaSchemaUpdater;
import net.risesoft.schema.LiquibaseSchemaUpdater;
import net.risesoft.schema.NoneSchemaUpdater;
import net.risesoft.schema.SchemaUpdater;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

/**
 * @author shidaobang
 * @date 2023/12/05
 * @since 9.6.3
 */
@AutoConfiguration(after = KafkaAutoConfiguration.class)
@EnableConfigurationProperties(Y9Properties.class)
@RequiredArgsConstructor
@Slf4j
public class Y9MultiTenantAutoConfiguration {

    @Bean("y9KafkaTemplate")
    @Primary
    @ConditionalOnMissingBean(name = "y9KafkaTemplate")
    public KafkaTemplate<?, ?> y9KafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory) {
        return new KafkaTemplate<>(kafkaProducerFactory);
    }

    @ConditionalOnMissingBean(name = "y9TenantDataSourceLookup")
    @Bean("y9TenantDataSourceLookup")
    public Y9TenantDataSourceLookup y9TenantDataSourceLookup(@Qualifier("y9PublicDS") HikariDataSource ds,
        Environment environment) {
        return new Y9TenantDataSourceLookup(ds, environment.getProperty("y9.systemName"));
    }

    @Bean
    @ConditionalOnBean(name = "y9MultiTenantSpringLiquibase")
    public SchemaUpdater liquibaseDbUpdater(Y9TenantDataSourceLookup y9TenantDataSourceLookup,
        Y9MultiTenantSpringLiquibase y9MultiTenantSpringLiquibase,
        @Autowired(required = false) TenantDataInitializer tenantDataInitializer,
        KafkaTemplate<String, String> y9KafkaTemplate) {
        return new LiquibaseSchemaUpdater(y9TenantDataSourceLookup, y9KafkaTemplate, y9MultiTenantSpringLiquibase,
            tenantDataInitializer);
    }

    @Bean
    @ConditionalOnMissingBean(name = "liquibaseDbUpdater")
    @ConditionalOnClass(value = Y9TenantHibernateInfoHolder.class)
    public SchemaUpdater jpaDbUpdater(Y9TenantDataSourceLookup y9TenantDataSourceLookup,
        @Autowired(required = false) TenantDataInitializer tenantDataInitializer,
        KafkaTemplate<String, String> y9KafkaTemplate) {
        return new JpaSchemaUpdater(y9TenantDataSourceLookup, y9KafkaTemplate, tenantDataInitializer);
    }

    @Bean
    @ConditionalOnMissingBean(name = "jpaDbUpdater")
    public SchemaUpdater noneSchemaUpdater(Y9TenantDataSourceLookup y9TenantDataSourceLookup,
        @Autowired(required = false) TenantDataInitializer tenantDataInitializer,
        KafkaTemplate<String, String> y9KafkaTemplate) {
        return new NoneSchemaUpdater(y9TenantDataSourceLookup, tenantDataInitializer, y9KafkaTemplate);
    }

    @Bean
    public MessageCommonListener messageCommonListener(Y9TenantDataSourceLookup y9TenantDataSourceLookup) {
        return new MessageCommonListener(y9TenantDataSourceLookup);
    }

    @Bean
    public TenantAppEventListener
        tenantAppEventListener(@Autowired(required = false) TenantAppInitializer tenantAppInitializer) {
        return new TenantAppEventListener(tenantAppInitializer);
    }

}
