package org.apereo.cas.web.y9.config;

import javax.sql.DataSource;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.support.JpaBeans;
import org.apereo.cas.web.y9.util.Y9Context;
import org.apereo.cas.web.y9.y9user.Y9UserDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration(proxyBeanMethods = false)
public class Y9UserDaoConfig {

    @Bean
    @ConditionalOnMissingBean
    public Y9Context y9Context() {
        return new Y9Context();
    }

    @Primary
    @Bean
    public PlatformTransactionManager
        y9SessionTransactionManager(@Qualifier("y9UserDataSource") DataSource y9UserDataSource) {
        return new DataSourceTransactionManager(y9UserDataSource);
    }

    @Bean
    @RefreshScope
    public Y9UserDao y9UserDao(@Qualifier("y9UserJdbcTemplate") JdbcTemplate y9UserJdbcTemplate) {
        return new Y9UserDao(y9UserJdbcTemplate);
    }

    @Primary
    @Bean
    @RefreshScope
    public DataSource y9UserDataSource(CasConfigurationProperties casProperties) {
        return JpaBeans.newDataSource(casProperties.getServiceRegistry().getJpa());
    }

    @Primary
    @Bean
    @RefreshScope
    public JdbcTemplate y9UserJdbcTemplate(@Qualifier("y9UserDataSource") DataSource y9UserDataSource) {
        return new JdbcTemplate(y9UserDataSource);
    }

}
