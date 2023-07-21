package net.risesoft.y9.configuration.feature.publish;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9.configuration.feature.publish.kafka.Y9PublishKafkaProperties;
import net.risesoft.y9.configuration.feature.publish.redis.Y9PublishRedisProperties;

/**
 * 消息发布配置
 *
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
public class Y9PublishProperties {

    @NestedConfigurationProperty
    private Y9PublishRedisProperties redis = new Y9PublishRedisProperties();

    @NestedConfigurationProperty
    private Y9PublishKafkaProperties kafka = new Y9PublishKafkaProperties();
}
