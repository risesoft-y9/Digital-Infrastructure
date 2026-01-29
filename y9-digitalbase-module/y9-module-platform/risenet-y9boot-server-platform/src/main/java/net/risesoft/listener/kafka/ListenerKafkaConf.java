package net.risesoft.listener.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.message.Y9MessageOrgReply;
import net.risesoft.y9public.entity.event.Y9PublishedEventListener;
import net.risesoft.y9public.repository.event.Y9PublishedEventListenerRepository;

@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(KafkaAutoConfiguration.class)
@ConditionalOnProperty(name = "y9.feature.listener.kafka.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class ListenerKafkaConf {

    private Y9PublishedEventListenerRepository y9PublishedEventListenerRepository;

    @KafkaListener(topics = {"y9_org_event_reply"})
    public void messageOrgReplyListener4Kafka(ConsumerRecord<String, String> data) {
        String value = data.value().toString();
        Y9MessageOrgReply msg = null;
        try {
            msg = Y9JsonUtil.readValue(value, Y9MessageOrgReply.class);
            if (this.y9PublishedEventListenerRepository == null) {
                this.y9PublishedEventListenerRepository = Y9Context.getBean(Y9PublishedEventListenerRepository.class);
            }
            if (this.y9PublishedEventListenerRepository != null) {
                Y9PublishedEventListener entity = new Y9PublishedEventListener();
                entity.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                entity.setClientIp(msg.getClientIp());
                entity.setEventType(msg.getEventType());
                entity.setSystemName(msg.getSystemName());
                entity.setTenantId(msg.getTenantId());
                y9PublishedEventListenerRepository.save(entity);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
