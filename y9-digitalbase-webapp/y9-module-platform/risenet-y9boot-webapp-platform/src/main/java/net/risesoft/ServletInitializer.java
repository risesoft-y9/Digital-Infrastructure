package net.risesoft;

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

/**
 * https://docs.spring.io/spring-boot/docs/2.1.0.RELEASE/reference/htmlsingle/ 92.3 Deploying a WAR to WebLogic To
 * deploy a Spring Boot application to WebLogic, you must ensure that your servlet initializer directly implements
 * WebApplicationInitializer (even if you extend from a base class that already implements it).
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public class ServletInitializer extends SpringBootServletInitializer implements WebApplicationInitializer {

    @Override
    protected WebApplicationContext run(SpringApplication application) {
        WebApplicationContext ctx = super.run(application);
        Environment env = ctx.getEnvironment();
        String cookieSecure = env.getProperty("server.servlet.session.cookie.secure", "false");

        ServletContext servletContext = ctx.getServletContext();
        servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));
        SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
        sessionCookieConfig.setHttpOnly(true);
        sessionCookieConfig.setSecure(Boolean.valueOf(cookieSecure));
        return ctx;
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
        setRegisterErrorPageFilter(false);
        builder.sources(Platform.class);
        return builder;
    }
}