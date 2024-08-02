package net.risesoft.y9.pubsub.message;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;

/**
 * 通用消息
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Y9MessageCommon implements Serializable {
    private static final long serialVersionUID = -1107265840539410276L;

    /** 处理消息的系统名 */
    private String eventTarget = Y9OrgEventConst.EVENT_TARGET_ALL;

    /**
     * 事件内容 <br>
     * 
     * {@link JsonTypeInfo} 解决 jackson 序列化丢失类型
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    private Serializable eventObject;

    /** 事件类型 */
    private String eventType;
}