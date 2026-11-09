package net.risesoft.y9.configuration.feature.publish.kafka;

import lombok.Getter;
import lombok.Setter;

/**
 * kafka 消息发布配置
 * 
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
public class Y9PublishKafkaProperties {

    /**
     * 是否启用
     */
    private boolean enabled;

}
