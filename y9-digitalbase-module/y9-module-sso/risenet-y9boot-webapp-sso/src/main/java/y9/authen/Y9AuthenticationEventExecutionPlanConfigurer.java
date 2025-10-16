package y9.authen;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.services.ServicesManager;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import y9.Y9Properties;
import y9.service.Y9LoginUserService;
import y9.service.Y9UserService;

@Component
@RequiredArgsConstructor
public class Y9AuthenticationEventExecutionPlanConfigurer implements AuthenticationEventExecutionPlanConfigurer {

    private final ServicesManager servicesManager;

    private final Y9UserService y9UserService;

    private final Y9LoginUserService y9LoginUserService;

    private final Y9Properties y9Properties;

    @Override
    public void configureAuthenticationExecutionPlan(AuthenticationEventExecutionPlan plan) throws Exception {
        Y9PrincipalResolver y9PrincipalResolver = new Y9PrincipalResolver(y9UserService);
        Y9AuthenticationHandler handler =
            new Y9AuthenticationHandler("y9AuthenticationHandler", 0, y9UserService, y9LoginUserService, y9Properties);
        // plan.registerAuthenticationHandlerWithPrincipalResolver(handler, y9PrincipalResolver);
        plan.registerAuthenticationHandler(handler);
    }
}
