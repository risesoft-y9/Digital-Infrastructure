package y9.autoconfiguration.pubsub.kafka;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import net.risesoft.y9.pubsub.Y9PublishService;

@Configuration
@ConditionalOnProperty(name = "y9.feature.publish.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class Y9PublishKafkaConfiguration {

    @PostConstruct
    public void init() {
        // System.out.println("init Y9PublishKafkaConfiguration...");
    }

    @Bean("y9KafkaTemplate")
    @ConditionalOnMissingBean(name = "y9KafkaTemplate")
    public KafkaTemplate<?, ?> y9KafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory) {
        return new KafkaTemplate<>(kafkaProducerFactory);
    }

    @Primary
    @Bean({"y9PublishService", "y9PublishServiceKafka"})
    public Y9PublishService y9PublishServiceKafka(KafkaTemplate<Object, Object> y9KafkaTemplate) {
        return new Y9PublishServiceKafka(y9KafkaTemplate);
    }

}
