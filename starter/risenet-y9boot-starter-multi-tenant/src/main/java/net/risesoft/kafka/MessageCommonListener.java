package net.risesoft.kafka;

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
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

/**
 * @author shidaobang
 * @date 2023/12/07
 * @since 9.6.3
 */
@RequiredArgsConstructor
@Slf4j
public class MessageCommonListener {

    private final Y9TenantDataSourceLookup y9TenantDataSourceLookup;

    @KafkaListener(topics = {"y9_common_event"})
    public void messageCommonListener4kafka(ConsumerRecord<String, String> data) {
        String value = data.value();
        try {
            Y9MessageCommon msg = Y9JsonUtil.readValue(value, Y9MessageCommon.class);
            String eventType = msg.getEventType();

            if (Y9CommonEventConst.TENANT_DATASOURCE_SYNC.equals(eventType)) {
                this.y9TenantDataSourceLookup.loadDataSources();
                LOGGER.info(this.y9TenantDataSourceLookup.getSystemName() + ", 同步租户数据源信息, 成功！");
                return;
            }

            if ((Y9CommonEventConst.TENANT_SYSTEM_REGISTERED.equals(eventType)
                || Y9CommonEventConst.TENANT_SYSTEM_INITIALIZED.equals(eventType))
                && !Objects.equals(Y9Context.getSystemName(), msg.getEventTarget())) {
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
}
