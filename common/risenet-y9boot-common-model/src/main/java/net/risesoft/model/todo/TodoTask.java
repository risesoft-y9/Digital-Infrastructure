package net.risesoft.model.todo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 代办任务基本信息表
 */
@Data
public class TodoTask implements Serializable {
    private static final long serialVersionUID = 5847600637362741741L;

    /**
     * 主键，唯一标识
     */
    private String id;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 消息信息标题
     */
    private String title;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 任务Id
     */
    private String taskId;

    /**
     * 消息内容正文
     */
    private String content;

    /**
     * 消息来源的系统名称
     */
    private String systemName;

    /**
     * 消息来源的系统中文名称
     */
    private String systemCnName;

    /**
     * 消息来源的应用名称
     */
    private String appName;

    /**
     * 消息来源的应用中文名称
     */
    private String appCnName;

    /**
     * 消息关联业务数据的唯一标识
     */
    private String bussinessId;

    /**
     * 打开查看消息时调用的URL
     */
    private String url;

    /**
     * 紧急程度，多用于消息信息显示、提示处理。通常定义为1到5级，分别对应于5:非常紧急、4:紧急、3:一般、2:不太紧急、1:不紧急。各业务系统可扩展。
     */
    private Integer emergency = 1;

    /**
     * 是否邮件提醒
     */
    private boolean emailAble = false;

    /**
     * 是否手机短信提醒
     */

    private boolean mobileAble = false;

    /**
     * 消息发送人UID
     */
    private String senderId;

    /**
     * 消息发送人姓名
     */
    private String senderName;

    /**
     * 消息发送人所属部门UID
     */
    private String senderDepartmentId;

    /**
     * 消息发送人所属部门名称
     */
    private String senderDepartmentName;

    /**
     * 发送时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;

    /**
     * 消息接收方UID
     */
    private String receiverId;

    /**
     * 消息接收方姓名，用于显示。
     */
    private String receiverName;

    /**
     * 消息发送人所属部门UID
     */
    private String receiverDepartmentId;

    /**
     * 消息发送人所属部门名称
     */
    private String receiverDepartmentName;

    /**
     * 任务是否是新的，没有被查看过
     */
    private String isNewTodo;

    // 紧急程度：0平件，1平急，2加急，3特急，4特提
    private String urgency;

    // 文件编号
    private String docNumber;

}
