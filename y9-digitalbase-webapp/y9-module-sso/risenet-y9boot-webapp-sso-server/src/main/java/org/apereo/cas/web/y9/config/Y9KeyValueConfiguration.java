package org.apereo.cas.web.y9.config;

import javax.sql.DataSource;

import org.apereo.cas.web.y9.service.Y9KeyValueService;
import org.apereo.cas.web.y9.service.impl.Y9JpaKeyValueServiceImpl;
import org.apereo.cas.web.y9.service.impl.Y9RedisKeyValueServiceImpl;
import org.apereo.cas.web.y9.util.Y9Context;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionOperations;

@Configuration(proxyBeanMethods = false)
public class Y9KeyValueConfiguration {

    @Bean
    public Y9Context y9Context() {
        return new Y9Context();
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = "cas.ticket.registry.redis.enabled", havingValue = "true", matchIfMissing = false)
    public static class Y9RedisKeyValueConfiguration {

        @Bean
        public RedisTemplate<Object, Object> y9RedisTemplate(
            @Qualifier("redisTicketConnectionFactory") final RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(redisConnectionFactory);
            return redisTemplate;
        }

        @Bean
        public Y9KeyValueService
            y9RedisKeyValueService(@Qualifier("y9RedisTemplate") RedisTemplate<Object, Object> y9RedisTemplate) {
            return new Y9RedisKeyValueServiceImpl(y9RedisTemplate);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = "cas.ticket.registry.jpa.enabled", havingValue = "true", matchIfMissing = false)
    public static class Y9JpaKeyValueConfiguration {

        @Bean
        public Y9KeyValueService y9JpaKeyValueService(
            @Qualifier("jdbcServiceRegistryTransactionTemplate") TransactionOperations transactionTemplate) {
            return new Y9JpaKeyValueServiceImpl(transactionTemplate);
        }

        @EnableScheduling
        @Configuration(proxyBeanMethods = false)
        class Y9KeyValueCleanupConfiguration implements SchedulingConfigurer {
            // 每分钟执行一次
            private final String cleanupCron = "0 * * * * *";

            private final Y9KeyValueService y9KeyValueService;

            public Y9KeyValueCleanupConfiguration(Y9KeyValueService y9KeyValueService) {
                this.y9KeyValueService = y9KeyValueService;
            }

            @Override
            public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
                taskRegistrar.addCronTask(this.y9KeyValueService::cleanUpExpiredKeyValue, this.cleanupCron);
            }

        }
    }
}
