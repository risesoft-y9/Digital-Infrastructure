package net.risesoft.eventsource;

import java.util.Objects;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9CommonEventConst;
import net.risesoft.y9.pubsub.event.Y9EventCommon;
import net.risesoft.y9.pubsub.message.Y9MessageCommon;

/**
 * @author shidaobang
 * @date 2023/12/07
 * @since 9.6.3
 */
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageCommon {

    @KafkaListener(topics = {"y9_common_event"})
    public void messageCommonListener4kafka(ConsumerRecord<String, String> data) {
        String value = data.value();
        try {
            Y9MessageCommon msg = Y9JsonUtil.readValue(value, Y9MessageCommon.class);
            String eventType = msg.getEventType();

            if ((Y9CommonEventConst.TENANT_SYSTEM_REGISTERED.equals(eventType)
                || Y9CommonEventConst.TENANT_APP_REGISTERED.equals(eventType))
                && Objects.equals(Y9Context.getSystemName(), msg.getEventTarget())) {
                // 对于非当前引入的系统的消息不处理
                
                Y9EventCommon event = new Y9EventCommon();
                event.setEventType(msg.getEventType());
                event.setEventObject(msg.getEventObject());
                event.setTarget(msg.getEventTarget());
                Y9Context.publishEvent(event);
                LOGGER.info("[common]将消息中间件发过来的消息转换成spring的事件后发送：{}", event);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


    /**
     * 监听租户数据源同步事件，这里的 groupId 使用随机生成保证引入服务在多实例时都能监听到 <br>
     * 可根据实际情况调整 kafka 的 offsets.retention.minutes （无活跃成员的消费者组的保留时长）配置项清理无用的消费者组
     */
    @KafkaListener(topics = {"y9_common_event"},
        groupId = "${spring.kafka.consumer.group-id}-${random.uuid}")
    public void dataSourceSyncListener4kafka(ConsumerRecord<String, String> data) {
        String value = data.value();
        try {
            Y9MessageCommon msg = Y9JsonUtil.readValue(value, Y9MessageCommon.class);
            String eventType = msg.getEventType();

            if (Y9CommonEventConst.TENANT_DATASOURCE_SYNC.equals(eventType)
                && Objects.equals(Y9Context.getSystemName(), msg.getEventTarget())) {
                // 对于非当前引入的系统的消息不处理

                Y9EventCommon event = new Y9EventCommon();
                event.setEventType(msg.getEventType());
                event.setEventObject(msg.getEventObject());
                event.setTarget(msg.getEventTarget());
                Y9Context.publishEvent(event);
                LOGGER.info("[common]将消息中间件发过来的消息转换成spring的事件后发送：{}", event);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
