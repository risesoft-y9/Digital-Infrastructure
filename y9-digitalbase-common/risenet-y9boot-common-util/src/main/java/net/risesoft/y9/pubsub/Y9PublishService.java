package net.risesoft.y9.pubsub;

import net.risesoft.y9.pubsub.constant.Y9TopicConst;
import net.risesoft.y9.pubsub.message.Y9MessageCommon;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;

/**
 * 发布消息接口
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PublishService {

    /**
     * 提交 JSON 字符串消息。提交时发生的异常由调用方处理，返回不代表消息已送达。
     *
     * @param json JSON 字符串
     * @param topic 主题
     */
    void publishJson(String json, String topic);

    /**
     * 使用默认主题发布通用消息
     *
     * @param msg 通用消息
     */
    default void publishMessageCommon(Y9MessageCommon msg) {
        publishMessageCommon(msg, Y9TopicConst.Y9_COMMON_EVENT);
    }

    /**
     * 发布通用消息
     *
     * @param msg 通用消息
     * @param topic 主题
     */
    void publishMessageCommon(Y9MessageCommon msg, String topic);

    /**
     * 使用默认主题（Y9_ORG_EVENT）发布组织消息
     *
     * @param msg 组织消息
     */
    default void publishMessageOrg(Y9MessageOrg msg) {
        publishMessageOrg(msg, Y9TopicConst.Y9_ORG_EVENT);
    }

    /**
     * 发布组织消息
     *
     * @param msg 组织消息
     * @param topic 主题
     */
    void publishMessageOrg(Y9MessageOrg msg, String topic);

    /**
     * 发布对象消息
     *
     * @param obj 待发布对象
     * @param topic 主题
     */
    void publishObject(Object obj, String topic);

}
