package net.risesoft.pojo;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuditLogEvent implements Serializable {

    private static final long serialVersionUID = -2637942737271839008L;

    /**
     * 操作类型
     */
    private String action;

    /**
     * 操作简要描述
     */
    private String description;

    /**
     * 操作对象id
     */
    private String objectId;

    /**
     * 原始对象
     */
    private Object oldObject;

    /**
     * 当前对象
     */
    private Object currentObject;
}
