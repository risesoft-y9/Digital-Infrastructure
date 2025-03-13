package y9.authen.noview;

import javax.servlet.http.HttpServletRequest;

import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.util.LoggingUtils;
import org.apereo.cas.web.flow.actions.AbstractNonInteractiveCredentialsAction;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.execution.RequestContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Y9WebflowAction extends AbstractNonInteractiveCredentialsAction {

    public Y9WebflowAction(final CasDelegatingWebflowEventResolver initialAuthenticationAttemptWebflowEventResolver, final CasWebflowEventResolver serviceTicketRequestWebflowEventResolver,
        final AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy) {
        super(initialAuthenticationAttemptWebflowEventResolver, serviceTicketRequestWebflowEventResolver, adaptiveAuthenticationPolicy);
    }

    @Override
    protected Credential constructCredentialsFromRequest(final RequestContext requestContext) {
        try {
            final HttpServletRequest request = WebUtils.getHttpServletRequestFromExternalWebflowContext(requestContext);
            String username = request.getParameter("username");
            if (username == null) {
                return null;
            }

            String password = request.getParameter("password");
            String noLoginScreen = request.getParameter("noLoginScreen");
            String loginType = request.getParameter("loginType");
            String deptId = request.getParameter("deptId");
            String positionId = request.getParameter("positionId");
            String tenantShortName = request.getParameter("tenantShortName");
            String screenDimension = request.getParameter("screenDimension");
            String systemName = request.getParameter("systemName");
            String pwdEcodeType = request.getParameter("pwdEcodeType");

            Y9Credential c = new Y9Credential();
            c.setUsername(username);
            c.setPassword(password);
            c.setPwdEcodeType(pwdEcodeType);
            c.setNoLoginScreen(noLoginScreen);
            c.setLoginType(loginType);
            c.setDeptId(deptId);
            c.setPositionId(positionId);
            c.setTenantShortName(tenantShortName);
            c.setScreenDimension(screenDimension);
            c.setSystemName(systemName);

            /*c.setUsername(Base64Util.encode("systemManager", "Unicode"));
            c.setPassword(Base64Util.encode("Risesoft@2025", "Unicode"));
            c.setNoLoginScreen("true");
            c.setLoginType("loginName");
            c.setDeptId("11111111-1111-1111-1111-111111111115");
            c.setTenantShortName("default");*/

            LOGGER.debug("Y9Credential found in HttpServletRequest." + c);
            return c;
        } catch (final Exception e) {
            LoggingUtils.warn(LOGGER, e);
        }
        return null;
    }
}
