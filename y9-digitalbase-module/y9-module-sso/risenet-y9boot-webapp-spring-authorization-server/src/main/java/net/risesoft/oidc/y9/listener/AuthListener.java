package net.risesoft.oidc.y9.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthListener implements ApplicationListener<AbstractAuthenticationEvent> {
    private static final Log LOGGER = LogFactory.getLog(AuthListener.class);

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        LOGGER.warn(event.getAuthentication().getPrincipal());
    }

}
