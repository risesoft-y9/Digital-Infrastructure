package y9.autoconfiguration.pubsub.kafka;

import org.springframework.kafka.core.KafkaTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.kafka.Y9TopicNameResolver;
import net.risesoft.y9.pubsub.Y9PublishService;
import net.risesoft.y9.pubsub.message.Y9MessageCommon;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;

@Slf4j
@RequiredArgsConstructor
public class Y9PublishServiceKafka implements Y9PublishService {

    private final KafkaTemplate<Object, Object> y9KafkaTemplate;

    private final Y9TopicNameResolver y9TopicNameResolver;

    @Override
    public void publishJson(String json, String topic) {
        y9KafkaTemplate.send(y9TopicNameResolver.resolve(topic), json);
    }

    @Override
    public void publishMessageCommon(Y9MessageCommon msg, String topic) {
        publishObject(msg, topic);
    }

    @Override
    public void publishMessageOrg(Y9MessageOrg msg, String topic) {
        publishObject(msg, topic);
    }

    @Override
    public void publishObject(Object obj, String topic) {
        try {
            publishJson(Y9JsonUtil.writeValueAsString(obj), topic);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

}
