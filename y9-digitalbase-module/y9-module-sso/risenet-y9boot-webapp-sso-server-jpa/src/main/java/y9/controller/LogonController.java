package y9.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.credential.RememberMeUsernamePasswordCredential;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.logout.LogoutManager;
import org.apereo.cas.logout.SingleLogoutExecutionRequest;
import org.apereo.cas.rest.BadRestRequestException;
import org.apereo.cas.ticket.InvalidTicketException;
import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.web.cookie.CasCookieBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dingzhaojun
 * @author mengjuhua
 * @author shidaobang
 * @author qinman
 */
@Lazy(false)
@RestController
@RequestMapping("/api")
@Slf4j
public class LogonController {

    private final CentralAuthenticationService centralAuthenticationService;
    private final CasCookieBuilder ticketGrantingTicketCookieGenerator;
    private final AuthenticationSystemSupport authenticationSystemSupport;
    private final ServiceFactory webApplicationServiceFactory;
    private final LogoutManager logoutManager;
    private final TicketRegistry ticketRegistry;

    public LogonController(CentralAuthenticationService centralAuthenticationService,
        @Qualifier("ticketGrantingTicketCookieGenerator") CasCookieBuilder ticketGrantingTicketCookieGenerator,
        @Qualifier("defaultAuthenticationSystemSupport") AuthenticationSystemSupport authenticationSystemSupport,
        @Qualifier("webApplicationServiceFactory") ServiceFactory webApplicationServiceFactory,
        @Qualifier("logoutManager") LogoutManager logoutManager,
        @Qualifier("ticketRegistry") TicketRegistry ticketRegistry) {
        this.centralAuthenticationService = centralAuthenticationService;
        this.ticketGrantingTicketCookieGenerator = ticketGrantingTicketCookieGenerator;
        this.authenticationSystemSupport = authenticationSystemSupport;
        this.webApplicationServiceFactory = webApplicationServiceFactory;
        this.logoutManager = logoutManager;
        this.ticketRegistry = ticketRegistry;
        LOGGER.info("LoginController created.");
    }

    @PostMapping(value = "/logon", consumes = MediaType.ALL_VALUE)
    public final ResponseEntity<Map<String, Object>> logon(RememberMeUsernamePasswordCredential credential,
        final HttpServletRequest request, final HttpServletResponse response) {
        Map<String, Object> retMap = new HashMap<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            if (credential == null) {
                throw new BadRestRequestException(
                    "No credentials are provided or extracted to authenticate the REST request");
            }

            String logoutTgtId = ticketGrantingTicketCookieGenerator.retrieveCookieValue(request);
            if (StringUtils.isNotBlank(logoutTgtId)) {
                TicketGrantingTicket ticket = null;
                try {
                    ticket = ticketRegistry.getTicket(logoutTgtId, TicketGrantingTicket.class);
                } catch (InvalidTicketException ignored) {
                }
                if (ticket != null) {
                    logoutManager.performLogout(SingleLogoutExecutionRequest.builder().ticketGrantingTicket(ticket)
                        .httpServletRequest(Optional.of(request)).httpServletResponse(Optional.of(response)).build());
                }
            }

            final Service service = this.webApplicationServiceFactory.createService(request);
            val authenticationResult =
                authenticationSystemSupport.finalizeAuthenticationTransaction(service, credential);
            if (authenticationResult == null) {
                throw new FailedLoginException("Authentication failed");
            }

            Ticket tgt = centralAuthenticationService.createTicketGrantingTicket(authenticationResult);
            String tgtId = tgt.getId();
            ticketGrantingTicketCookieGenerator.addCookie(request, response, tgtId);
            final Ticket serviceTicket =
                this.centralAuthenticationService.grantServiceTicket(tgtId, service, authenticationResult);

            retMap.put("success", true);
            retMap.put("msg", serviceTicket.getId());
            return new ResponseEntity<>(retMap, headers, HttpStatus.OK);
        } catch (final Exception e) {
            retMap.put("success", false);
            retMap.put("msg", e.getMessage());
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(retMap, headers, HttpStatus.UNAUTHORIZED);
        }
    }

}
