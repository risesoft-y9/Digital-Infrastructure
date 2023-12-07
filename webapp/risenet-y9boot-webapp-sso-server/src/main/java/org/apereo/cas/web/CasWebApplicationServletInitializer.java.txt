package org.apereo.cas.web;

import org.apereo.cas.util.app.ApplicationUtils;
import org.apereo.cas.util.spring.boot.AbstractCasSpringBootServletInitializer;
import org.apereo.cas.util.spring.boot.CasBanner;
import org.apereo.cas.web.y9.config.Y9AuthenticationConfiguration;
import org.apereo.cas.web.y9.config.Y9CasWebSecurityConfigurer;
import org.apereo.cas.web.y9.config.Y9JpaConfig;
import org.apereo.cas.web.y9.config.Y9RedisConfig;

import java.util.List;

/**
 * This is {@link CasWebApplicationServletInitializer}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class CasWebApplicationServletInitializer extends AbstractCasSpringBootServletInitializer {

    public CasWebApplicationServletInitializer() {
        super(List.of(CasWebApplication.class,Y9AuthenticationConfiguration.class,Y9CasWebSecurityConfigurer.class,Y9JpaConfig.class,Y9RedisConfig.class),
            CasBanner.getInstance(),
            ApplicationUtils.getApplicationStartup());
    }
}

