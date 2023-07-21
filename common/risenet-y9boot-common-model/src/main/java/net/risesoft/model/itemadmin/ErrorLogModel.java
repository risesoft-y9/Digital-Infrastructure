package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 错误日志信息
 *
 * @author qinman
 * @date 2022/12/29
 */
@Data
public class ErrorLogModel implements Serializable {

    private static final long serialVersionUID = -4465213360378742347L;

    /**
     * 错误类型：任务相关
     */
    public static final String ERROR_TASK = "taskError";

    /**
     * 错误类型：流程相关
     */
    public static final String ERROR_PROCESS_INSTANCE = "processInstanceError";

    /**
     * 错误标识：任务发送
     */
    public static final String ERROR_FLAG_FORWRDING = "forwarding";

    /**
     * 错误标识：流程办结
     */
    public static final String ERROR_FLAG_PROCESS_COMLETE = "processComlete";

    /**
     * 错误标识：恢复待办
     */
    public static final String ERROR_FLAG_RECOVERY_COMLETED = "recoveryCompleted";

    /**
     * 错误标识：办结截转至数据中心
     */
    public static final String ERROR_FLAG_SAVE_OFFICE_DONE = "saveOfficeDone";

    /**
     * 错误标识：保存统一待办
     */
    public static final String ERROR_FLAG_SAVE_TODO_TASK = "saveTodoTask";

    /**
     * 错误标识：删除统一待办
     */
    public static final String ERROR_FLAG_DELETE_TODO = "deleteTodo";

    /**
     * 错误标识：删除年度数据
     */
    public static final String ERROR_FLAG_DELETE_YEARDATA = "deleteYearData";

    /**
     * 错误标识：抄送保存
     */
    public static final String ERROR_FLAG_SAVE_CHAOSONG = "saveChaoSong";

    /**
     * @FieldCommit(value = "主键")
     */
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
     * @FieldCommit(value = "错误类型")
     */
    private String errorType;

    /**
     * @FieldCommit(value = "错误标识")
     */
    private String errorFlag;

    /**
     * @FieldCommit(value = "扩展字段")
     */
    private String extendField;

    /**
     * @FieldCommit(value = "错误日志信息")
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
