package org.apereo.cas.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.RiseAuthenticationHandler;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.cas.authentication.principal.RisePersonDirectoryPrincipalResolver;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.jpa.JpaPersistenceProviderConfigurer;
import org.apereo.cas.redis.core.CasRedisTemplate;
import org.apereo.cas.redis.core.RedisObjectFactory;
import org.apereo.cas.services.JpaRegisteredServiceEntity;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.services.Y9LoginUser;
import org.apereo.cas.services.Y9User;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.app.ApplicationUtils;
import org.apereo.cas.util.spring.beans.BeanCondition;
import org.apereo.cas.util.spring.beans.BeanSupplier;
import org.apereo.cas.util.spring.boot.CasBanner;
import org.apereo.cas.web.cookie.CasCookieBuilder;
import org.apereo.cas.web.flow.actions.RiseCredentialNonInteractiveCredentialsAction;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.webflow.execution.Action;

import lombok.NoArgsConstructor;
import lombok.val;
import y9.controller.CheckController;
import y9.controller.LoginController;
import y9.controller.QRCodeController;
import y9.controller.RedisController;
import y9.controller.ServiceController;
import y9.controller.TenantController;
import y9.service.Y9LoginUserService;
import y9.service.Y9UserService;
import y9.service.impl.Y9LoginUserServiceImpl;
import y9.service.impl.Y9UserServiceImpl;
import y9.util.Y9Context;

/**
 * This is {@link CasWebApplication} that houses the main method.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@EnableDiscoveryClient
@SpringBootApplication(proxyBeanMethods = false, exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    MongoAutoConfiguration.class,
    MongoDataAutoConfiguration.class
})
@EnableConfigurationProperties(CasConfigurationProperties.class)
@EnableAspectJAutoProxy(proxyTargetClass = false)
@EnableTransactionManagement(proxyTargetClass = false)
@EnableScheduling
@NoArgsConstructor
public class CasWebApplication {

    /**
     * Main entry point of the CAS web application.
     *
     * @param args the args
     */
    public static void main(final String[] args) {
        val applicationClasses = getApplicationSources(args);
        new SpringApplicationBuilder()
            .sources(applicationClasses.toArray(ArrayUtils.EMPTY_CLASS_ARRAY))
            .banner(CasBanner.getInstance())
            .web(WebApplicationType.SERVLET)
            .logStartupInfo(true)
            .applicationStartup(ApplicationUtils.getApplicationStartup())
            .run(args);
    }

    protected static List<Class> getApplicationSources(final String[] args) {
        val applicationClasses = new ArrayList<Class>();
        applicationClasses.add(CasWebApplication.class);
        ApplicationUtils.getApplicationEntrypointInitializers()
            .forEach(init -> {
                init.initialize(args);
                applicationClasses.addAll(init.getApplicationSources(args));
            });
        return applicationClasses;
    }
    
    // y9-start
    @Configuration
    public static class Y9RedisConfig {
    	@Bean
        public Y9Context y9Context() {
            return new Y9Context();
        }

        @Bean
        @RefreshScope
        public CasRedisTemplate<Object, Object> y9RedisTemplate(
            @Qualifier("redisTicketConnectionFactory") final RedisConnectionFactory redisTicketConnectionFactory) {
            CasRedisTemplate<Object, Object> redisTemplate = RedisObjectFactory.newRedisTemplate(redisTicketConnectionFactory);
            return redisTemplate;
        }
    }

    @Configuration
    public static class Y9JpaConfig {
    	private static final BeanCondition CONDITION = BeanCondition.on("cas.service-registry.jpa.enabled").isTrue().evenIfMissing();

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
            RiseAuthenticationHandler handler = new RiseAuthenticationHandler("y9AuthenticationHandler", servicesManager, risePrincipalFactory(), 0);
            return plan -> plan.registerAuthenticationHandlerWithPrincipalResolver(handler, risePersonDirectoryPrincipalResolver());
        }

        @Bean
        public Action riseCredentialNonInteractiveCredentialsAction(
            @Qualifier("adaptiveAuthenticationPolicy") final AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy,
            @Qualifier("serviceTicketRequestWebflowEventResolver") final CasWebflowEventResolver serviceTicketRequestWebflowEventResolver,
            @Qualifier("initialAuthenticationAttemptWebflowEventResolver") final CasDelegatingWebflowEventResolver initialAuthenticationAttemptWebflowEventResolver) {
            return new RiseCredentialNonInteractiveCredentialsAction(
                initialAuthenticationAttemptWebflowEventResolver, 
                serviceTicketRequestWebflowEventResolver, 
                adaptiveAuthenticationPolicy);
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

    @Configuration
    public static class Y9ControllerConfig {
    	@Bean
        public CheckController checkController(Y9UserService y9UserService,
                @Qualifier("y9RedisTemplate") CasRedisTemplate<Object, Object> redisTemplate) {
            return new CheckController(y9UserService, redisTemplate);
        }
    	
    	@Bean
        public LoginController loginController(CentralAuthenticationService centralAuthenticationService,
                @Qualifier("ticketGrantingTicketCookieGenerator") CasCookieBuilder ticketGrantingTicketCookieGenerator,
                @Qualifier("defaultAuthenticationSystemSupport") AuthenticationSystemSupport authenticationSystemSupport,
                @Qualifier("webApplicationServiceFactory") ServiceFactory webApplicationServiceFactory,
                Y9UserService y9UserService) {
            return new LoginController(centralAuthenticationService, ticketGrantingTicketCookieGenerator,
            		authenticationSystemSupport,webApplicationServiceFactory,y9UserService);
        }
    	
    	@Bean
        public QRCodeController qRCodeController(@Qualifier("y9RedisTemplate") CasRedisTemplate<Object, Object> redisTemplate) {
            return new QRCodeController(redisTemplate);
        }
    	
    	@Bean
        public RedisController redisController(@Qualifier("y9RedisTemplate") CasRedisTemplate<Object, Object> redisTemplate) {
            return new RedisController(redisTemplate);
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
    }

    @Configuration
    public static class Y9ServiceConfig {
    	@Bean
        public Y9UserService y9UserService() {
            return new Y9UserServiceImpl();
        }
        
        @Bean
        public Y9LoginUserService y9LoginUserService() {
            return new Y9LoginUserServiceImpl();
        }

    }    
    // y9-end
}