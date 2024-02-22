package y9.autoconfiguration.pubsub.kafka;

import org.springframework.kafka.core.KafkaTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.Y9PublishService;
import net.risesoft.y9.pubsub.message.Y9MessageCommon;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;
import net.risesoft.y9.pubsub.message.Y9MessageTask;

@Slf4j
@RequiredArgsConstructor
public class Y9PublishServiceKafka implements Y9PublishService {

    private final KafkaTemplate<Object, Object> y9KafkaTemplate;

    @Override
    public void publishJson(String json, String topic) {
        try {
            y9KafkaTemplate.send(topic, json);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    @Override
    public void publishMessageCommon(Y9MessageCommon msg, String topic) {
        try {
            String jsonString = Y9JsonUtil.writeValueAsString(msg);
            y9KafkaTemplate.send(topic, jsonString);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    @Override
    public void publishMessageOrg(Y9MessageOrg msg, String topic) {
        try {
            String jsonString = Y9JsonUtil.writeValueAsString(msg);
            y9KafkaTemplate.send(topic, jsonString);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    @Override
    public void publishMessageTask(Y9MessageTask msg, String topic) {
        try {
            String jsonString = Y9JsonUtil.writeValueAsString(msg);
            y9KafkaTemplate.send(topic, jsonString);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    @Override
    public void publishObject(Object obj, String topic) {
        try {
            String jsonString = Y9JsonUtil.writeValueAsString(obj);
            y9KafkaTemplate.send(topic, jsonString);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

}
