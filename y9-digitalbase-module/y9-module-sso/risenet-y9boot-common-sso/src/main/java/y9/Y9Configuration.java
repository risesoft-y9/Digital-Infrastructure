package y9;

import java.util.List;

import javax.servlet.DispatcherType;

import org.apereo.cas.web.ProtocolEndpointWebSecurityConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.filter.ForwardedHeaderFilter;

import lombok.RequiredArgsConstructor;

import y9.repository.Y9LoginUserRepository;
import y9.repository.Y9UserRepository;
import y9.service.Y9KeyValueService;
import y9.service.Y9LoginUserService;
import y9.service.impl.Y9LoginUserJpaServiceImpl;
import y9.util.Y9Context;

@Lazy(false)
@EnableConfigurationProperties(Y9Properties.class)
@Configuration(proxyBeanMethods = true)
public class Y9Configuration {

    @Bean
    @ConditionalOnMissingBean(value = Y9Context.class)
    public Y9Context y9Context() {
        return new Y9Context();
    }

    /**
     * 针对经过反向代理的请求不能正确获得一些原始的请求信息，例如不能正确获得原始的 schema 导致重定向的 url 错误 <br/>
     * 此过滤器更多是为了减少外部 servlet 容器的配置 <br/>
     *
     * @return {@code FilterRegistrationBean<ForwardedHeaderFilter> }
     * @see <a href= "https://docs.spring.io/spring-security/reference/servlet/appendix/proxy-server.html">Proxy Server
     *      Configuration</a>
     */
    @Bean
    @ConditionalOnWarDeployment
    FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        ForwardedHeaderFilter filter = new ForwardedHeaderFilter();
        FilterRegistrationBean<ForwardedHeaderFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    @Bean
    public ProtocolEndpointWebSecurityConfigurer<Void> y9ResourceConfigurer() {
        return new ProtocolEndpointWebSecurityConfigurer<>() {
            @Override
            public List<String> getIgnoredEndpoints() {
                return List.of("/y9static/**", "/api/**", "/actuator/**");
            }
        };
    }

    @Bean
    public Runnable y9KeyValueCleaner(Y9KeyValueService y9KeyValueService) {
        return new Y9KeyValueCleaner(y9KeyValueService);
    }

    @RequiredArgsConstructor
    @EnableScheduling
    static class Y9KeyValueCleaner implements Runnable {
        private final Y9KeyValueService y9KeyValueService;

        @Override
        @Scheduled(cron = "0 * * * * *")
        public void run() {
            y9KeyValueService.cleanUpExpiredKeyValue();
        }
    }

    @Configuration
    @ConditionalOnProperty(value = "y9.loginInfoSaveTarget", havingValue = "jpa", matchIfMissing = true)
    static class Y9UserLoginJpaConfiguration {
        @Bean
        public Y9LoginUserService y9LoginUserServiceImpl(Y9LoginUserRepository y9LoginUserRepository,
            Y9UserRepository y9UserRepository) {
            return new Y9LoginUserJpaServiceImpl(y9LoginUserRepository, y9UserRepository);
        }

    }
}
