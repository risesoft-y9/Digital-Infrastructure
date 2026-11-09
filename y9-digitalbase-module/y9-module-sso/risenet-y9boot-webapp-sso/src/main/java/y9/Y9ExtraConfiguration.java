package y9;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import net.risesoft.y9.pubsub.Y9PublishService;

import y9.repository.Y9UserRepository;
import y9.service.Y9LoginUserService;
import y9.service.impl.Y9LoginUserKafkaServiceImpl;

@Lazy(false)
@EnableConfigurationProperties(Y9Properties.class)
@Configuration(proxyBeanMethods = true)
public class Y9ExtraConfiguration {

    @Configuration
    @ConditionalOnProperty(value = "y9.loginInfoSaveTarget", havingValue = "kafka")
    static class Y9UserLoginKafkaConfiguration {

        @Bean
        public Y9LoginUserService y9LoginUserKafkaServiceImpl(Y9UserRepository y9UserRepository,
            Y9PublishService y9PublishService) {
            return new Y9LoginUserKafkaServiceImpl(y9UserRepository, y9PublishService);
        }

    }

}
