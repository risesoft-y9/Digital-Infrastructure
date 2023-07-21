package y9.autoconfiguration.oauth2.resource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

import y9.oauth2.resource.filter.Y9Oauth2ResourceFilter;

@Configuration
@ConditionalOnProperty(name = "y9.feature.oauth2.resource.enabled", havingValue = "true", matchIfMissing = true)
public class Y9Oauth2ResourceConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "y9Oauth2ResourceFilter")
    public FilterRegistrationBean<Y9Oauth2ResourceFilter> y9Oauth2ResourceFilter(Environment env) {
        String protectedUrlPatterns = env.getProperty("y9.feature.oauth2.resource.protectedUrlPatterns", "/services/rest/*");
        final FilterRegistrationBean<Y9Oauth2ResourceFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new Y9Oauth2ResourceFilter());
        filterBean.setAsyncSupported(false);
        filterBean.addUrlPatterns(protectedUrlPatterns.split(","));
        filterBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);

        return filterBean;
    }

}
