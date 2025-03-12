package y9.authen.noview;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import y9.service.Y9LoginUserService;
import y9.service.Y9UserService;

@Component
public class Y9AuthenticationEventExecutionPlanConfigurerNoView implements AuthenticationEventExecutionPlanConfigurer {

    @Autowired
    ServicesManager servicesManager;

    @Autowired
    Y9UserService y9UserService;

    @Autowired
    Y9LoginUserService y9LoginUserService;

    @Override
    public void configureAuthenticationExecutionPlan(AuthenticationEventExecutionPlan plan) throws Exception {
        Y9AuthenticationHandlerNoView handler = new Y9AuthenticationHandlerNoView("y9AuthenticationHandlerNoView", servicesManager, 1,
            y9UserService, y9LoginUserService);
        plan.registerAuthenticationHandler(handler);
    }
}
