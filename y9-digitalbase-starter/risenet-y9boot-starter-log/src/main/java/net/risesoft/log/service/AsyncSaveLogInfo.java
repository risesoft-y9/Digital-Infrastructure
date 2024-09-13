package net.risesoft.log.service;

import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.model.log.AccessLog;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.feature.log.Y9LogProperties;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9TopicConst;
import net.risesoft.y9.util.RemoteCallUtil;

@Slf4j
@RequiredArgsConstructor
public class AsyncSaveLogInfo {

    private final Y9Properties y9Properties;
    private KafkaTemplate<String, Object> y9KafkaTemplate;

    @Async("y9ThreadPoolTaskExecutor")
    public void asyncSave(final AccessLog log) {
        Y9LogProperties.LogSaveTarget logSaveTarget = y9Properties.getFeature().getLog().getLogSaveTarget();
        if (Y9LogProperties.LogSaveTarget.KAFKA.equals(logSaveTarget)) {
            try {
                if (this.y9KafkaTemplate == null) {
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
        } else if (Y9LogProperties.LogSaveTarget.API.equals(logSaveTarget)) {
            String logBaseUrl = y9Properties.getCommon().getLogBaseUrl();
            String url = logBaseUrl + "/services/rest/v1/accessLog/asyncSaveLog";
            List<NameValuePair> requestBody = RemoteCallUtil.objectToNameValuePairList(log);
            RemoteCallUtil.post(url, null, requestBody, Object.class);
        }
    }
}
