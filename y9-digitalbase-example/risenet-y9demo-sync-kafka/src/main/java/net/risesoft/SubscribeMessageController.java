package net.risesoft;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SubscribeMessageController {

    private static final String TEST_TOPIC = "test-topic";

    @KafkaListener(topics = TEST_TOPIC)
    public void listenMessage(String message) {
        LOGGER.info("收到订阅的 kafka 主题 [{}] 的消息 [{}]", TEST_TOPIC, message);
    }
}
