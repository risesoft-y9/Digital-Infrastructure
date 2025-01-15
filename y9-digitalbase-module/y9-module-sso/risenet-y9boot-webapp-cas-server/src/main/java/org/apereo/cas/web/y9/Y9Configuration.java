package org.apereo.cas.web.y9;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.authentication.principal.Y9PrincipalResolver;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.CasWebSecurityConfigurer;
import org.apereo.cas.web.cookie.CasCookieBuilder;
import org.apereo.cas.web.y9.authen.Y9AuthenticationHandler;
import org.apereo.cas.web.y9.controller.*;
import org.apereo.cas.web.y9.service.Y9KeyValueService;
import org.apereo.cas.web.y9.service.Y9LoginUserService;
import org.apereo.cas.web.y9.service.Y9UserService;
import org.apereo.cas.web.y9.service.impl.Y9KeyValueServiceImpl;
import org.apereo.cas.web.y9.service.impl.Y9LoginUserServiceImpl;
import org.apereo.cas.web.y9.service.impl.Y9UserServiceImpl;
import org.apereo.cas.web.y9.util.Y9Context;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.util.List;

@Lazy(false)
@Configuration(proxyBeanMethods = true)
public class Y9Configuration {

    @Bean
    @ConditionalOnMissingBean(value = Y9Context.class)
    public Y9Context y9Context() {
        return new Y9Context();
    }

    @Bean
    public OnApplicationReady onApplicationReady(Y9UserService y9UserService) {
        return new OnApplicationReady(y9UserService);
    }

    @Bean
    @ConditionalOnMissingBean(name = "y9UserService")
    public Y9UserService y9UserService(
            @Qualifier("jdbcServiceRegistryTransactionTemplate")
            TransactionOperations transactionTemplate) {
        return new Y9UserServiceImpl(transactionTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(name = "y9LoginUserService")
    public Y9LoginUserService y9LoginUserService(
            ConfigurableApplicationContext applicationContext,
            @Qualifier("jdbcServiceRegistryTransactionTemplate")
            TransactionOperations transactionTemplate,
            Y9UserService y9UserService
    ) {
        return new Y9LoginUserServiceImpl(applicationContext, transactionTemplate, y9UserService);
    }

    @Bean
    @ConditionalOnMissingBean(name = "y9KeyValueService")
    public Y9KeyValueService y9KeyValueService(
            @Qualifier("jdbcServiceRegistryTransactionTemplate")
            TransactionOperations transactionTemplate
    ) {
        return new Y9KeyValueServiceImpl(transactionTemplate);
    }

    @Bean
    public PrincipalResolver y9PrincipalResolver(Y9UserService y9UserService) {
        return new Y9PrincipalResolver(y9UserService);
    }

    @Bean
    public AuthenticationEventExecutionPlanConfigurer riseAuthenticationEventExecutionPlanConfigurer(
            @Qualifier(ServicesManager.BEAN_NAME) final ServicesManager servicesManager,
            Y9UserService y9UserService,
            Y9LoginUserService y9LoginUserService) {
        Y9AuthenticationHandler handler = new Y9AuthenticationHandler("y9AuthenticationHandler",
                servicesManager, 0, y9UserService, y9LoginUserService);
        return plan -> plan.registerAuthenticationHandlerWithPrincipalResolver(handler, y9PrincipalResolver(y9UserService));
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
    public Runnable y9KeyValueCleaner(Y9KeyValueService y9KeyValueService) {
        return new Y9KeyValueCleaner(y9KeyValueService);
    }

    /*@Configuration
    static class ControllerConfiguration {
        @Bean
        public LoginController loginController(
                CentralAuthenticationService centralAuthenticationService,
                @Qualifier("ticketGrantingTicketCookieGenerator") CasCookieBuilder ticketGrantingTicketCookieGenerator,
                @Qualifier("defaultAuthenticationSystemSupport") AuthenticationSystemSupport authenticationSystemSupport,
                @Qualifier("webApplicationServiceFactory") ServiceFactory webApplicationServiceFactory,
                Y9UserService y9UserService) {
            return new LoginController(centralAuthenticationService, ticketGrantingTicketCookieGenerator, authenticationSystemSupport, webApplicationServiceFactory, y9UserService);
        }

        @Bean
        public QRCodeController qrCodeController(Y9KeyValueService y9KeyValueService){
            return new QRCodeController(y9KeyValueService);
        }

        @Bean
        public RandomController randomController(){
            return new RandomController();
        }

        @Bean
        public ServiceController serviceController(ServicesManager servicesManager, CasConfigurationProperties casConfigurationProperties){
            return new ServiceController(servicesManager, casConfigurationProperties);
        }

        @Bean
        public TenantController tenantControllerenantController(Y9UserService y9UserService){
            return new TenantController(y9UserService);
        }

    }*/

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

    /*@Configuration(proxyBeanMethods = false)
    static class Y9KeyValueConfiguration {
        @Bean
        public Y9KeyValueService y9JpaKeyValueService(
                @Qualifier("jdbcServiceRegistryTransactionTemplate") TransactionOperations transactionTemplate) {
            return new Y9KeyValueServiceImpl(transactionTemplate);
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
    }*/

}
