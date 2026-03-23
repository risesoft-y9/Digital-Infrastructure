package y9.autoconfiguration.systemregistration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import net.risesoft.api.platform.resource.SystemApi;
import net.risesoft.system.registration.SystemRegistrationRunner;
import net.risesoft.y9.configuration.feature.systemregistration.Y9SystemRegistrationProperties;

/**
 * 启动注册自动配置类
 *
 * @author shidaobang
 * @since 9.6.10
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(Y9SystemRegistrationProperties.class)
@ConditionalOnProperty(prefix = "y9.feature.system-registration", name = "enabled", havingValue = "true",
    matchIfMissing = true)
public class Y9SystemRegistrationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SystemRegistrationRunner systemRegistrationRunner(Y9SystemRegistrationProperties properties,
        ResourceLoader resourceLoader, SystemApi systemApi) {
        return new SystemRegistrationRunner(properties, resourceLoader, systemApi);
    }
}
