package net.risesoft.y9.pubsub.message;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;

/**
 * 组织消息
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Y9MessageOrg<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 6408566736524500841L;

    // 解决 jackson 序列化丢失类型
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    private T orgObj;

    /** 事件类型 */
    private String eventType;

    /** 处理消息的系统名 */
    private String eventTarget = Y9OrgEventConst.EVENT_TARGET_ALL;

    /** 租户id */
    private String tenantId;

    public Y9MessageOrg(T orgObj, String eventType, String tenantId) {
        this.orgObj = orgObj;
        this.eventType = eventType;
        this.tenantId = tenantId;
    }
}