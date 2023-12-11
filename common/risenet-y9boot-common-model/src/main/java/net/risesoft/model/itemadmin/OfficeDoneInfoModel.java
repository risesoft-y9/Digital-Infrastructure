package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 办结信息
 *
 * @author Think
 *
 */
@Data
public class OfficeDoneInfoModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4796601122130990872L;

    /**
     * 主键
     */
    private String id;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 流程定义id
     */
    private String processDefinitionId;

    /**
     * 流程定义key
     */
    private String processDefinitionKey;

    /**
     * 流程编号
     */
    private String processSerialNumber;

    /**
     * 系统英文名称
     */
    private String systemName;

    /**
     * 系统中文名称
     */
    private String systemCnName;

    /**
     * 事项id
     */
    private String itemId;

    /**
     * 事项名称
     */
    private String itemName;

    /**
     * 标题
     */
    private String title;

    /**
     * 文号
     */
    private String docNumber;

    /**
     * 紧急程度
     */
    private String urgency;

    /**
     * 创建人Id
     */
    private String creatUserId;

    /**
     * 创建人姓名
     */
    private String creatUserName;

    /**
     * 承办人Id
     */
    private String allUserId;

    /**
     * 委托人Id，用于委托办结件查询
     */
    private String entrustUserId;

    /**
     * 科室id
     */
    private String deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 委办局id
     */
    private String bureauId;

    /**
     * 创建时间
     */
    private String startTime;

    /**
     * 办结时间
     */
    private String endTime;

    /**
     * 办结人
     */
    private String userComplete;

    /**
     * 是否上会，1为上会,当代研究所使用
     */
    private String meeting = "0";

    /**
     * 会议类型，党组会，办公会，专题会,当代研究所使用
     */
    private String meetingType;
}
