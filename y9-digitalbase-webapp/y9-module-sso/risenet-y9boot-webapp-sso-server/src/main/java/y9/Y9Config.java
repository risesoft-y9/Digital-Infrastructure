package y9;

import java.util.List;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.RiseAuthenticationHandler;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.cas.authentication.principal.RisePersonDirectoryPrincipalResolver;
import org.apereo.cas.jpa.JpaPersistenceProviderConfigurer;
import org.apereo.cas.services.JpaRegisteredServiceEntity;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.services.Y9LoginUser;
import org.apereo.cas.services.Y9User;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.spring.beans.BeanCondition;
import org.apereo.cas.util.spring.beans.BeanSupplier;
import org.apereo.cas.web.CasWebSecurityConfigurer;
import org.apereo.cas.web.flow.actions.RiseCredentialNonInteractiveCredentialsAction;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.webflow.execution.Action;

import lombok.val;

import y9.service.Y9KeyValueService;
import y9.service.impl.Y9JpaKeyValueServiceImpl;
import y9.service.impl.Y9RedisKeyValueServiceImpl;
import y9.util.Y9Context;

@Lazy(false)
@Configuration(proxyBeanMethods = false)
public class Y9Config {

    @Configuration
    public static class Y9JpaConfig {
        private static final BeanCondition CONDITION =
            BeanCondition.on("cas.service-registry.jpa.enabled").isTrue().evenIfMissing();

        @Bean
        @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
        public JpaPersistenceProviderConfigurer
            jpaServicePersistenceProviderConfigurer(final ConfigurableApplicationContext applicationContext) {
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
        @Bean
        public CasWebSecurityConfigurer<Void> y9ResourceConfigurer() {
            return new CasWebSecurityConfigurer<>() {
                @Override
                public List<String> getIgnoredEndpoints() {
                    return List.of("/y9static", "/api");
                }
            };
        }
    }

    @Configuration
    public static class Y9AuthenticationConfiguration {
        @Bean
        public AuthenticationEventExecutionPlanConfigurer riseAuthenticationEventExecutionPlanConfigurer(
            @Qualifier(ServicesManager.BEAN_NAME) final ServicesManager servicesManager) {
            RiseAuthenticationHandler handler =
                new RiseAuthenticationHandler("y9AuthenticationHandler", servicesManager, risePrincipalFactory(), 0);
            return plan -> plan.registerAuthenticationHandlerWithPrincipalResolver(handler,
                risePersonDirectoryPrincipalResolver());
        }

        @Bean
        public Action riseCredentialNonInteractiveCredentialsAction(
            @Qualifier("adaptiveAuthenticationPolicy") final AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy,
            @Qualifier("serviceTicketRequestWebflowEventResolver") final CasWebflowEventResolver serviceTicketRequestWebflowEventResolver,
            @Qualifier("initialAuthenticationAttemptWebflowEventResolver") final CasDelegatingWebflowEventResolver initialAuthenticationAttemptWebflowEventResolver) {
            return new RiseCredentialNonInteractiveCredentialsAction(initialAuthenticationAttemptWebflowEventResolver,
                serviceTicketRequestWebflowEventResolver, adaptiveAuthenticationPolicy);
        }

        @Bean
        public PrincipalResolver risePersonDirectoryPrincipalResolver() {
            return new RisePersonDirectoryPrincipalResolver();
        }

        @Bean
        public PrincipalFactory risePrincipalFactory() {
            return new DefaultPrincipalFactory();
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
