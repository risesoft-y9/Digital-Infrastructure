package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class ActRuDetailModel implements Serializable {

    private static final long serialVersionUID = 3387901751275051825L;

    public static final Integer TODO = 0;

    public static final Integer DOING = 1;

    /**
     * 唯一标示
     */
    private String id;

    /**
     * 事项Id
     */
    private String itemId;

    /**
     * 流程序列号
     */
    private String processSerialNumber;

    /**
     * 流程实例
     */
    private String processInstanceId;

    /**
     * 任务Id
     */
    private String taskId;

    /**
     * 所属事项绑定的流程定义
     */
    private String processDefinitionKey;

    /**
     * 流程启动时间
     */
    private String startTime;

    /**
     * 所属事项的系统英文名称
     */
    private String systemName;

    /** 1是在办、0是待办 */
    private Integer status;

    /**
     * 办理人Id
     */
    private String assignee;

    /**
     * 办理部门
     */
    private String deptId;

    /**
     * 办理部门名称
     */
    private String deptName;

    /**
     * 办理部门所在委办局
     */
    private String bureauId;

    /**
     * 办理部门所在委办局
     */
    private String bureauName;

    /**
     * 办理人姓名
     */
    private String assigneeName;

    /**
     * 生成的时间
     */
    private Date createTime;

    /**
     * 最后一次的办理时间
     */
    private Date lastTime;

    /**
     * 是否启动流程
     */
    private boolean started;

    /**
     * 流程是否办结
     */
    private boolean ended;

    /**
     * 是否删除
     */
    private boolean deleted;

    /**
     * 是否归档
     */
    private boolean placeOnFile;
}
