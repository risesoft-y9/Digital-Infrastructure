package net.risesoft.y9.pubsub.event;

import org.springframework.context.ApplicationEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 组织事件
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Getter
@Setter
@EqualsAndHashCode
public class Y9EventOrg extends ApplicationEvent {
    private static final long serialVersionUID = 5693234105948292780L;

    private Object orgObj;
    private String eventType;
    private String tenantId;

    public Y9EventOrg() {
        super("event source");
    }

    public Y9EventOrg(Object orgObj, String eventType, String tenantId) {
        super("event source");
        this.orgObj = orgObj;
        this.eventType = eventType;
        this.tenantId = tenantId;
    }
}