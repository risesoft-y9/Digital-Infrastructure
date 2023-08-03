package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

@Data
public class CustomProcessInfoModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2620414732616268131L;

    private String id;

    // @FieldCommit(value="任务key")
    private String taskKey;

    // @FieldCommit(value="任务名称")
    private String taskName;

    // @FieldCommit(value="节点类型")
    private String taskType;

    // @FieldCommit(value="当前运行节点")
    private Boolean currentTask;

    // @FieldCommit(value="事项id")
    private String itemId;

    // @FieldCommit(value="流程编号")
    private String processSerialNumber;

    // @FieldCommit(value="办理人id")
    private String orgId;

    // @FieldCommit(value="排序号")
    private Integer tabIndex;
}
