package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

@Data
public class TaskVariableModel implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1972054094376070120L;

    private String id;

    /**
     * @FieldCommit(value = "流程实例")
     */
    private String processInstanceId;

    /**
     * @FieldCommit(value = "任务id")
     */
    private String taskId;

    /**
     * @FieldCommit(value = "变量名称")
     */
    private String keyName;

    /**
     * @FieldCommit(value = "变量值")
     */
    private String text;

    /**
     * @FieldCommit(value="创建时间")
     */
    private String createTime;

    /**
     * @FieldCommit(value="更新时间")
     */
    private String updateTime;

}
