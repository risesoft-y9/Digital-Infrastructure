package net.risesoft.model.processadmin;

import java.io.Serializable;

import lombok.Data;

@Data
public class IdentityLinkModel implements Serializable {

    private static final long serialVersionUID = 4650268040263457370L;

    private String type;

    private String userId;

    private String groupId;

    private String taskId;

    private String processInstanceId;

    private String processDefinitionId;

}
