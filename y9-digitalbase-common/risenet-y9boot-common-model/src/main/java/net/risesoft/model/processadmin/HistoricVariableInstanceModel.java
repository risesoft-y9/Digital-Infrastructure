package net.risesoft.model.processadmin;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class HistoricVariableInstanceModel implements Serializable {

    private static final long serialVersionUID = 2764777788475295445L;

    private String id;

    private String variableName;

    private String variableTypeName;

    private Object value;

    private String processInstanceId;

    private String taskId;

    private Date createTime;

    private Date lastUpdatedTime;

}
