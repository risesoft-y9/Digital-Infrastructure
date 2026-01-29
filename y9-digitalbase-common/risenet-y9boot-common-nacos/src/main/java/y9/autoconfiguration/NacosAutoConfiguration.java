package y9.autoconfiguration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.cloud.nacos.registry.NacosAutoServiceRegistration;

/**
 * nacos 自动配置
 *
 * @author shidaobang
 * @date 2024/02/06
 */
@Configuration(proxyBeanMethods = false)
public class NacosAutoConfiguration {

    @Bean
    @ConditionalOnProperty(value = "spring.cloud.nacos.discovery.enabled", matchIfMissing = true)
    @ConditionalOnWarDeployment
    public NacosAutoServiceRegistrationListener nacosAutoServiceRegistrationListener(NacosAutoServiceRegistration nacosAutoServiceRegistration) {
        return new NacosAutoServiceRegistrationListener(nacosAutoServiceRegistration);
    }

}
