package y9.autoconfiguration.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.filters.ApiTokenFilter;
import net.risesoft.filters.CSRFFilter;
import net.risesoft.filters.XSSFilter;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.feature.security.api.Y9ApiProperties;
import net.risesoft.y9.configuration.feature.security.cors.Y9CorsProperties;
import net.risesoft.y9.json.Y9JsonUtil;

@Configuration
@ConditionalOnProperty(name = "y9.feature.security.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class Y9SecurityConfiguration {

    @Bean
    @ConditionalOnProperty(name = "y9.feature.security.cors.enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<CorsFilter> corsFilter(Y9Properties y9Properties) {
        Y9CorsProperties corsProperties = y9Properties.getFeature().getSecurity().getCors();
        LOGGER.info("CorsFilter init. Configuration:{}", Y9JsonUtil.writeValueAsString(corsProperties));
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(corsProperties.getAllowedOriginPatterns());
        config.setAllowedMethods(corsProperties.getAllowedMethods());
        config.setAllowedHeaders(corsProperties.getAllowedHeaders());
        config.setAllowCredentials(corsProperties.isAllowCredentials());
        config.setMaxAge(corsProperties.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new CorsFilter(source));
        filterBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterBean;
    }

    @Bean
    @ConditionalOnProperty(name = "y9.feature.security.xss.enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<XSSFilter> xssFilter(Y9Properties y9Properties) {
        LOGGER.info("XSSFilter init. Configuration:{}",
            Y9JsonUtil.writeValueAsString(y9Properties.getFeature().getSecurity().getXss()));

        FilterRegistrationBean<XSSFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new XSSFilter());
        filterBean.setAsyncSupported(false);
        filterBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        filterBean.addUrlPatterns("/*");
        return filterBean;
    }

    @Bean
    @ConditionalOnProperty(name = "y9.feature.security.csrf.enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<CSRFFilter> csrfFilter(Y9Properties y9Properties) {
        LOGGER.info("CSRFFilter init. Configuration:{}",
            Y9JsonUtil.writeValueAsString(y9Properties.getFeature().getSecurity().getCsrf()));

        FilterRegistrationBean<CSRFFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new CSRFFilter());
        filterBean.setAsyncSupported(false);
        filterBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        filterBean.addUrlPatterns("/*");
        return filterBean;
    }

    @Bean
    @ConditionalOnProperty(name = "y9.feature.security.api.token-required", havingValue = "true")
    public FilterRegistrationBean<ApiTokenFilter> apiTokenFilter(Y9Properties y9Properties) {
        Y9ApiProperties y9ApiProperties = y9Properties.getFeature().getSecurity().getApi();
        LOGGER.info("ApiTokenFilter init. Configuration:{}", Y9JsonUtil.writeValueAsString(y9ApiProperties));

        FilterRegistrationBean<ApiTokenFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new ApiTokenFilter(y9Properties));
        filterBean.setAsyncSupported(false);
        filterBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 3);
        filterBean.addUrlPatterns(y9ApiProperties.getUrlPatterns().toArray(new String[0]));
        return filterBean;
    }

}
