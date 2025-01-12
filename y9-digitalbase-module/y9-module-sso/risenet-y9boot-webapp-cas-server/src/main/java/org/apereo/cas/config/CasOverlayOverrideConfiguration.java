package org.apereo.cas.config;

//import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScopedProxyMode;
import y9.authen.Y9AuthenticationHandler;
import y9.service.Y9LoginUserService;
import y9.service.Y9UserService;

//import org.apereo.cas.configuration.CasConfigurationProperties;

@AutoConfiguration
//@ComponentScan("y9")
//@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CasOverlayOverrideConfiguration {

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

}
