package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProcessTrackModel implements Serializable {

    private static final long serialVersionUID = -7349004792256172372L;

    /**
     * 主键
     */
    private String id;

    /**
     * 流程实例Id
     */
    private String processInstanceId;

    /**
     * 任务节点Id
     */
    private String taskId;

    /**
     * 发送人/操作人
     */
    private String senderName;

    /**
     * 接收人
     */
    private String receiverName;

    /**
     * 任务节点名称
     */
    private String taskDefName;

    /**
     * 是否有抄送
     */
    private Boolean isChaoSong;

    /**
     * 意见
     */
    private String opinion;

    /**
     * 正文版本
     */
    private Integer docVersion;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 办理用时
     */
    private String handlingTime;

    /**
     * 描述
     */
    private String described;

}
