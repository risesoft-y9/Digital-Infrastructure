package net.risesoft.model.processadmin;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class HistoricActivityInstanceModel implements Serializable {

    private static final long serialVersionUID = -264972095894876276L;

    private String Id;

    private String activityId;

    private String activityName;

    private String activityType;

    private String processDefinitionId;

    private String processInstanceId;

    private String executionId;

    private String taskId;

    private String calledProcessInstanceId;

    private String assignee;

    private Date startTime;

    private Date endTime;

    private Long durationInMillis;

    private String deleteReason;

    private String tenantId;

}
