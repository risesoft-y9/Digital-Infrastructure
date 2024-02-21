package net.risesoft.model.processadmin;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import lombok.Data;

@Data
public class HistoricProcessInstanceModel implements Serializable {

    private static final long serialVersionUID = 1809384368949513536L;

    private String Id;

    private String processDefinitionId;

    private String processDefinitionName;

    private String processDefinitionKey;

    private Integer processDefinitionVersion;

    private String deploymentId;

    private Date startTime;

    private Date endTime;

    private Long durationInMillis;

    private String endActivityId;

    private String startUserId;

    private String startActivityId;

    private String deleteReason;

    private String superProcessInstanceId;

    private String name;

    private String description;

    private Map<String, Object> variables;

}
