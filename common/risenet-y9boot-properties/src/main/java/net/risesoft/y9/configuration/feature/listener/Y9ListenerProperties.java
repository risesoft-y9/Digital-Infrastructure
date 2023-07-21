package net.risesoft.y9.configuration.feature.listener;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9.configuration.feature.listener.kafka.Y9ListenerKafkaProperties;
import net.risesoft.y9.configuration.feature.listener.redis.Y9ListenerRedisProperties;

/**
 * 监听事件配置
 *
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
public class Y9ListenerProperties {

    @NestedConfigurationProperty
    private Y9ListenerRedisProperties redis = new Y9ListenerRedisProperties();

    @NestedConfigurationProperty
    private Y9ListenerKafkaProperties kafka = new Y9ListenerKafkaProperties();

}
