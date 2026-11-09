package net.risesoft.y9.configuration.feature.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Kafka 收发端共用的主题名称配置。
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "y9.feature.kafka")
public class Y9KafkaProperties {

    /**
     * 主题名前缀，包含分隔符，例如 dev_。默认为空，兼容已有主题。
     */
    private String topicNamePrefix = "";

}
