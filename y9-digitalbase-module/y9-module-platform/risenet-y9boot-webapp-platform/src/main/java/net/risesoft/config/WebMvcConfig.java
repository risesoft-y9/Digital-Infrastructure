package net.risesoft.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;

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
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
        supportedMediaTypes.add(MediaType.parseMediaType("text/html;charset=UTF-8"));
        // supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(new MediaType("application", "*+json"));
        supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        converter.setSupportedMediaTypes(supportedMediaTypes);
        return converter;
    }

    @Bean
    @ConditionalOnMissingBean
    public Y9Context y9Context() {
        return new Y9Context();
    }
}
