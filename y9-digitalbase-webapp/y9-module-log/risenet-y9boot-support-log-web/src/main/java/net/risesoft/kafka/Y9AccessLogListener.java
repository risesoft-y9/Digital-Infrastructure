package net.risesoft.kafka;

import java.util.Date;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9TopicConst;
import net.risesoft.y9public.entity.Y9logAccessLog;
import net.risesoft.y9public.service.Y9logAccessLogService;

@Component
@Slf4j
@RequiredArgsConstructor
public class Y9AccessLogListener {

    private final Y9logAccessLogService y9logAccessLogService;

    // kafka需要2.3.0以上的版本
    @KafkaListener(topics = {Y9TopicConst.Y9_ACCESSLOG_MESSAGE})
    public void listener(ConsumerRecord<String, String> data) {
        try {
            Y9logAccessLog y9logAccessLog = Y9JsonUtil.readValue(data.value(), Y9logAccessLog.class);
            y9logAccessLog.setLogTime(new Date());
            if (y9logAccessLog.getUserHostIp().contains(":")) {
                y9logAccessLog.setUserHostIp("127.0.0.1");
            }
            y9logAccessLogService.save(y9logAccessLog);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

}
