package net.risesoft;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.pubsub.Y9PublishService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PublishMessageController {

    private static final String TEST_TOPIC = "test-topic";

    private final Y9PublishService publishService;

    @RequestMapping("/publishMessage")
    public void publishMessage() {
        String message = "test-message";
        publishService.publishObject(message, TEST_TOPIC);
        LOGGER.info("往 kafka 的主题 [{}] 发布消息 [{}]", TEST_TOPIC, message);
    }
}
