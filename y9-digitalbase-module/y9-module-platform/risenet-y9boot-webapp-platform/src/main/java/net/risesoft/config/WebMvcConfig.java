package net.risesoft.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.EnableAsync;

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
@EnableAsync(proxyTargetClass = true, mode = AdviceMode.ASPECTJ)
public class WebMvcConfig {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @DependsOn("y9Context")
    @Bean
    public FilterRegistrationBean checkUserLoginFilter(Y9Properties y9config) {
        FilterRegistrationBean filterBean = new FilterRegistrationBean();
        boolean enabled = y9config.getFeature().getOauth2().getResource().isEnabled();
        if (enabled) {
            filterBean.setFilter(new CheckUserLoginFilter4Platform());
        } else {
            filterBean.setFilter(new CheckUserLoginFilterSkipSSO());
        }
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
