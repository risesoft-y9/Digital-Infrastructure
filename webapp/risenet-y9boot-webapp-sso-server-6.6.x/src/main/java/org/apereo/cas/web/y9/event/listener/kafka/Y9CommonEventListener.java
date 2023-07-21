package org.apereo.cas.web.y9.event.listener.kafka;

import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apereo.cas.web.y9.event.message.Y9MessageCommon;
import org.apereo.cas.web.y9.util.Y9Context;
import org.apereo.cas.web.y9.util.json.Y9JacksonUtil;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Y9CommonEventListener {

    private ContextRefresher contextRefresher;

    @KafkaListener(topics = {"y9_common_event"})
    public void messageCommonListener4kafka(ConsumerRecord<String, String> data) {
        String value = data.value().toString();
        Y9MessageCommon msg = null;
        try {
            msg = Y9JacksonUtil.readValue(value, Y9MessageCommon.class);
            String eventType = msg.getEventType();
            if (Y9MessageCommon.RefreshRemoteApplicationEvent.equals(eventType)) {
                if (contextRefresher == null) {
                    try {
                        this.contextRefresher = Y9Context.getBean(ContextRefresher.class);
                    } catch (Exception e) {
                        LOGGER.error("contextRefresher==null，刷新bean信息失败。", e);
                    }
                }

                if (contextRefresher != null) {
                    Set<String> keys = contextRefresher.refresh();
                    LOGGER.info("Received remote refresh request. Keys refreshed " + keys);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}