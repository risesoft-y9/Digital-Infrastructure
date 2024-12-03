package y9.autoconfiguration.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScopedProxyMode;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.FilterOrderConsts;
import net.risesoft.filters.CorsFilter;
import net.risesoft.filters.CsrfFilter;
import net.risesoft.filters.SqlInjectionFilter;
import net.risesoft.filters.XssFilter;
import net.risesoft.y9.configuration.feature.security.Y9SecurityProperties;
import net.risesoft.y9.json.Y9JsonUtil;

@Configuration
@ConditionalOnProperty(name = "y9.feature.security.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class Y9SecurityConfiguration {

    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public Y9SecurityProperties y9SecurityProperties() {
        return new Y9SecurityProperties();
    }

    @Bean
    @ConditionalOnProperty(name = "y9.feature.security.cors.enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<CorsFilter> corsFilter(Y9SecurityProperties y9SecurityProperties) {
        LOGGER.info("CorsFilter init. Configuration:{}", Y9JsonUtil.writeValueAsString(y9SecurityProperties.getCors()));
        FilterRegistrationBean<CorsFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new CorsFilter());
        filterBean.setOrder(FilterOrderConsts.CORS_ORDER);
        return filterBean;
    }

    @Bean
    @ConditionalOnProperty(name = "y9.feature.security.csrf.enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<CsrfFilter> csrfFilter(Y9SecurityProperties y9SecurityProperties) {
        LOGGER.info("CSRFFilter init. Configuration:{}", Y9JsonUtil.writeValueAsString(y9SecurityProperties.getCsrf()));

        FilterRegistrationBean<CsrfFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new CsrfFilter());
        filterBean.setAsyncSupported(false);
        filterBean.setOrder(FilterOrderConsts.CSRF_ORDER);
        filterBean.addUrlPatterns("/*");
        return filterBean;
    }

    @Bean
    @ConditionalOnProperty(name = "y9.feature.security.xss.enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<XssFilter> xssFilter(Y9SecurityProperties y9SecurityProperties) {
        LOGGER.info("XSSFilter init. Configuration:{}", Y9JsonUtil.writeValueAsString(y9SecurityProperties.getXss()));

        FilterRegistrationBean<XssFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new XssFilter());
        filterBean.setAsyncSupported(false);
        filterBean.setOrder(FilterOrderConsts.XSS_ORDER);
        filterBean.addUrlPatterns("/*");
        return filterBean;
    }

    @Bean
    @ConditionalOnProperty(name = "y9.feature.security.sqlIn.enabled", havingValue = "true")
    public FilterRegistrationBean<SqlInjectionFilter> sqlInjectionFilter(Y9SecurityProperties y9SecurityProperties) {
        LOGGER.info("SQLInFilter init. Configuration:{}",
            Y9JsonUtil.writeValueAsString(y9SecurityProperties.getSqlIn()));

        FilterRegistrationBean<SqlInjectionFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new SqlInjectionFilter());
        filterBean.setAsyncSupported(false);
        filterBean.setOrder(FilterOrderConsts.SQL_INJECTION_ORDER);
        filterBean.addUrlPatterns("/*");
        filterBean.addInitParameter("skip", y9SecurityProperties.getSqlIn().getSkipUrl());
        return filterBean;
    }

}
