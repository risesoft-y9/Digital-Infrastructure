package net.risesoft.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.app.y9platform.Y9PlatformProperties;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Configuration
@EnableConfigurationProperties({Y9Properties.class, Y9PlatformProperties.class})
public class WebMvcConfig {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @DependsOn("y9Context")
    @Bean
    @ConditionalOnProperty(value = "y9.feature.oauth2.resource.enabled", havingValue = "true")
    public FilterRegistrationBean checkUserLoginFilter() {
        FilterRegistrationBean filterBean = new FilterRegistrationBean();
        filterBean.setFilter(new CheckUserLoginFilter4Platform());
        filterBean.setAsyncSupported(false);
        filterBean.setOrder(50);
        filterBean.addUrlPatterns("/api/*");
        return filterBean;
    }

    @Bean
    @ConditionalOnMissingBean
    public Y9Context y9Context() {
        return new Y9Context();
    }
}
