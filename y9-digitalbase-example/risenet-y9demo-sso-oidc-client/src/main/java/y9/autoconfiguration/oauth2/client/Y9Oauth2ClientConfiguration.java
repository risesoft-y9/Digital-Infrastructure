package y9.autoconfiguration.oauth2.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

import net.risesoft.y9.Y9Context;

import y9.oauth2.client.filter.Y9OAuthFilter;

@Configuration
@ComponentScan(basePackages = {"net.risesoft.demo.sso", "y9.oauth2.client"})
public class Y9Oauth2ClientConfiguration {

    @Autowired
    private Environment env;

    @Bean
    @ConditionalOnMissingBean(value = Y9Context.class)
    public Y9Context y9Context() {
        return new Y9Context();
    }

    @Bean
    @DependsOn("y9Context")
    public FilterRegistrationBean<Y9OAuthFilter> y9OAuthFilter() {
        final FilterRegistrationBean<Y9OAuthFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new Y9OAuthFilter(this.env));
        filterBean.setAsyncSupported(false);
        filterBean.setOrder(Ordered.LOWEST_PRECEDENCE);
        filterBean.addUrlPatterns(env.getProperty("y9.feature.oauth2.client.protectedUrlPatterns").split(","));
        return filterBean;
    }

}
