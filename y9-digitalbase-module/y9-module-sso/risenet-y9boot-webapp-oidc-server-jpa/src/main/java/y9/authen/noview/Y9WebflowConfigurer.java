package y9.authen.noview;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.configurer.AbstractCasWebflowConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

import lombok.val;

public class Y9WebflowConfigurer extends AbstractCasWebflowConfigurer {

	static final String STATE_ID_Y9_AUTHENTICATION_CHECK = "y9AuthenticationCheck";

	public Y9WebflowConfigurer(final FlowBuilderServices flowBuilderServices,
			final FlowDefinitionRegistry loginFlowDefinitionRegistry,
			final ConfigurableApplicationContext applicationContext, final CasConfigurationProperties casProperties) {
		super(flowBuilderServices, loginFlowDefinitionRegistry, applicationContext, casProperties);
	}

	@Override
	protected void doInitialize() {
		val flow = getLoginFlow();
		if (flow != null) {
			val actionState = createActionState(flow, "y9AuthenticationCheck","y9WebflowAction");
			val transitionSet = actionState.getTransitionSet();
			transitionSet.add(createTransition(CasWebflowConstants.TRANSITION_ID_SUCCESS,
					CasWebflowConstants.STATE_ID_CREATE_TICKET_GRANTING_TICKET));
			transitionSet.add(
					createTransition(CasWebflowConstants.TRANSITION_ID_WARN, CasWebflowConstants.TRANSITION_ID_WARN));
			transitionSet.add(createTransition(CasWebflowConstants.TRANSITION_ID_ERROR, getStartState(flow).getId()));
			transitionSet.add(createTransition(CasWebflowConstants.TRANSITION_ID_SUCCESS_WITH_WARNINGS,
					CasWebflowConstants.STATE_ID_SHOW_AUTHN_WARNING_MSGS));
			actionState.getExitActionList()
					.add(createEvaluateAction(CasWebflowConstants.ACTION_ID_CLEAR_WEBFLOW_CREDENTIALS));
			setStartState(flow, actionState);
		}
	}
}
