package y9.autoconfiguration.multitenant;

import org.hibernate.integrator.api.integrator.Y9TenantHibernateInfoHolder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.dao.MultiTenantDao;
import net.risesoft.eventsource.DbScanner;
import net.risesoft.eventsource.KafkaMessageCommon;
import net.risesoft.liquibase.Y9MultiTenantSpringLiquibase;
import net.risesoft.listener.MultiTenantApplicationReadyListener;
import net.risesoft.listener.TenantAppEventListener;
import net.risesoft.listener.TenantDataSourceEventListener;
import net.risesoft.listener.TenantSystemRegisteredEventListener;
import net.risesoft.schema.JpaSchemaUpdater;
import net.risesoft.schema.LiquibaseSchemaUpdater;
import net.risesoft.schema.NoneSchemaUpdater;
import net.risesoft.schema.SchemaUpdater;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

/**
 * 多租户自动配置类
 * 
 * @author shidaobang
 * @date 2023/12/05
 * @since 9.6.3
 */
@EnableConfigurationProperties(Y9Properties.class)
@RequiredArgsConstructor
@Slf4j
@AutoConfiguration
public class Y9MultiTenantAutoConfiguration {

    @ConditionalOnMissingBean(name = "y9TenantDataSourceLookup")
    @Bean("y9TenantDataSourceLookup")
    public Y9TenantDataSourceLookup y9TenantDataSourceLookup(@Qualifier("y9PublicDS") DruidDataSource ds,
        Environment environment) {
        return new Y9TenantDataSourceLookup(ds, environment.getProperty("y9.systemName"));
    }

    @Bean
    @ConditionalOnBean(name = "y9MultiTenantSpringLiquibase")
    public SchemaUpdater liquibaseDbUpdater(Y9MultiTenantSpringLiquibase y9MultiTenantSpringLiquibase) {
        return new LiquibaseSchemaUpdater(y9MultiTenantSpringLiquibase);
    }

    @Bean
    @ConditionalOnMissingBean(name = "liquibaseDbUpdater")
    @ConditionalOnClass(value = Y9TenantHibernateInfoHolder.class)
    public SchemaUpdater jpaDbUpdater() {
        return new JpaSchemaUpdater();
    }

    @Bean
    @ConditionalOnMissingBean(SchemaUpdater.class)
    public SchemaUpdater noneSchemaUpdater() {
        return new NoneSchemaUpdater();
    }

    @Bean
    public TenantAppEventListener tenantAppEventListener(MultiTenantDao multiTenantDao) {
        return new TenantAppEventListener(multiTenantDao);
    }

    @Bean
    public TenantSystemRegisteredEventListener tenantSystemRegisteredEventListener(
        Y9TenantDataSourceLookup y9TenantDataSourceLookup, SchemaUpdater schemaUpdater, MultiTenantDao multiTenantDao) {
        return new TenantSystemRegisteredEventListener(y9TenantDataSourceLookup, schemaUpdater, multiTenantDao);
    }

    @Bean
    public TenantDataSourceEventListener
        tenantDataSourceEventListener(Y9TenantDataSourceLookup y9TenantDataSourceLookup) {
        return new TenantDataSourceEventListener(y9TenantDataSourceLookup);
    }

    @Bean
    public MultiTenantDao multiTenantDao(@Qualifier("jdbcTemplate4Public") JdbcTemplate jdbcTemplate4Public) {
        return new MultiTenantDao(jdbcTemplate4Public);
    }

    @AutoConfiguration(after = KafkaAutoConfiguration.class)
    @ConditionalOnBean(ProducerFactory.class)
    @ConditionalOnProperty(prefix = "y9.feature.multi-tenant", name = "event-source", havingValue = "kafka",
        matchIfMissing = true)
    public static class KafkaConfiguration {

        @Bean("y9KafkaTemplate")
        @Primary
        @ConditionalOnMissingBean(name = "y9KafkaTemplate")
        public KafkaTemplate<?, ?> y9KafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory) {
            return new KafkaTemplate<>(kafkaProducerFactory);
        }

        @Bean
        public KafkaMessageCommon messageCommonListener() {
            return new KafkaMessageCommon();
        }

        @Bean
        public MultiTenantApplicationReadyListener multiTenantApplicationReadyListener(MultiTenantDao multiTenantDao,
            Y9Properties y9Properties) {
            return new MultiTenantApplicationReadyListener(multiTenantDao, y9Properties);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "y9.feature.multi-tenant", name = "event-source", havingValue = "db")
    public static class DbConfiguration {
        @Bean
        public DbScanner dbScanner(Y9TenantDataSourceLookup y9TenantDataSourceLookup, MultiTenantDao multiTenantDao) {
            return new DbScanner(y9TenantDataSourceLookup, multiTenantDao);
        }

        @EnableScheduling
        @Configuration(proxyBeanMethods = false)
        static class ScheduledConfiguration implements SchedulingConfigurer {
            // 每分钟执行一次
            private final String scanCron = "0 * * * * *";

            private final DbScanner dbScanner;

            public ScheduledConfiguration(DbScanner dbScanner) {
                this.dbScanner = dbScanner;
            }

            @Override
            public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
                taskRegistrar.addCronTask(this.dbScanner::scan, this.scanCron);
            }
        }

    }
}
