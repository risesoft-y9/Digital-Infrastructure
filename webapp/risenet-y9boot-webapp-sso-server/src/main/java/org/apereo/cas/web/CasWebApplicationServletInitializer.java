package org.apereo.cas.web;

import org.apereo.cas.web.y9.config.Y9AuthenticationConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.WebApplicationInitializer;

public class CasWebApplicationServletInitializer extends SpringBootServletInitializer
    implements WebApplicationInitializer {

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
        setRegisterErrorPageFilter(false);
        builder.sources(CasWebApplication.class, Y9AuthenticationConfiguration.class);
        return builder;
    }

}
