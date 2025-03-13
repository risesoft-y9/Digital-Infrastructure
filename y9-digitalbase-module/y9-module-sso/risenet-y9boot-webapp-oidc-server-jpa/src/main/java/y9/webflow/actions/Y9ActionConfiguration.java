package y9.webflow.actions;

import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.webflow.execution.Action;

//@Configuration
public class Y9ActionConfiguration {

    @Bean
    public Action y9CredentialNonInteractiveCredentialsAction(
        @Qualifier("adaptiveAuthenticationPolicy") final AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy,
        @Qualifier("serviceTicketRequestWebflowEventResolver") final CasWebflowEventResolver serviceTicketRequestWebflowEventResolver,
        @Qualifier("initialAuthenticationAttemptWebflowEventResolver") final CasDelegatingWebflowEventResolver initialAuthenticationAttemptWebflowEventResolver) {
        return new Y9CredentialNonInteractiveCredentialsAction(initialAuthenticationAttemptWebflowEventResolver,
            serviceTicketRequestWebflowEventResolver, adaptiveAuthenticationPolicy);
    }

}
