package y9.autoconfiguration.pubsub.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import net.risesoft.y9.configuration.feature.kafka.Y9KafkaProperties;
import net.risesoft.y9.configuration.feature.publish.Y9PublishProperties;
import net.risesoft.y9.pubsub.kafka.Y9TopicNameResolver;
import net.risesoft.y9.pubsub.Y9PublishService;

@Configuration
@ConditionalOnProperty(name = "y9.feature.publish.kafka.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({Y9KafkaProperties.class, Y9PublishProperties.class})
public class Y9PublishKafkaConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "y9TopicNameResolver")
    public Y9TopicNameResolver y9TopicNameResolver(Y9KafkaProperties properties) {
        return new Y9TopicNameResolver(properties.getTopicNamePrefix());
    }

    @Bean("y9KafkaTemplate")
    @ConditionalOnMissingBean(name = "y9KafkaTemplate")
    public KafkaTemplate<?, ?> y9KafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory) {
        return new KafkaTemplate<>(kafkaProducerFactory);
    }

    @Primary
    @Bean({"y9PublishService", "y9PublishServiceKafka"})
    public Y9PublishService y9PublishServiceKafka(KafkaTemplate<Object, Object> y9KafkaTemplate,
        Y9TopicNameResolver y9TopicNameResolver) {
        return new Y9PublishServiceKafka(y9KafkaTemplate, y9TopicNameResolver);
    }

}
