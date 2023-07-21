package net.risesoft.model.processadmin;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import lombok.Data;

@Data
public class ProcessInstanceModel implements Serializable {

    private static final long serialVersionUID = 6326703125881185732L;

    private String Id;

    private boolean suspended;

    private boolean ended;

    private String name;

    private String description;

    private String processDefinitionId;

    private String processDefinitionName;

    private String processDefinitionKey;

    private Integer processDefinitionVersion;

    private String deploymentId;

    private Date startTime;

    private String startUserId;

    private Map<String, Object> variables;

}
