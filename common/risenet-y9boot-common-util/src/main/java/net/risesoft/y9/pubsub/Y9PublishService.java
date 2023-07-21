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

    public void publishJson(String json, String topic);

    default public void publishMessageCommon(Y9MessageCommon msg) {
        publishMessageCommon(msg, Y9TopicConst.Y9_COMMON_EVENT);
    };

    public void publishMessageCommon(Y9MessageCommon msg, String topic);

    default public void publishMessageOrg(Y9MessageOrg msg) {
        publishMessageOrg(msg, Y9TopicConst.Y9_ORG_EVENT);
    };

    public void publishMessageOrg(Y9MessageOrg msg, String topic);

    default public void publishMessageTask(Y9MessageTask msg) {
        publishMessageTask(msg, Y9TopicConst.Y9_TASK_EVENT);
    }

    public void publishMessageTask(Y9MessageTask msg, String topic);

    public void publishObject(Object obj, String topic);

}
