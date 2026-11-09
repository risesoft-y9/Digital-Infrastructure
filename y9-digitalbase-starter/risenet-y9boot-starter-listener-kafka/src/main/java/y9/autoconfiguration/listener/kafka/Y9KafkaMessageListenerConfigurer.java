package y9.autoconfiguration.listener.kafka;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.util.ReflectionUtils;

import lombok.RequiredArgsConstructor;

import net.risesoft.y9.pubsub.Y9MessageListener;
import net.risesoft.y9.pubsub.Y9ReceivedMessage;
import net.risesoft.y9.pubsub.kafka.Y9TopicNameResolver;

/**
 * 将业务订阅注册到应用的 Kafka 监听容器工厂。
 */
@RequiredArgsConstructor
class Y9KafkaMessageListenerConfigurer implements KafkaListenerConfigurer {

    private static final Method RECEIVE_METHOD =
        ReflectionUtils.findMethod(MessageAdapter.class, "receive", ConsumerRecord.class);

    private final ListableBeanFactory beanFactory;

    private final Y9TopicNameResolver topicNameResolver;

    @Override
    public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
        MessageHandlerMethodFactory handlerFactory = registrar.getMessageHandlerMethodFactory();
        if (handlerFactory == null) {
            DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
            factory.afterPropertiesSet();
            handlerFactory = factory;
        }

        for (Map.Entry<String, Y9MessageListener> entry : beanFactory.getBeansOfType(Y9MessageListener.class)
            .entrySet()) {
            Y9MessageListener listener = entry.getValue();
            String topic = listener.topic();
            MethodKafkaListenerEndpoint<Object, Object> endpoint = new MethodKafkaListenerEndpoint<>();
            endpoint.setId("y9MessageListener." + entry.getKey() + "." + topic);
            endpoint.setTopics(topicNameResolver.resolve(topic));
            endpoint.setBean(new MessageAdapter(listener));
            endpoint.setMethod(RECEIVE_METHOD);
            endpoint.setMessageHandlerMethodFactory(handlerFactory);
            registrar.registerEndpoint(endpoint);
        }
    }

    @RequiredArgsConstructor
    private static class MessageAdapter {

        private final Y9MessageListener listener;

        public void receive(ConsumerRecord<?, ?> record) {
            listener.onMessage(new Y9ReceivedMessage(Objects.toString(record.value(), null)));
        }
    }
}
