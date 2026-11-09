package net.risesoft.y9.pubsub;

import lombok.Value;

/**
 * 收到的消息正文。
 */
@Value
public class Y9ReceivedMessage {

    String payload;
}
