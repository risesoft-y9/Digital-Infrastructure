package y9.autoconfiguration.producer.kafka;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import lombok.extern.slf4j.Slf4j;

@Configuration
@AutoConfigureAfter(KafkaAutoConfiguration.class)
@ConditionalOnProperty(value = "y9.common.kafkaEnabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class Y9KafkaConfiguration {

    @Primary
    @Bean("y9KafkaTemplate")
    @ConditionalOnMissingBean(name = "y9KafkaTemplate")
    public KafkaTemplate<?, ?> y9KafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory) {
        LOGGER.info("Y9KafkaConfiguration y9KafkaTemplate init ......");

        return new KafkaTemplate<>(kafkaProducerFactory);
    }
}
