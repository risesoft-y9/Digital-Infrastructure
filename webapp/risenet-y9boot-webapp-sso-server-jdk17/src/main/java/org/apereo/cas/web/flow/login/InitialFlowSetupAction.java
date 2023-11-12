package org.apereo.cas.web.flow.login;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationServiceSelectionPlan;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.principal.NullPrincipal;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.logout.LogoutManager;
import org.apereo.cas.logout.SingleLogoutExecutionRequest;
import org.apereo.cas.services.RegisteredServiceAccessStrategyUtils;
import org.apereo.cas.services.RegisteredServiceProperty.RegisteredServiceProperties;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.services.UnauthorizedServiceException;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.apereo.cas.util.function.FunctionUtils;
import org.apereo.cas.web.cookie.CasCookieBuilder;
import org.apereo.cas.web.flow.SingleSignOnParticipationRequest;
import org.apereo.cas.web.flow.SingleSignOnParticipationStrategy;
import org.apereo.cas.web.flow.actions.BaseCasWebflowAction;
import org.apereo.cas.web.support.ArgumentExtractor;
import org.apereo.cas.web.support.WebUtils;
import org.jooq.lambda.Unchecked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to automatically set the paths for the CookieGenerators.
 * <p>
 * Note: This is technically not thread-safe, but because its overriding with a constant value it doesn't matter.
 * <p>
 * Note: As of CAS 3.1, this is a required class that retrieves and exposes the values in the two cookies for subclasses
 * to use.
 *
 * @author Scott Battaglia
 * @since 3.1
 */
@Slf4j
@RequiredArgsConstructor
@Getter
public class InitialFlowSetupAction extends BaseCasWebflowAction {

    private final List<ArgumentExtractor> argumentExtractors;
    private final ServicesManager servicesManager;
    private final AuthenticationServiceSelectionPlan authenticationRequestServiceSelectionStrategies;
    private final CasCookieBuilder ticketGrantingTicketCookieGenerator;
    private final CasCookieBuilder warnCookieGenerator;
    private final CasConfigurationProperties casProperties;
    private final AuthenticationEventExecutionPlan authenticationEventExecutionPlan;
    private final SingleSignOnParticipationStrategy renewalStrategy;
    private final TicketRegistrySupport ticketRegistrySupport;
    @Autowired
    @Qualifier("logoutManager")
    private LogoutManager logoutManager;// y9 add

    protected static void configureWebflowForPostParameters(final RequestContext context) {
        val request = WebUtils.getHttpServletRequestFromExternalWebflowContext(context);
        if (request.getMethod().equalsIgnoreCase(HttpMethod.POST.name())) {
            WebUtils.putInitialHttpRequestPostParameters(context);
        }
    }

    protected void clearTicketGrantingCookieFromContext(final RequestContext context,
        final String ticketGrantingTicketId) {
        val request = WebUtils.getHttpServletRequestFromExternalWebflowContext(context);
        val response = WebUtils.getHttpServletResponseFromExternalWebflowContext(context);
        ticketGrantingTicketCookieGenerator.removeAll(request, response);
        WebUtils.putTicketGrantingTicketInScopes(context, StringUtils.EMPTY);
        Optional.ofNullable(ticketGrantingTicketId)
            .ifPresent(Unchecked.consumer(id -> ticketRegistrySupport.getTicketRegistry().deleteTicket(id)));
    }

    protected void configureCookieGenerators(final RequestContext context) {
        val contextPath = context.getExternalContext().getContextPath();
        val cookiePath = StringUtils.isNotBlank(contextPath) ? contextPath + '/' : "/";

        if (casProperties.getWarningCookie().isAutoConfigureCookiePath()) {
            val path = warnCookieGenerator.getCookiePath();
            if (StringUtils.isBlank(path)) {
                LOGGER.debug("Setting path for cookies for warn cookie generator to: [{}]", cookiePath);
                warnCookieGenerator.setCookiePath(cookiePath);
            } else {
                LOGGER.trace("Warning cookie is set to [{}] with path [{}]", warnCookieGenerator.getCookieDomain(),
                    path);
            }
        }

        if (casProperties.getTgc().isAutoConfigureCookiePath()) {
            val path = ticketGrantingTicketCookieGenerator.getCookiePath();
            if (StringUtils.isBlank(path)) {
                LOGGER.debug("Setting path for cookies for TGC cookie generator to: [{}]", cookiePath);
                ticketGrantingTicketCookieGenerator.setCookiePath(cookiePath);
            } else {
                LOGGER.trace("Ticket-granting cookie domain is [{}] with path [{}]",
                    ticketGrantingTicketCookieGenerator.getCookieDomain(), path);
            }
        }
    }

    protected void configureWebflowContext(final RequestContext context) {
        val request = WebUtils.getHttpServletRequestFromExternalWebflowContext(context);
        WebUtils.putWarningCookie(context, Boolean.valueOf(warnCookieGenerator.retrieveCookieValue(request)));

        WebUtils.putGeoLocationTrackingIntoFlowScope(context, casProperties.getEvents().getCore().isTrackGeolocation());
        WebUtils.putRememberMeAuthenticationEnabled(context,
            casProperties.getTicket().getTgt().getRememberMe().isEnabled());

        val staticAuthEnabled = (casProperties.getAuthn().getAccept().isEnabled()
            && StringUtils.isNotBlank(casProperties.getAuthn().getAccept().getUsers()))
            || StringUtils.isNotBlank(casProperties.getAuthn().getReject().getUsers());
        WebUtils.putStaticAuthenticationIntoFlowScope(context, staticAuthEnabled);

        if (casProperties.getAuthn().getPolicy().isSourceSelectionEnabled()) {
            val availableHandlers = authenticationEventExecutionPlan.getAuthenticationHandlers().stream()
                .filter(handler -> handler.supports(UsernamePasswordCredential.class))
                .map(handler -> StringUtils.capitalize(handler.getName().trim())).distinct().sorted()
                .collect(Collectors.toList());
            WebUtils.putAvailableAuthenticationHandleNames(context, availableHandlers);
        }
        context.getFlowScope().put("httpRequestSecure", request.isSecure());
        context.getFlowScope().put("httpRequestMethod", request.getMethod());
    }

    protected void configureWebflowForCustomFields(final RequestContext context) {
        WebUtils.putCustomLoginFormFields(context, casProperties.getView().getCustomLoginFormFields());
    }

    protected void configureWebflowForServices(final RequestContext context) {
        val response = WebUtils.getHttpServletResponseFromExternalWebflowContext(context);
        if (HttpStatus.valueOf(response.getStatus()).isError()) {
            throw new UnauthorizedServiceException(UnauthorizedServiceException.CODE_UNAUTHZ_SERVICE,
                StringUtils.EMPTY);
        }

        val service = WebUtils.getService(argumentExtractors, context);
        if (service != null) {
            LOGGER.debug("Placing service in context scope: [{}]", service.getId());
            val selectedService = FunctionUtils
                .doUnchecked(() -> authenticationRequestServiceSelectionStrategies.resolveService(service));
            val registeredService = servicesManager.findServiceBy(selectedService);
            RegisteredServiceAccessStrategyUtils.ensureServiceAccessIsAllowed(service.getId(), registeredService);
            if (registeredService != null
                && registeredService.getAccessStrategy().isServiceAccessAllowed()) {
                LOGGER.debug("Placing registered service [{}] with id [{}] in context scope",
                    registeredService.getServiceId(), registeredService.getId());
                WebUtils.putRegisteredService(context, registeredService);
                WebUtils.putWildcardedRegisteredService(context,
                    RegisteredServiceProperties.WILDCARDED_SERVICE_DEFINITION.isAssignedTo(registeredService));
                val accessStrategy = registeredService.getAccessStrategy();
                if (accessStrategy.getUnauthorizedRedirectUrl() != null) {
                    LOGGER.debug(
                        "Placing registered service's unauthorized redirect url [{}] with id [{}] in context scope",
                        accessStrategy.getUnauthorizedRedirectUrl(), registeredService.getServiceId());
                    WebUtils.putUnauthorizedRedirectUrlIntoFlowScope(context,
                        accessStrategy.getUnauthorizedRedirectUrl());
                }
            }
            WebUtils.putServiceIntoFlowScope(context, service);
        }
    }

    protected void configureWebflowForSsoParticipation(final RequestContext context,
        final String ticketGrantingTicketId) throws Throwable {
        val request = WebUtils.getHttpServletRequestFromExternalWebflowContext(context);
        val response = WebUtils.getHttpServletResponseFromExternalWebflowContext(context);

        val ssoRequest = SingleSignOnParticipationRequest.builder().requestContext(context).httpServletRequest(request)
            .httpServletResponse(response).build();
        val ssoParticipation = renewalStrategy.supports(ssoRequest) && renewalStrategy.isParticipating(ssoRequest);
        if (!ssoParticipation && StringUtils.isNotBlank(ticketGrantingTicketId)) {
            val auth = ticketRegistrySupport.getAuthenticationFrom(ticketGrantingTicketId);
            WebUtils.putExistingSingleSignOnSessionAvailable(context, auth != null);
            WebUtils.putExistingSingleSignOnSessionPrincipal(context,
                Optional.ofNullable(auth).map(Authentication::getPrincipal).orElseGet(NullPrincipal::getInstance));
            WebUtils.putTicketGrantingTicketInScopes(context, StringUtils.EMPTY);
        }
    }

    protected String configureWebflowForTicketGrantingTicket(final RequestContext context) {
        val request = WebUtils.getHttpServletRequestFromExternalWebflowContext(context);
        val ticketGrantingTicketId = ticketGrantingTicketCookieGenerator.retrieveCookieValue(request);
        val ticket = ticketRegistrySupport.getTicketGrantingTicket(ticketGrantingTicketId);
        // if (ticket != null) {
        // WebUtils.putTicketGrantingTicketInScopes(context, ticket.getId());
        // return ticket.getId();
        // }
        if (ticket != null) { // y9 add
            // 先登出才能触发重新登录
            val response = WebUtils.getHttpServletResponseFromExternalWebflowContext(context);
            String noLoginScreen = request.getParameter("noLoginScreen");
            if ("true".equalsIgnoreCase(noLoginScreen)) {
                logoutManager.performLogout(SingleLogoutExecutionRequest.builder().ticketGrantingTicket(ticket)
                    .httpServletRequest(Optional.of(request)).httpServletResponse(Optional.of(response)).build());
            } else {
                WebUtils.putTicketGrantingTicketInScopes(context, ticket.getId());
                return ticket.getId();
            }
        } // y9 add
        clearTicketGrantingCookieFromContext(context, null);
        return null;
    }

    @Override
    protected Event doExecute(RequestContext context) {
        configureCookieGenerators(context);
        configureWebflowContext(context);

        configureWebflowForPostParameters(context);
        configureWebflowForCustomFields(context);
        configureWebflowForServices(context);

        return FunctionUtils.doUnchecked(() -> {
            val ticketGrantingTicketId = configureWebflowForTicketGrantingTicket(context);
            configureWebflowForSsoParticipation(context, ticketGrantingTicketId);
            return success();
        });
    }

}
