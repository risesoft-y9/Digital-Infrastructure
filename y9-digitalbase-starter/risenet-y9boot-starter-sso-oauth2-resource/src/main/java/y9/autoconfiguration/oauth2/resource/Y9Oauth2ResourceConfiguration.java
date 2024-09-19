package y9.autoconfiguration.oauth2.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.kafka.core.KafkaTemplate;

import net.risesoft.y9.configuration.Y9Properties;

import y9.oauth2.resource.filter.Y9Oauth2ResourceFilter;

@Configuration
@EnableConfigurationProperties(Y9Properties.class)
@ConditionalOnProperty(name = "y9.feature.oauth2.resource.enabled", havingValue = "true", matchIfMissing = true)
public class Y9Oauth2ResourceConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "y9Oauth2ResourceFilter")
    public FilterRegistrationBean<Y9Oauth2ResourceFilter> y9Oauth2ResourceFilter(Y9Properties y9Properties,
        @Autowired(required = false) KafkaTemplate<String, Object> y9KafkaTemplate) {
        final FilterRegistrationBean<Y9Oauth2ResourceFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new Y9Oauth2ResourceFilter(y9Properties, y9KafkaTemplate));
        filterBean.setAsyncSupported(false);
        filterBean.setUrlPatterns(y9Properties.getFeature().getOauth2().getResource().getProtectedUrlPatterns());
        filterBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);

        return filterBean;
    }

}
