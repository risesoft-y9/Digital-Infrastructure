package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

@Data
public class OpinionHistoryModel implements Serializable {

    private static final long serialVersionUID = 3543969481530895802L;

    /**
     * 唯一标示
     */
    private String id;

    /**
     * 租户Id
     */
    private String tenantId;

    /**
     * 意见框Id
     */
    private String opinionFrameMark;

    /**
     * 流程系列号
     */
    private String processSerialNumber;

    /**
     * 流程实例Id
     */
    private String processInstanceId;

    /**
     * 任务实例Id
     */
    private String taskId;

    /**
     * 意见内容
     */
    private String content;

    /**
     * 填写意见人员id
     */
    private String userId;

    /**
     * 填写意见的人员名称
     */
    private String userName;

    /**
     * 填写意见部门id
     */
    private String deptId;

    /**
     * 填写意见的部门名称
     */
    private String deptName;

    private String agentUserId; // 意见代录人Guid

    private String agentUserName;// 意见代录人姓名

    private String agentUserDeptId;// 代录人的部门Id

    private String agentUserDeptName;// 代录人的部门Name

    private Integer isAgent = 0; // 是否是代录意见， 0=不是；1=是

    /**
     * 意见生成时间
     */
    private String createDate;

    /**
     * 意见最后的修改时间
     */
    private String modifyDate;

    /**
     * 操作、保存时间
     */
    private String saveDate;

    /**
     * 意见类型，1为修改，2为删除
     */
    private String opinionType;
}
