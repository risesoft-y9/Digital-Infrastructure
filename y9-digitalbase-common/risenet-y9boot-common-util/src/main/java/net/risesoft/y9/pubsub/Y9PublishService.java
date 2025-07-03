package net.risesoft.y9.pubsub;

import net.risesoft.y9.pubsub.constant.Y9TopicConst;
import net.risesoft.y9.pubsub.message.Y9MessageCommon;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;
import net.risesoft.y9.pubsub.message.Y9MessageTask;

/**
 * 发布消息接口
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PublishService {

    void publishJson(String json, String topic);

    default void publishMessageCommon(Y9MessageCommon msg) {
        publishMessageCommon(msg, Y9TopicConst.Y9_COMMON_EVENT);
    }

    void publishMessageCommon(Y9MessageCommon msg, String topic);

    default void publishMessageOrg(Y9MessageOrg msg) {
        publishMessageOrg(msg, Y9TopicConst.Y9_ORG_EVENT);
    }

    void publishMessageOrg(Y9MessageOrg msg, String topic);

    default void publishMessageTask(Y9MessageTask msg) {
        publishMessageTask(msg, Y9TopicConst.Y9_TASK_EVENT);
    }

    void publishMessageTask(Y9MessageTask msg, String topic);

    void publishObject(Object obj, String topic);

}
