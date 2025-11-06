package net.risesoft.demo.sso;

import java.util.Collections;

import jakarta.servlet.ServletContext;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.Environment;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.WebApplicationContext;

import y9.oauth2.client.CallbackController;

public class ServletInitializer extends SpringBootServletInitializer implements WebApplicationInitializer {

    @Override
    protected WebApplicationContext run(SpringApplication application) {
        WebApplicationContext ctx = super.run(application);
        Environment env = ctx.getEnvironment();
        String sessionTimeout = env.getProperty("server.servlet.session.timeout", "300");
        String cookieSecure = env.getProperty("server.servlet.session.cookie.secure", "false");

        ServletContext servletContext = ctx.getServletContext();
        servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));
        servletContext.setSessionTimeout(Integer.valueOf(sessionTimeout));
        SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
        sessionCookieConfig.setHttpOnly(true);
        sessionCookieConfig.setSecure(Boolean.valueOf(cookieSecure));
        return ctx;
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
        setRegisterErrorPageFilter(false);
        builder.sources(DemoSsoApplication.class, CallbackController.class);
        return builder;
    }
}