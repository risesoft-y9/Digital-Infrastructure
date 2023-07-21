package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

@Data
public class ChaoSongModel implements Serializable {

    private static final long serialVersionUID = -4235779237483037821L;

    /**
     * 主键
     */
    private String id;

    /**
     * 租户Id
     */
    private String tenantId;

    /**
     * 抄送的标题
     */
    private String title;

    /**
     * 抄送的流程实例
     */
    private String processInstanceId;

    /**
     * 抄送目标人员名称
     */
    private String userName;

    /**
     * 抄送目标人员Id
     */
    private String userId;

    /**
     * 抄送目标人员部门名称
     */
    private String userDeptName;

    /**
     * 抄送目标人员部门Id
     */
    private String userDeptId;

    /**
     * 操作人的名称
     */
    private String senderName;

    /**
     * 操作人的Id
     */
    private String senderId;

    /**
     * 操作人员部门名称
     */
    private String sendDeptName;

    /**
     * 操作人员部门Id
     */
    private String sendDeptId;

    /**
     * 传阅的状态,0未阅,1已阅,2新件
     */
    private Integer status = 0;

    /**
     * 抄送时间
     */
    private String createTime;

    /**
     * 抄送时间
     */
    private String readTime;

    /**
     * 事项id
     */
    private String itemId;

    /**
     * 系统名称
     */
    private String systemName;

    /**
     * 是否结束
     */
    private boolean ended;

}
