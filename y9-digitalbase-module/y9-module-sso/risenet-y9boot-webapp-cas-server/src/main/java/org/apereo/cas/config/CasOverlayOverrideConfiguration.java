package org.apereo.cas.config;

//import org.springframework.boot.context.properties.EnableConfigurationProperties;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.jpa.JpaPersistenceProviderConfigurer;
import org.apereo.cas.services.JpaRegisteredServiceEntity;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.spring.beans.BeanCondition;
import org.apereo.cas.util.spring.beans.BeanSupplier;
import org.apereo.cas.web.CasWebSecurityConfigurer;
import org.apereo.cas.web.cookie.CasCookieBuilder;
import org.apereo.cas.web.y9.OnApplicationReady;
import org.apereo.cas.web.y9.controller.*;
import org.apereo.cas.services.y9.Y9LoginUser;
import org.apereo.cas.services.y9.Y9User;
import org.apereo.cas.web.y9.service.Y9KeyValueService;
import org.apereo.cas.web.y9.service.impl.Y9KeyValueServiceImpl;
import org.apereo.cas.web.y9.service.impl.Y9LoginUserServiceImpl;
import org.apereo.cas.web.y9.service.impl.Y9UserServiceImpl;
import org.apereo.cas.web.y9.util.Y9Context;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScopedProxyMode;
import org.apereo.cas.web.y9.authen.Y9AuthenticationHandler;
import org.apereo.cas.web.y9.service.Y9LoginUserService;
import org.apereo.cas.web.y9.service.Y9UserService;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.util.List;

//import org.apereo.cas.configuration.CasConfigurationProperties;

@AutoConfiguration
//@ComponentScan("org.apereo.cas.web.y9")
//@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CasOverlayOverrideConfiguration {

    @Configuration
    static class Y9JpaConfig {
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

    @Configuration(proxyBeanMethods = false)
    static class Y9AuthenticationConfiguration {
        @Bean
        @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
        public AuthenticationEventExecutionPlanConfigurer riseAuthenticationEventExecutionPlanConfigurer(
                @Qualifier(PrincipalResolver.BEAN_NAME_PRINCIPAL_RESOLVER)
                final PrincipalResolver defaultPrincipalResolver,
                @Qualifier(ServicesManager.BEAN_NAME)
                final ServicesManager servicesManager,
                Y9UserService y9UserService,
                Y9LoginUserService y9LoginUserService) {
            Y9AuthenticationHandler handler = new Y9AuthenticationHandler("y9AuthenticationHandler",
                    servicesManager, 0, y9UserService, y9LoginUserService);
            return plan -> plan.registerAuthenticationHandler(handler);
        }
    }

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

    @Bean
    @ConditionalOnMissingBean(value = Y9Context.class)
    public Y9Context y9Context() {
        return new Y9Context();
    }

    @Bean
    @ConditionalOnMissingBean(name = "y9UserService")
    public Y9UserService y9UserService() {
        return new Y9UserServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(name = "y9LoginUserService")
    public Y9LoginUserService y9LoginUserService() {
        return new Y9LoginUserServiceImpl();
    }

    @Bean
    public Y9KeyValueService y9KeyValueService(
            @Qualifier("jdbcServiceRegistryTransactionTemplate") TransactionOperations transactionTemplate) {
        return new Y9KeyValueServiceImpl(transactionTemplate);
    }

    @Bean
    public QRCodeController qRCodeController(Y9KeyValueService y9KeyValueService) {
        return new QRCodeController(y9KeyValueService);
    }

    @Bean
    public LoginController loginController(CentralAuthenticationService centralAuthenticationService,
                                           @Qualifier("ticketGrantingTicketCookieGenerator") CasCookieBuilder ticketGrantingTicketCookieGenerator,
                                           @Qualifier("defaultAuthenticationSystemSupport") AuthenticationSystemSupport authenticationSystemSupport,
                                           @Qualifier("webApplicationServiceFactory") ServiceFactory webApplicationServiceFactory,
                                           Y9UserService y9UserService) {
        return new LoginController(centralAuthenticationService,
                ticketGrantingTicketCookieGenerator,
                authenticationSystemSupport,
                webApplicationServiceFactory,
                y9UserService);
    }

    @Bean
    public ServiceController serviceController(ServicesManager servicesManager,
                                               CasConfigurationProperties casConfigurationProperties) {
        return new ServiceController(servicesManager, casConfigurationProperties);
    }

    @Bean
    public TenantController tenantController(Y9UserService y9UserService) {
        return new TenantController(y9UserService);
    }

    @Bean
    public CheckController checkController() {
        return new CheckController();
    }

    /*@Bean
    public OnApplicationReady onApplicationReady(Y9UserService y9UserService) {
        return new OnApplicationReady(y9UserService);
    }*/


    @Bean
    public Runnable Y9KeyValueCleaner(Y9KeyValueService y9KeyValueService) {
        return new Y9KeyValueCleaner(y9KeyValueService);
    }

    @Slf4j
    @RequiredArgsConstructor
    @EnableScheduling
    static class Y9KeyValueCleaner implements Runnable {
        private final Y9KeyValueService y9KeyValueService;

        @Override
        @Scheduled(
                cron = "0 * * * * *"
        )
        public void run() {
            //Y9KeyValueService y9KeyValueService = Y9Context.getBean(Y9KeyValueService.class);
            y9KeyValueService.cleanUpExpiredKeyValue();
        }
    }

}
