package net.risesoft.model.processadmin;

import java.io.Serializable;

import lombok.Data;

@Data
public class ExecutionModel implements Serializable {

    private static final long serialVersionUID = 2555371070475673717L;

    private String id;

    private boolean isSuspended;

    private boolean isEnded;

    private String activityId;

    private String processInstanceId;

    private String parentId;

    private String superExecutionId;

    private String rootProcessInstanceId;

    private String tenantId;

    private String name;

    private String description;

}
