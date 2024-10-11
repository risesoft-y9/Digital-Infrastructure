package y9.autoconfiguration.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.Ordered;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.filters.ApiTokenFilter;
import net.risesoft.filters.CorsFilter;
import net.risesoft.filters.CsrfFilter;
import net.risesoft.filters.SqlInjectionFilter;
import net.risesoft.filters.XssFilter;
import net.risesoft.y9.configuration.feature.security.Y9SecurityProperties;
import net.risesoft.y9.configuration.feature.security.api.Y9ApiProperties;
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
        filterBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterBean;
    }

    @Bean
    @ConditionalOnProperty(name = "y9.feature.security.csrf.enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<CsrfFilter> csrfFilter(Y9SecurityProperties y9SecurityProperties) {
        LOGGER.info("CSRFFilter init. Configuration:{}", Y9JsonUtil.writeValueAsString(y9SecurityProperties.getCsrf()));

        FilterRegistrationBean<CsrfFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new CsrfFilter());
        filterBean.setAsyncSupported(false);
        filterBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
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
        filterBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        filterBean.addUrlPatterns("/*");
        return filterBean;
    }

    @Bean
    @ConditionalOnProperty(name = "y9.feature.security.api.token-required", havingValue = "true")
    public FilterRegistrationBean<ApiTokenFilter> apiTokenFilter(Y9SecurityProperties y9SecurityProperties) {
        Y9ApiProperties y9ApiProperties = y9SecurityProperties.getApi();
        LOGGER.info("ApiTokenFilter init. Configuration:{}", Y9JsonUtil.writeValueAsString(y9ApiProperties));

        FilterRegistrationBean<ApiTokenFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new ApiTokenFilter(y9SecurityProperties));
        filterBean.setAsyncSupported(false);
        filterBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 3);
        filterBean.addUrlPatterns(y9ApiProperties.getUrlPatterns().toArray(new String[0]));
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
        filterBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 4);
        filterBean.addUrlPatterns("/*");
        filterBean.addInitParameter("skip", y9SecurityProperties.getSqlIn().getSkipUrl());
        return filterBean;
    }

}
