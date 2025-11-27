package net.risesoft.y9.configuration.feature.listener.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * 消息监听 - 监听 kafka 事件
 *
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "y9.feature.listener.kafka", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class Y9ListenerKafkaProperties {

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * org消息是否监听
     */
    private boolean messageOrgEnabled = false;

}
