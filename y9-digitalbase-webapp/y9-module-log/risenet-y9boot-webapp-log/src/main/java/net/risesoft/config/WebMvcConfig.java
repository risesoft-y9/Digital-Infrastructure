package net.risesoft.config;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import net.risesoft.util.EsIndexDate;
import net.risesoft.y9.Y9Context;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // starter-log工程用到了RequestContextHolder
    // https://github.com/spring-projects/spring-boot/issues/2637
    // https://github.com/spring-projects/spring-boot/issues/4331
    @Bean
    public static RequestContextFilter requestContextFilter() {
        return new OrderedRequestContextFilter();
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(responseBodyConverter());
    }

    @Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        // List<MediaType> supportedMediaTypes = new ArrayList<>();
        // supportedMediaTypes.add(MediaType.TEXT_HTML);
        // supportedMediaTypes.add(MediaType.parseMediaType("text/html;charset=UTF-8"));
        // supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        // converter.setSupportedMediaTypes(supportedMediaTypes);
        return converter;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.parseMediaType("text/html;charset=UTF-8"));
        supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(new MediaType("application", "*+json"));
        converter.setSupportedMediaTypes(supportedMediaTypes);
        return converter;
    }

    @Bean
    public Y9Context y9Context() {
        return new Y9Context();
    }

    @Bean
    public EsIndexDate esIndexDate() {
        return new EsIndexDate();
    }
}
