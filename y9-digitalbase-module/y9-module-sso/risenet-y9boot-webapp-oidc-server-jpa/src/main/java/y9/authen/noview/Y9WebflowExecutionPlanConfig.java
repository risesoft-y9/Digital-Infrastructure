package y9.authen.noview;

import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.CasWebflowExecutionPlanConfigurer;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.Action;

@EnableConfigurationProperties(CasConfigurationProperties.class)
@AutoConfiguration
public class Y9WebflowExecutionPlanConfig {

	@Bean
	public Action y9WebflowAction(
			@Qualifier("adaptiveAuthenticationPolicy") final AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy,
			@Qualifier("serviceTicketRequestWebflowEventResolver") final CasWebflowEventResolver serviceTicketRequestWebflowEventResolver,
			@Qualifier("initialAuthenticationAttemptWebflowEventResolver") final CasDelegatingWebflowEventResolver initialAuthenticationAttemptWebflowEventResolver) {
		return new Y9WebflowAction(initialAuthenticationAttemptWebflowEventResolver,
				serviceTicketRequestWebflowEventResolver, adaptiveAuthenticationPolicy);
	}

	@Bean
	public CasWebflowConfigurer y9WebflowConfigurer(final CasConfigurationProperties casProperties,
			final ConfigurableApplicationContext applicationContext,
			@Qualifier(CasWebflowConstants.BEAN_NAME_LOGIN_FLOW_DEFINITION_REGISTRY) final FlowDefinitionRegistry loginFlowDefinitionRegistry,
			@Qualifier(CasWebflowConstants.BEAN_NAME_FLOW_BUILDER_SERVICES) final FlowBuilderServices flowBuilderServices) {
		return new Y9WebflowConfigurer(flowBuilderServices, loginFlowDefinitionRegistry, applicationContext,
				casProperties);
	}

	@Bean
	public CasWebflowExecutionPlanConfigurer y9WebflowExecutionPlanConfigurer(
			@Qualifier("y9WebflowConfigurer") final CasWebflowConfigurer y9WebflowConfigurer) {
		return plan -> plan.registerWebflowConfigurer(y9WebflowConfigurer);
	}

}
