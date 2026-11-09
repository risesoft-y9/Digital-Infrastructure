package net.risesoft.y9.pubsub;

/**
 * 单主题业务消息监听器 <br/>
 * 
 * 应用可实现此接口并将其注册为 Spring Bean 实现消息的监听 <br/>
 * 此类主要是封装一些具体技术的使用细节
 */
public interface Y9MessageListener {

    /**
     * 订阅的原始 topic
     */
    String topic();

    /**
     * 处理该 topic 收到的消息。
     */
    void onMessage(Y9ReceivedMessage message);
}
