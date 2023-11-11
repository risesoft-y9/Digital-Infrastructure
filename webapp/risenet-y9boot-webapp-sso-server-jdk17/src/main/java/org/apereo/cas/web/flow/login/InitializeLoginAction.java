package org.apereo.cas.web.flow.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.logout.LogoutManager;
import org.apereo.cas.logout.SingleLogoutExecutionRequest;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.services.UnauthorizedServiceException;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.web.cookie.CasCookieBuilder;
import org.apereo.cas.web.flow.actions.BaseCasWebflowAction;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.repository.NoSuchFlowExecutionException;

import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * This is {@link InitializeLoginAction}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class InitializeLoginAction extends BaseCasWebflowAction {
    /**
     * The services manager with access to the registry.
     **/
    protected final ServicesManager servicesManager;

    /**
     * CAS Properties.
     */
    protected final CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("logoutManager")
    private LogoutManager logoutManager; // y9 add

    @Autowired
    @Qualifier("ticketGrantingTicketCookieGenerator")
    private CasCookieBuilder ticketGrantingTicketCookieGenerator; // y9 add

    @Autowired
    @Qualifier("ticketRegistry")
    private TicketRegistry ticketRegistry; // y9 add

    @Override
    protected Event doExecuteInternal(final RequestContext requestContext) throws Exception {
        LOGGER.trace("Initialized login sequence");
        val service = WebUtils.getService(requestContext);

        if (service == null && !casProperties.getSso().getServices().isAllowMissingServiceParameter()) {
            val request = WebUtils.getHttpServletRequestFromExternalWebflowContext(requestContext);
            LOGGER.warn(
                "No service authentication request is available at [{}]. CAS is configured to disable the flow.",
                request.getRequestURL());
            throw new NoSuchFlowExecutionException(requestContext.getFlowExecutionContext().getKey(),
                UnauthorizedServiceException.required());
        }

        // y9 add
        HttpServletRequest request = WebUtils.getHttpServletRequestFromExternalWebflowContext(requestContext);
        HttpServletResponse response = WebUtils.getHttpServletResponseFromExternalWebflowContext(requestContext);
        try {
            String tgtId = WebUtils.getTicketGrantingTicketId(requestContext);
            if (StringUtils.isBlank(tgtId)) {
                tgtId = ticketGrantingTicketCookieGenerator.retrieveCookieValue(request);
            }
            if (StringUtils.isNotBlank(tgtId)) {
                TicketGrantingTicket ticket = ticketRegistry.getTicket(tgtId, TicketGrantingTicket.class);
                logoutManager.performLogout(SingleLogoutExecutionRequest.builder().ticketGrantingTicket(ticket)
                    .httpServletRequest(Optional.of(request)).httpServletResponse(Optional.of(response)).build());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        String noLoginScreen = request.getParameter("noLoginScreen");
        if ("true".equalsIgnoreCase(noLoginScreen)) {
            return nonInteractiveLogin();
        }
        // y9 add

        return success();
    }

    // y9 add
    protected Event nonInteractiveLogin() {
        return getEventFactorySupport().event(this, "nonInteractiveLogin");
    }

}
