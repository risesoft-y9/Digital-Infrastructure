package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

@Data
public class OpinionModel implements Serializable {

    private static final long serialVersionUID = 3543969481530895802L;

    /**
     * 唯一标示
     */
    private String id;

    /**
     * 租户Id,必填
     */
    private String tenantId;

    /**
     * 意见框Id,必填
     */
    private String opinionFrameMark;

    /**
     * 流程系列号,必填
     */
    private String processSerialNumber;

    /**
     * 流程实例Id,新建为空，不是新建传值
     */
    private String processInstanceId;

    /**
     * 任务实例Id,新建为空，不是新建传值
     */
    private String taskId;

    /**
     * 意见内容,必填
     */
    private String content;

    /**
     * 填写意见人员id,必填
     */
    private String userId;

    /**
     * 填写意见的人员名称,必填
     */
    private String userName;

    /**
     * 填写意见部门id,必填
     */
    private String deptId;

    /**
     * 填写意见的部门名称,必填
     */
    private String deptName;

    /**
     * 意见生成时间
     */
    private String createDate;

    /**
     * 意见最后的修改时间
     */
    private String modifyDate;

    /**
     * 自定义历程id
     */
    private String processTrackId;

    /**
     * 岗位id,必填
     */
    private String positionId;

    /**
     * 岗位名称
     */
    private String positionName;

    /**
     * 个人签名图片
     */
    private byte[] sign;
}
