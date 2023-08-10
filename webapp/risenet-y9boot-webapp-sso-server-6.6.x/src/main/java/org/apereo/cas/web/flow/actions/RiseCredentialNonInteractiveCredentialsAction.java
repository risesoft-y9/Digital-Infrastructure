package org.apereo.cas.web.flow.actions;

import jakarta.servlet.http.HttpServletRequest;

import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.authentication.credential.RememberMeUsernamePasswordCredential;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.execution.RequestContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class RiseCredentialNonInteractiveCredentialsAction extends AbstractNonInteractiveCredentialsAction {

    public RiseCredentialNonInteractiveCredentialsAction(
        final CasDelegatingWebflowEventResolver initialAuthenticationAttemptWebflowEventResolver,
        final CasWebflowEventResolver serviceTicketRequestWebflowEventResolver,
        final AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy) {
        super(initialAuthenticationAttemptWebflowEventResolver, serviceTicketRequestWebflowEventResolver,
            adaptiveAuthenticationPolicy);
    }

    @Override
    protected Credential constructCredentialsFromRequest(final RequestContext requestContext) {
        final HttpServletRequest request = WebUtils.getHttpServletRequestFromExternalWebflowContext(requestContext);
        String noLoginScreen = request.getParameter("noLoginScreen");
        String loginType = request.getParameter("loginType");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String deptId = request.getParameter("deptId");
        String positionId = request.getParameter("positionId");
        String tenantShortName = request.getParameter("tenantShortName");
        String screenDimension = request.getParameter("screenDimension");
        String systemName = request.getParameter("systemName");
        String userAgent = request.getParameter("userAgent");
        String clientIp = request.getParameter("clientIp");
        String clientMac = request.getParameter("clientMac");
        String clientDiskId = request.getParameter("clientDiskId");
        String clientHostName = request.getParameter("clientHostName");

        try {
            RememberMeUsernamePasswordCredential riseCredential = new RememberMeUsernamePasswordCredential();
            riseCredential.setTenantShortName(tenantShortName);
            riseCredential.setUsername(username);
            riseCredential.assignPassword(password);
            riseCredential.setDeptId(deptId);
            riseCredential.setNoLoginScreen("true");
            riseCredential.setLoginType(loginType);
            riseCredential.setScreenDimension(screenDimension);
            riseCredential.setPositionId(positionId);
            riseCredential.setSystemName(systemName);
            riseCredential.setUserAgent(userAgent);
            riseCredential.setClientIp(clientIp);
            riseCredential.setClientMac(clientMac);
            riseCredential.setClientDiskId(clientDiskId);
            riseCredential.setClientHostName(clientHostName);
            LOGGER.debug("RiseCredential found in HttpServletRequest." + riseCredential);
            return riseCredential;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        LOGGER.debug("RiseCredential not found in HttpServletRequest.");
        return null;
    }
}
