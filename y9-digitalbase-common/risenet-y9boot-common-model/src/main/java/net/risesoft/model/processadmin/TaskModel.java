package net.risesoft.model.processadmin;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.flowable.task.api.DelegationState;

import lombok.Data;

@Data
public class TaskModel implements Serializable {

    private static final long serialVersionUID = -8531935784821978703L;

    private String Id;

    private String Name;

    private String Description;

    private int priority;

    private String Owner;

    private String Assignee;

    private String ProcessInstanceId;

    private String ExecutionId;

    private String ProcessDefinitionId;

    private Date CreateTime;

    private String TaskDefinitionKey;

    private String formKey;// 用于存放待办件已阅，未阅状态

    private Date DueDate;

    private Date ClaimTime;

    private DelegationState delegationState;

    private Map<String, Object> localVariables;

    private Map<String, Object> variables;

}
