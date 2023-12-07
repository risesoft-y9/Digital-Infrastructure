package org.apereo.cas.web;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;

import java.util.Collections;

import org.apereo.cas.web.y9.util.Y9Context;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.Environment;
import org.springframework.web.WebApplicationInitializer;

 public class CasWebApplicationServletInitializer extends SpringBootServletInitializer implements WebApplicationInitializer {
     
     @Override
     protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
         setRegisterErrorPageFilter(false);
         builder.sources(CasWebApplication.class);
         return builder;
     }

     /*@Override
     public void onStartup(ServletContext servletContext) throws ServletException {
         Environment env = Y9Context.getEnvironment();
         String sessionTimeout = env.getProperty("server.servlet.session.timeout", "300");
         String cookieSecure = env.getProperty("server.servlet.session.cookie.secure", "false");
         servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));
         servletContext.setSessionTimeout(Integer.valueOf(sessionTimeout));
         SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
         sessionCookieConfig.setHttpOnly(true);
         sessionCookieConfig.setSecure(Boolean.valueOf(cookieSecure));
         super.onStartup(servletContext);
     }*/

}
