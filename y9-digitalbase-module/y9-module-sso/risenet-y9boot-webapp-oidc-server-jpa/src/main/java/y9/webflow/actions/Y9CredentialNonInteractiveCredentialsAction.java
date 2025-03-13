package y9.webflow.actions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.authentication.credential.RememberMeUsernamePasswordCredential;
import org.apereo.cas.web.flow.actions.AbstractNonInteractiveCredentialsAction;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.execution.RequestContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Y9CredentialNonInteractiveCredentialsAction extends AbstractNonInteractiveCredentialsAction {

    public Y9CredentialNonInteractiveCredentialsAction(
        final CasDelegatingWebflowEventResolver initialAuthenticationAttemptWebflowEventResolver,
        final CasWebflowEventResolver serviceTicketRequestWebflowEventResolver,
        final AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy) {
        super(initialAuthenticationAttemptWebflowEventResolver, serviceTicketRequestWebflowEventResolver,
            adaptiveAuthenticationPolicy);
    }

    @Override
    protected Credential constructCredentialsFromRequest(final RequestContext requestContext) {
        final HttpServletRequest request = WebUtils.getHttpServletRequestFromExternalWebflowContext(requestContext);
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String noLoginScreen = request.getParameter("noLoginScreen");
        String loginType = request.getParameter("loginType");
        String deptId = request.getParameter("deptId");
        String positionId = request.getParameter("positionId");
        String tenantShortName = request.getParameter("tenantShortName");
        String screenDimension = request.getParameter("screenDimension");
        String systemName = request.getParameter("systemName");

        RememberMeUsernamePasswordCredential riseCredential = new RememberMeUsernamePasswordCredential();
        riseCredential.setUsername(username);
        riseCredential.assignPassword(password);
        Map<String, Object> customFields = riseCredential.getCustomFields();
        customFields.put("noLoginScreen", noLoginScreen);
        customFields.put("loginType", loginType);
        customFields.put("deptId", deptId);
        customFields.put("positionId", positionId);
        customFields.put("tenantShortName", tenantShortName);
        customFields.put("screenDimension", screenDimension);
        customFields.put("systemName", systemName);

        LOGGER.debug("RiseCredential found in HttpServletRequest." + riseCredential);
        return riseCredential;
    }
}
