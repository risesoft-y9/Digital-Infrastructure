package net.risesoft.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9TopicConst;
import net.risesoft.y9public.entity.Y9logFlowableAccessLog;
import net.risesoft.y9public.service.Y9logFlowableAccessLogService;

/**
 * @author qinman
 */
@Slf4j
@RequiredArgsConstructor
public class Y9FlowableAccessLogListener {

    private final Y9logFlowableAccessLogService y9logFlowableAccessLogService;

    // kafka需要2.3.0以上的版本
    @KafkaListener(topics = {Y9TopicConst.Y9_ACCESSLOG_MESSAGE_FLOWABLE})
    public void listener(ConsumerRecord<String, String> data) {
        try {
            Y9logFlowableAccessLog y9logFlowableAccessLog =
                Y9JsonUtil.readValue(data.value(), Y9logFlowableAccessLog.class);
            y9logFlowableAccessLogService.save(y9logFlowableAccessLog);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

}
