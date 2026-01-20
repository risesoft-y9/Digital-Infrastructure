package y9.controller;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.FailedLoginException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.credential.RememberMeUsernamePasswordCredential;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.rest.BadRestRequestException;
import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.web.cookie.CasCookieBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

import y9.service.Y9UserService;

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

    private final Y9UserService y9UserService;

    public LogonController(
        CentralAuthenticationService centralAuthenticationService,
        @Qualifier("ticketGrantingTicketCookieGenerator") CasCookieBuilder ticketGrantingTicketCookieGenerator,
        @Qualifier("defaultAuthenticationSystemSupport") AuthenticationSystemSupport authenticationSystemSupport,
        @Qualifier("webApplicationServiceFactory") ServiceFactory webApplicationServiceFactory,
        Y9UserService y9UserService) {
        this.centralAuthenticationService = centralAuthenticationService;
        this.ticketGrantingTicketCookieGenerator = ticketGrantingTicketCookieGenerator;
        this.authenticationSystemSupport = authenticationSystemSupport;
        this.webApplicationServiceFactory = webApplicationServiceFactory;
        this.y9UserService = y9UserService;
        LOGGER.info("LoginController created.");
    }

    @PostMapping(value = "/logon", consumes = MediaType.ALL_VALUE)
    public final ResponseEntity<Map<String, Object>> logon(RememberMeUsernamePasswordCredential credential,
        @RequestBody(required = false) final MultiValueMap<String, String> requestBody,
        final HttpServletRequest request, final HttpServletResponse response) throws Throwable {
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("success", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            if (credential == null) {
                throw new BadRestRequestException(
                    "No credentials are provided or extracted to authenticate the REST request");
            }
            String username = credential.getUsername();
            String password = credential.toPassword();
            Map<String, Object> customFields = credential.getCustomFields();
            String tenantShortName = (String)customFields.get("tenantShortName");
            String loginType = (String)customFields.get("loginType");
            String rsaPublicKey = request.getParameter("rsaPublicKey");

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
