package y9.autoconfiguration.listener.kafka;

import java.util.Objects;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.feature.listener.kafka.Y9ListenerKafkaProperties;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;
import net.risesoft.y9.pubsub.constant.Y9TopicConst;
import net.risesoft.y9.pubsub.event.Y9EventOrg;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;
import net.risesoft.y9.pubsub.message.Y9MessageOrgReply;

@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(KafkaAutoConfiguration.class)
@ConditionalOnProperty(name = "y9.feature.listener.kafka.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
@EnableConfigurationProperties(Y9ListenerKafkaProperties.class)
public class Y9ListenerKafkaConfiguration {

    private KafkaTemplate<Object, Object> y9KafkaTemplate;

    @KafkaListener(topics = {"y9_org_event"})
    public void messageOrgListener4kafka(ConsumerRecord<String, String> data) {
        String value = data.value();
        Y9MessageOrg msg = null;
        try {
            msg = Y9JsonUtil.readValue(value, Y9MessageOrg.class);
            Y9MessageOrgReply msgReply = new Y9MessageOrgReply();
            msgReply.setClientIp(Y9Context.getHostIp());
            msgReply.setSystemName(Y9Context.getSystemName());
            msgReply.setEventType(msg.getEventType());
            msgReply.setTenantId(msg.getTenantId());
            String json = Y9JsonUtil.writeValueAsString(msgReply);

            if (this.y9KafkaTemplate == null) {
                this.y9KafkaTemplate = Y9Context.getBean("y9KafkaTemplate");
            }
            if (this.y9KafkaTemplate != null) {
                this.y9KafkaTemplate.send(Y9TopicConst.Y9_ORG_EVENT_REPLY, json);
                LOGGER.info("向消息中间件发送回执。");
            }
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
