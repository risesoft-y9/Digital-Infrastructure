package y9;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import lombok.extern.slf4j.Slf4j;

import y9.repository.Y9UserRepository;
import y9.service.Y9LoginUserService;
import y9.service.impl.Y9LoginUserKafkaServiceImpl;

@Lazy(false)
@EnableConfigurationProperties(Y9Properties.class)
@Configuration(proxyBeanMethods = true)
public class Y9ExtraConfiguration {

    @Configuration
    @ConditionalOnProperty(value = "y9.loginInfoSaveTarget", havingValue = "kafka")
    @Slf4j
    @EnableConfigurationProperties(KafkaProperties.class)
    static class Y9UserLoginKafkaConfiguration {

        @Bean("y9KafkaTemplate")
        @ConditionalOnMissingBean(name = "y9KafkaTemplate")
        public KafkaTemplate<?, ?> y9KafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory) {
            LOGGER.info("Y9UserLoginKafkaConfiguration y9KafkaTemplate init ......");
            return new KafkaTemplate<>(kafkaProducerFactory);
        }

        @Bean
        public Y9LoginUserService y9LoginUserKafkaServiceImpl(Y9UserRepository y9UserRepository,
            KafkaTemplate<String, Object> y9KafkaTemplate) {
            return new Y9LoginUserKafkaServiceImpl(y9UserRepository, y9KafkaTemplate);
        }

    }

}
