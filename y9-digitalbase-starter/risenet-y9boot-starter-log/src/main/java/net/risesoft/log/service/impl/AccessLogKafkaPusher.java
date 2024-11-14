package net.risesoft.log.service.impl;

import org.springframework.kafka.core.KafkaTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.service.AccessLogPusher;
import net.risesoft.model.log.AccessLog;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9TopicConst;

/**
 * 访问日志 kafka 推送
 *
 * @author shidaobang
 * @date 2024/11/14
 * @since 9.6.8
 */
@Slf4j
@RequiredArgsConstructor
public class AccessLogKafkaPusher implements AccessLogPusher {

    private final KafkaTemplate<String, Object> y9KafkaTemplate;

    @Override
    public void push(final AccessLog log) {
        try {
            String jsonString = Y9JsonUtil.writeValueAsString(log);
            y9KafkaTemplate.send(Y9TopicConst.Y9_ACCESSLOG_MESSAGE, jsonString);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}
