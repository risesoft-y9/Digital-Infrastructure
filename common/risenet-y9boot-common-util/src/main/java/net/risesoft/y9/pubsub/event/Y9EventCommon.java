package net.risesoft.y9.pubsub.event;

import org.springframework.context.ApplicationEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 通用事件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Getter
@Setter
@EqualsAndHashCode
public class Y9EventCommon extends ApplicationEvent {
    private static final long serialVersionUID = 6197864557550039984L;

    /** 系统名，针对只需要特定系统接收消息的情况 */
    private String target;
    /** 事件内容 */
    private Object eventObject;
    /** 事件类型 */
    private String eventType;

    public Y9EventCommon() {
        super("event source");
    }

    public Y9EventCommon(Object eventObject, String eventType) {
        super("event source");
        this.eventObject = eventObject;
        this.eventType = eventType;
    }

}