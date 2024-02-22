package net.risesoft.log.service;

import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.model.log.AccessLog;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9TopicConst;

@Component
@Slf4j
@RequiredArgsConstructor
public class SaveLogInfo4Kafka {

    private KafkaTemplate<String, Object> y9KafkaTemplate;

    private final Environment environment;

    @Async("y9ThreadPoolTaskExecutor")
    public void asyncSave(final AccessLog log) {
        try {
            if (this.y9KafkaTemplate == null && Boolean.valueOf(environment.getProperty("y9.app.log.kafkaEnabled"))) {
                y9KafkaTemplate = Y9Context.getBean("y9KafkaTemplate");
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        if (y9KafkaTemplate != null) {
            try {
                String jsonString = Y9JsonUtil.writeValueAsString(log);
                y9KafkaTemplate.send(Y9TopicConst.Y9_ACCESSLOG_MESSAGE, jsonString);
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
    }
}
