package org.apereo.cas.config;

import jakarta.servlet.DispatcherType;
import lombok.val;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.RiseAuthenticationHandler;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.jpa.JpaPersistenceProviderConfigurer;
import org.apereo.cas.services.JpaRegisteredServiceEntity;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.services.Y9LoginUser;
import org.apereo.cas.services.Y9User;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.spring.beans.BeanCondition;
import org.apereo.cas.util.spring.beans.BeanSupplier;
import org.apereo.cas.web.CasWebSecurityConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.Ordered;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.web.filter.ForwardedHeaderFilter;
import y9.service.Y9KeyValueService;
import y9.service.Y9LoginUserService;
import y9.service.Y9UserService;
import y9.service.impl.Y9JpaKeyValueServiceImpl;
import y9.service.impl.Y9RedisKeyValueServiceImpl;
import y9.util.Y9Context;

import java.util.List;


@AutoConfiguration
@ComponentScan("y9")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CasOverlayOverrideConfiguration {

    @Configuration
    public static class Y9JpaConfig {
        private static final BeanCondition CONDITION =
                BeanCondition.on("cas.service-registry.jpa.enabled").isTrue().evenIfMissing();

        @Bean
        @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
        public JpaPersistenceProviderConfigurer jpaServicePersistenceProviderConfigurer(final ConfigurableApplicationContext applicationContext) {
            return BeanSupplier.of(JpaPersistenceProviderConfigurer.class)
                    .when(CONDITION.given(applicationContext.getEnvironment())).supply(() -> context -> {
                        String s1 = JpaRegisteredServiceEntity.class.getName();
                        String s2 = Y9LoginUser.class.getName();
                        String s3 = Y9User.class.getName();
                        val entities = CollectionUtils.wrapList(s1, s2, s3);
                        context.getIncludeEntityClasses().addAll(entities);
                    }).otherwiseProxy().get();
        }
    }

    @Configuration
    public static class Y9CasWebSecurityConfigurer {

        /**
         * 针对经过反向代理的请求不能正确获得一些原始的请求信息，例如不能正确获得原始的 schema 导致重定向的 url 错误 <br/>
         * 此过滤器更多是为了减少外部 servlet 容器的配置 <br/>
         *
         * @return {@code FilterRegistrationBean<ForwardedHeaderFilter> }
         * @see <a href="https://docs.spring.io/spring-security/reference/servlet/appendix/proxy-server.html">Proxy
         * Server Configuration</a>
         */
        @Bean
        @ConditionalOnWarDeployment
        FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
            ForwardedHeaderFilter filter = new ForwardedHeaderFilter();
            FilterRegistrationBean<ForwardedHeaderFilter> registration = new FilterRegistrationBean<>(filter);
            registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR);
            registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
            return registration;
        }

        @Bean
        public CasWebSecurityConfigurer<Void> y9ResourceConfigurer() {
            return new CasWebSecurityConfigurer<>() {
                @Override
                public List<String> getIgnoredEndpoints() {
                    return List.of("/y9static/**", "/api/**");
                }
            };
        }
    }

    @Configuration
    public static class Y9AuthenticationConfiguration {
        @Bean
        public AuthenticationEventExecutionPlanConfigurer riseAuthenticationEventExecutionPlanConfigurer(
                @Qualifier(ServicesManager.BEAN_NAME) final ServicesManager servicesManager, Y9UserService y9UserService,
                Y9LoginUserService y9LoginUserService) {
            RiseAuthenticationHandler handler = new RiseAuthenticationHandler("y9AuthenticationHandler",
                    servicesManager, 0, y9UserService, y9LoginUserService);
            return plan -> plan.registerAuthenticationHandler(handler);
        }
    }

    @Configuration(proxyBeanMethods = false)
    public static class Y9KeyValueConfiguration {

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

}
