package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

@Data
public class RemindInstanceModel implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6170620339097452197L;

    public static String processComplete = "processComplete";// 流程办结提醒

    public static String taskComplete = "taskComplete";// 任务完成提醒

    public static String nodeArrive = "nodeArrive";// 节点到达提醒

    public static String nodeComplete = "nodeComplete";// 节点完成提醒

    private String id;

    /**
     * @FieldCommit(value = "租户Id")
     */
    private String tenantId;

    /**
     * @FieldCommit(value = "消息提醒类型")
     */
    private String remindType;

    /**
     * @FieldCommit(value = "流程实例id")
     */
    private String processInstanceId;

    /**
     * @FieldCommit(value = "任务id")
     */
    private String taskId;

    /**
     * @FieldCommit(value = "节点到达任务key")
     */
    private String arriveTaskKey;

    /**
     * @FieldCommit(value = "节点完成任务key")
     */
    private String completeTaskKey;

    /**
     * @FieldCommit(value = "人员id")
     */
    private String userId;

    /**
     * @FieldCommit(value = "创建时间")
     */
    private String createTime;
}
