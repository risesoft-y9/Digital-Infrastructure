package net.risesoft.y9.pubsub.event;

import java.io.Serializable;

import org.springframework.context.ApplicationEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 任务事件
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Getter
@Setter
@EqualsAndHashCode
public class Y9EventTask extends ApplicationEvent {
    private static final long serialVersionUID = 5693234105948292780L;

    public static final String TASK = "TASK";
    public static final String TASKEVENTNAME_CREATE = "create";
    public static final String TASKEVENTNAME_ASSIGNMENT = "assignment";
    public static final String TASKEVENTNAME_COMPLETE = "complete";
    public static final String TASKEVENTNAME_DELETE = "delete";

    private Object taskObj;
    private String eventType;
    private String tenantId;
    private String personId;

    public Y9EventTask() {
        super("event source");
    }

    public Y9EventTask(Object source, Serializable taskObj, String eventType, String tenantId, String personId) {
        super("event source");
        this.taskObj = taskObj;
        this.eventType = eventType;
        this.tenantId = tenantId;
        this.personId = personId;
    }

}