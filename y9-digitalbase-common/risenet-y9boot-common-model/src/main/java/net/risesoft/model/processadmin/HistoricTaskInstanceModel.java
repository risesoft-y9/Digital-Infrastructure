package net.risesoft.model.processadmin;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class HistoricTaskInstanceModel implements Serializable {

    private static final long serialVersionUID = 5536307402841240381L;

    private String id;

    private String name;

    private String description;

    private String owner;

    private String assignee;

    private String processInstanceId;

    private String executionId;

    private String processDefinitionId;

    private String taskDefinitionKey;

    private Date dueDate;

    private String parentTaskId;

    private Date claimTime;

    private String deleteReason;

    private Date startTime;

    private Date endTime;

    private String tenantId;

    // private Map<String, Object> TaskLocalVariables;

    // private Map<String, Object> ProcessVariables;

}
