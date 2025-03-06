package y9.autoconfiguration.oauth2.resource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.risesoft.consts.FilterOrderConsts;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.feature.oauth2.resource.Y9Oauth2ResourceProperties;

import y9.oauth2.resource.filter.Y9Oauth2ResourceFilter;

@Configuration
@EnableConfigurationProperties({Y9Properties.class, Y9Oauth2ResourceProperties.class})
@ConditionalOnProperty(name = "y9.feature.oauth2.resource.enabled", havingValue = "true", matchIfMissing = true)
public class Y9Oauth2ResourceConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "y9Oauth2ResourceFilter")
    public FilterRegistrationBean<Y9Oauth2ResourceFilter>
        y9Oauth2ResourceFilter(Y9Oauth2ResourceProperties y9Oauth2ResourceProperties) {
        final FilterRegistrationBean<Y9Oauth2ResourceFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new Y9Oauth2ResourceFilter(y9Oauth2ResourceProperties));
        filterBean.setAsyncSupported(false);
        filterBean.setUrlPatterns(y9Oauth2ResourceProperties.getProtectedUrlPatterns());
        filterBean.setOrder(FilterOrderConsts.OAUTH2_RESOURCE_ORDER);

        return filterBean;
    }

}
