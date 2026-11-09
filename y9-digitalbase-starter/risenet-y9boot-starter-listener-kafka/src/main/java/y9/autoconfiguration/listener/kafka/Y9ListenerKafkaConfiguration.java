package y9.autoconfiguration.listener.kafka;

import java.util.Objects;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.feature.kafka.Y9KafkaProperties;
import net.risesoft.y9.configuration.feature.listener.kafka.Y9ListenerKafkaProperties;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.kafka.Y9TopicNameResolver;
import net.risesoft.y9.pubsub.Y9MessageListener;
import net.risesoft.y9.pubsub.Y9ReceivedMessage;
import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;
import net.risesoft.y9.pubsub.constant.Y9TopicConst;
import net.risesoft.y9.pubsub.event.Y9EventOrg;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;

@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(KafkaAutoConfiguration.class)
@ConditionalOnProperty(name = "y9.feature.listener.kafka.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
@EnableKafka
@EnableConfigurationProperties({Y9KafkaProperties.class, Y9ListenerKafkaProperties.class})
public class Y9ListenerKafkaConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "y9TopicNameResolver")
    public Y9TopicNameResolver y9TopicNameResolver(Y9KafkaProperties properties) {
        return new Y9TopicNameResolver(properties.getTopicNamePrefix());
    }

    @Bean
    public KafkaListenerConfigurer y9MessageListenerConfigurer(ListableBeanFactory beanFactory,
        Y9TopicNameResolver topicNameResolver) {
        return new Y9KafkaMessageListenerConfigurer(beanFactory, topicNameResolver);
    }

    @Bean
    @ConditionalOnProperty(name = "y9.feature.listener.kafka.message-org-enabled", havingValue = "true")
    public Y9MessageOrgKafkaListener y9MessageOrgKafkaListener() {
        return new Y9MessageOrgKafkaListener();
    }

    public static class Y9MessageOrgKafkaListener implements Y9MessageListener {

        @Override
        public String topic() {
            return Y9TopicConst.Y9_ORG_EVENT;
        }

        @Override
        public void onMessage(Y9ReceivedMessage message) {
            String value = message.getPayload();
            Y9MessageOrg msg = null;
            try {
                msg = Y9JsonUtil.readValue(value, Y9MessageOrg.class);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

            if (Y9OrgEventConst.EVENT_TARGET_ALL.equals(msg.getEventTarget())
                    || Objects.equals(Y9Context.getSystemName(), msg.getEventTarget())) {
                // 只有消息目标是 所有系统 或 当前引入系统 时才处理
                try {
                    Y9EventOrg event = new Y9EventOrg();
                    event.setEventType(msg.getEventType());
                    event.setOrgObj(msg.getOrgObj());
                    event.setTenantId(msg.getTenantId());

                    Y9Context.publishEvent(event);
                    LOGGER.info("[org]将消息中间件发过来的消息转换成spring的事件后发送：" + event);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean(value = Y9Context.class)
    public Y9Context y9Context() {
        return new Y9Context();
    }

    @Bean("y9KafkaTemplate")
    @ConditionalOnMissingBean(name = "y9KafkaTemplate")
    public KafkaTemplate<?, ?> y9KafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory) {
        return new KafkaTemplate<>(kafkaProducerFactory);
    }
}
