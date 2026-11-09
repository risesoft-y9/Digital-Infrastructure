package net.risesoft;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.pubsub.Y9MessageListener;
import net.risesoft.y9.pubsub.Y9ReceivedMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscribeMessageListener implements Y9MessageListener {

    private static final String TEST_TOPIC = "test-topic";

    @Override
    public String topic() {
        return TEST_TOPIC;
    }

    @Override
    public void onMessage(Y9ReceivedMessage message) {
        LOGGER.info("收到订阅的主题 [{}] 的消息 [{}]", topic(), message.getPayload());
    }
}
