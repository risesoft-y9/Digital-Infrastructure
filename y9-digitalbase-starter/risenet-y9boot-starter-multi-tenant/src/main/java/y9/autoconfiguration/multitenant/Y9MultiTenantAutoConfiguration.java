package y9.autoconfiguration.multitenant;

import java.util.Map;

import org.hibernate.integrator.api.integrator.Y9TenantHibernateInfoHolder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
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

import com.zaxxer.hikari.HikariDataSource;

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
import net.risesoft.schema.JpaSchemaUpdaterOnTenantSystemEvent;
import net.risesoft.schema.LiquibaseSchemaUpdaterOnTenantSystemEvent;
import net.risesoft.schema.NoneSchemaUpdaterOnTenantSystemEvent;
import net.risesoft.schema.SchemaUpdaterOnTenantSystemEvent;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.feature.multitenant.Y9MultiTenantProperties;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

/**
 * 多租户自动配置类
 * 
 * @author shidaobang
 * @date 2023/12/05
 * @since 9.6.3
 */
@EnableConfigurationProperties(Y9MultiTenantProperties.class)
@RequiredArgsConstructor
@Slf4j
@AutoConfiguration
public class Y9MultiTenantAutoConfiguration {

    @ConditionalOnMissingBean(name = "y9TenantDataSourceLookup")
    @Bean("y9TenantDataSourceLookup")
    public Y9TenantDataSourceLookup y9TenantDataSourceLookup(@Qualifier("y9PublicDS") HikariDataSource ds,
        Environment environment) {
        return new Y9TenantDataSourceLookup(ds, environment.getProperty("y9.systemName"));
    }

    @Configuration(proxyBeanMethods = false)
    public static class SchemaUpdaterOnTenantSystemEventConfiguration {
        @Bean
        @ConditionalOnBean(name = "y9MultiTenantSpringLiquibase")
        public SchemaUpdaterOnTenantSystemEvent
            liquibaseDbUpdater(Y9MultiTenantSpringLiquibase y9MultiTenantSpringLiquibase) {
            return new LiquibaseSchemaUpdaterOnTenantSystemEvent(y9MultiTenantSpringLiquibase);
        }

        @Bean
        @ConditionalOnMissingBean(name = "liquibaseDbUpdater")
        @ConditionalOnClass(value = Y9TenantHibernateInfoHolder.class)
        public SchemaUpdaterOnTenantSystemEvent jpaDbUpdater() {
            return new JpaSchemaUpdaterOnTenantSystemEvent();
        }

        @Bean
        @ConditionalOnMissingBean(SchemaUpdaterOnTenantSystemEvent.class)
        public SchemaUpdaterOnTenantSystemEvent noneSchemaUpdater() {
            return new NoneSchemaUpdaterOnTenantSystemEvent();
        }
    }

    @Configuration(proxyBeanMethods = false)
    public static class SchemaUpdaterOnApplicationReadyEventConfiguration {
        @Bean
        @ConditionalOnMissingBean(name = "liquibaseDbUpdater")
        @ConditionalOnClass(value = Y9TenantHibernateInfoHolder.class)
        public ApplicationListener<ApplicationReadyEvent>
            jpaSchemaUpdaterOnApplicationReadyEvent(Y9TenantDataSourceLookup y9TenantDataSourceLookup) {
            return applicationReadyEvent -> {
                Map<String, HikariDataSource> map = y9TenantDataSourceLookup.getDataSources();
                for (String tenantId : map.keySet()) {
                    Y9LoginUserHolder.setTenantId(tenantId);
                    Y9TenantHibernateInfoHolder.schemaUpdate(Y9Context.getEnvironment());
                }
            };
        }

        @Bean
        @ConditionalOnBean(name = "y9MultiTenantSpringLiquibase")
        public ApplicationListener<ApplicationReadyEvent>
            liquibaseSchemaUpdaterOnApplicationReadyEvent(Y9MultiTenantSpringLiquibase y9MultiTenantSpringLiquibase) {
            return applicationReadyEvent -> {
                y9MultiTenantSpringLiquibase.updateAll();
            };
        }
    }

    @Configuration(proxyBeanMethods = false)
    public static class EventListenerConfiguration {
        @Bean
        public TenantAppEventListener tenantAppEventListener(MultiTenantDao multiTenantDao) {
            return new TenantAppEventListener(multiTenantDao);
        }

        @Bean
        public TenantSystemRegisteredEventListener tenantSystemRegisteredEventListener(
            Y9TenantDataSourceLookup y9TenantDataSourceLookup,
            SchemaUpdaterOnTenantSystemEvent schemaUpdaterOnTenantSystemEvent, MultiTenantDao multiTenantDao) {
            return new TenantSystemRegisteredEventListener(y9TenantDataSourceLookup, schemaUpdaterOnTenantSystemEvent,
                multiTenantDao);
        }

        @Bean
        public TenantDataSourceEventListener
            tenantDataSourceEventListener(Y9TenantDataSourceLookup y9TenantDataSourceLookup) {
            return new TenantDataSourceEventListener(y9TenantDataSourceLookup);
        }
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
