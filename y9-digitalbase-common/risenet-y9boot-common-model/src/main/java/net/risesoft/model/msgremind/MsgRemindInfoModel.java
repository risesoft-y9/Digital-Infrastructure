package net.risesoft.model.msgremind;

import java.io.Serializable;

import lombok.Data;

/**
 * 消息提醒信息
 *
 * @author 10858
 *
 */
@Data
public class MsgRemindInfoModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3605772934936527497L;

    /** 消息类型,流程办结 */
    public static final String MSG_TYPE_PROCESS_COMPLETE = "y9_processComplete";

    /** 消息类型,任务完成 */
    public static final String MSG_TYPE_TASK_COMPLETE = "y9_taskComplete";

    /** 消息类型,节点到达 */
    public static final String MSG_TYPE_NODE_ARRIVE = "y9_nodeArrive";

    /** 消息类型,节点完成 */
    public static final String MSG_TYPE_NODE_COMPLETE = "y9_nodeComplete";

    /** 消息类型,意见填写 */
    public static final String MSG_TYPE_OPINION = "y9_opinion";

    /**
     * 主键
     */
    private String id;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 事项id
     */
    private String itemId;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 消息类型
     */
    private String msgType;

    /**
     * 系统中文名称
     */
    private String systemName;

    /**
     * 标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 参与人id
     */
    private String allUserId;

    /**
     * 已阅人id
     */
    private String readUserId;

    /**
     * 创建人姓名
     */
    private String userName;

    /**
     * 创建时间
     */
    private String startTime;

    /**
     * 创建时间,毫秒
     */
    private long time;

    /**
     * 详情链接
     */
    private String url;

}
