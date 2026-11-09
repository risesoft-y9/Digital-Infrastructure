package net.risesoft.y9.pubsub.kafka;

/**
 * 为原 Kafka 主题名添加配置的前缀。收发端必须使用相同配置。
 */
public class Y9TopicNameResolver {

    private final String prefix;

    public Y9TopicNameResolver(String prefix) {
        this.prefix = prefix == null ? "" : prefix;
    }

    /**
     * 参数为业务主题名；请勿传入已经解析过的主题名，以免重复添加前缀。
     */
    public String resolve(String topicName) {
        return prefix + topicName;
    }
}
