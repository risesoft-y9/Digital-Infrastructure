package net.risesoft.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Configuration
@EnableConfigurationProperties(Y9Properties.class)
@EnableAsync(proxyTargetClass = true, mode = AdviceMode.ASPECTJ)
public class WebMvcConfig implements WebMvcConfigurer {

    // starter-log工程用到了RequestContextHolder
    // https://github.com/spring-projects/spring-boot/issues/2637
    // https://github.com/spring-projects/spring-boot/issues/4331
    @Bean
    public static RequestContextFilter requestContextFilter() {
        return new OrderedRequestContextFilter();
    }

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

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
        supportedMediaTypes.add(MediaType.parseMediaType("text/html;charset=UTF-8"));
        // supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        converter.setSupportedMediaTypes(supportedMediaTypes);
        return converter;
    }

    @Bean
    @ConditionalOnMissingBean
    public Y9Context y9Context() {
        return new Y9Context();
    }
}
