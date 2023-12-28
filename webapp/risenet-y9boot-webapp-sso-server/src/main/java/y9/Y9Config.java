package y9;

import java.util.List;

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
import org.apereo.cas.util.spring.beans.BeanCondition;
import org.apereo.cas.util.spring.beans.BeanSupplier;
import org.apereo.cas.web.CasWebSecurityConfigurer;
import org.apereo.cas.web.cookie.CasCookieBuilder;
import org.apereo.cas.web.flow.actions.RiseCredentialNonInteractiveCredentialsAction;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.webflow.execution.Action;

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

@Lazy(false)
@Configuration(proxyBeanMethods = false)
public class Y9Config {

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
		@ConditionalOnMissingBean
		public LoginController loginController(CentralAuthenticationService centralAuthenticationService,
				@Qualifier("ticketGrantingTicketCookieGenerator") CasCookieBuilder ticketGrantingTicketCookieGenerator,
				@Qualifier("defaultAuthenticationSystemSupport") AuthenticationSystemSupport authenticationSystemSupport,
				@Qualifier("webApplicationServiceFactory") ServiceFactory webApplicationServiceFactory,
				Y9UserService y9UserService) {
			return new LoginController(centralAuthenticationService, ticketGrantingTicketCookieGenerator,
					authenticationSystemSupport, webApplicationServiceFactory, y9UserService);
		}
    	
    	@Bean
    	@ConditionalOnMissingBean
        public QRCodeController qRCodeController(@Qualifier("y9RedisTemplate") CasRedisTemplate<Object, Object> redisTemplate) {
            return new QRCodeController(redisTemplate);
        }
    	
    	@Bean
    	@ConditionalOnMissingBean
        public RedisController redisController(@Qualifier("y9RedisTemplate") CasRedisTemplate<Object, Object> redisTemplate) {
            return new RedisController(redisTemplate);
        }
    	
    	@Bean
    	@ConditionalOnMissingBean
        public ServiceController serviceController(ServicesManager servicesManager,
        		CasConfigurationProperties casConfigurationProperties) {
            return new ServiceController(servicesManager, casConfigurationProperties);
        }
    	
    	@Bean
    	@ConditionalOnMissingBean
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
    
}
