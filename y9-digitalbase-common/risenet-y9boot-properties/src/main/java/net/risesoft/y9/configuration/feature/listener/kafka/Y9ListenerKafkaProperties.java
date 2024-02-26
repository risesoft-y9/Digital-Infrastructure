package net.risesoft.y9.configuration.feature.listener.kafka;

import lombok.Getter;
import lombok.Setter;

/**
 * 监听 kafka 事件
 *
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
public class Y9ListenerKafkaProperties {

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * org消息主题
     */
    private String orgMessageTopic = "y9_org_event";

    /**
     * 任务消息主题
     */
    private String taskMessageTopic = "y9_task_event";

    /**
     * 公共消息主题
     */
    private String commonMessageTopic = "y9_common_event";
}
