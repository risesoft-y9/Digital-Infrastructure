package y9.autoconfiguration.producer.kafka;

import java.util.Objects;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9CommonEventConst;
import net.risesoft.y9.pubsub.event.Y9EventCommon;
import net.risesoft.y9.pubsub.message.Y9MessageCommon;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

@Configuration
@AutoConfigureAfter(KafkaAutoConfiguration.class)
@ConditionalOnProperty(value = "y9.common.kafkaEnabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class Y9KafkaConfiguration {

    private Y9TenantDataSourceLookup y9TenantDataSourceLookup;

    @KafkaListener(topics = {"y9_common_event"})
    public void messageCommonListener4kafka(ConsumerRecord<String, String> data) {
        String value = data.value().toString();
        Y9MessageCommon msg = null;
        try {
            msg = Y9JsonUtil.readValue(value, Y9MessageCommon.class);
            String eventType = msg.getEventType();

            if (Y9CommonEventConst.TENANT_DATASOURCE_SYNC.equals(eventType)) {
                if (this.y9TenantDataSourceLookup == null) {
                    try {
                        this.y9TenantDataSourceLookup = Y9Context.getBean(Y9TenantDataSourceLookup.class);
                    } catch (Exception e) {
                        LOGGER.error("tenantDataSource==null，同步租户数据源信息失败。", e);
                        return;
                    }
                }
                if (y9TenantDataSourceLookup != null) {
                    this.y9TenantDataSourceLookup.loadDataSources();
                    LOGGER.info(this.y9TenantDataSourceLookup.getSystemName() + ", 同步租户数据源信息, 成功！");
                }
                return;
            }

            if (Y9CommonEventConst.TENANT_SYSTEM_REGISTERED.equals(eventType)
                && !Objects.equals(Y9Context.getSystemName(), msg.getTarget())) {
                // 对于非当前引入的系统的消息不处理
                return;
            }

            Y9EventCommon event = new Y9EventCommon();
            event.setEventType(msg.getEventType());
            event.setEventObject(msg.getEventObject());
            Y9Context.publishEvent(event);
            LOGGER.info("[common]将消息中间件发过来的消息转换成spring的事件后发送：{}", event);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Bean
    @ConditionalOnMissingBean(value = Y9Context.class)
    public Y9Context y9Context() {
        return new Y9Context();
    }

    @Primary
    @Bean("y9KafkaTemplate")
    @ConditionalOnMissingBean(name = "y9KafkaTemplate")
    public KafkaTemplate<?, ?> y9KafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory) {
        LOGGER.info("Y9KafkaConfiguration y9KafkaTemplate init ......");

        return new KafkaTemplate<>(kafkaProducerFactory);
    }
}
