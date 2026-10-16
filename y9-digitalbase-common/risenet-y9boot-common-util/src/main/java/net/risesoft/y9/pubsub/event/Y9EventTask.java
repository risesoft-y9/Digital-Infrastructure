package net.risesoft.y9.pubsub.event;

import java.io.Serializable;

import org.springframework.context.ApplicationEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 任务事件
 * 
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

    /** 任务事件类型 */
    public static final String TASK = "TASK";
    /** 任务事件名称：创建 */
    public static final String TASKEVENTNAME_CREATE = "create";
    /** 任务事件名称：分配 */
    public static final String TASKEVENTNAME_ASSIGNMENT = "assignment";
    /** 任务事件名称：完成 */
    public static final String TASKEVENTNAME_COMPLETE = "complete";
    /** 任务事件名称：删除 */
    public static final String TASKEVENTNAME_DELETE = "delete";

    /** 任务对象 */
    private Object taskObj;
    /** 事件类型 */
    private String eventType;
    /** 租户id */
    private String tenantId;
    /** 人员id */
    private String personId;

    /**
     * 构造任务事件
     */
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