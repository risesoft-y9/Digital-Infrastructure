package org.apereo.cas.web.y9.config;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.RiseAuthenticationHandler;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.cas.authentication.principal.RisePersonDirectoryPrincipalResolver;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.flow.actions.RiseCredentialNonInteractiveCredentialsAction;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.webflow.execution.Action;

@Configuration(proxyBeanMethods = false)
public class Y9AuthenticationConfiguration {

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
