package net.risesoft.model.datacenter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 办件信息
 *
 * @author Think
 *
 */
@Data
public class OfficeInfo implements Serializable {

    private static final long serialVersionUID = 8741280356901722546L;

    /**
     * 主键
     */
    private String id;

    /**
     * 流程实例id
     */
    private String processInstanceId;

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
     * 应用名称
     */
    private String appName;

    /**
     * 应用中文名称
     */
    private String appCnName;

    /**
     * 标题
     */
    private String title;

    /**
     * 文件编号，序列号
     */
    private String serialNumber;

    /**
     * 紧急程度
     */
    private String urgency;

    /**
     * 创建人姓名
     */
    private String creatUserName;

    /**
     * 创建部门名称
     */
    private String creatDeptName;

    /**
     * 承办人
     */
    private String undertaker;

    /**
     * 承办人名称
     */
    private String undertakerName;

    /**
     * 办结人姓名
     */
    private String completeUserName;

    /**
     * 归档人
     */
    private String filingMan;

    /**
     * 归档部门
     */
    private String filingDept;

    /**
     * 创建时间
     */
    private Date startTime;

    /**
     * 办结时间
     */
    private Date endTime;

    /**
     * 历程信息
     */
    private List<HistoryInfo> historys;

    /**
     * 表单信息
     */
    private List<EformInfo> eforms;

    /**
     * 正文url
     */
    private String textUrl;

    /**
     * 正文
     */
    private String text;

    /**
     * 安全级别
     */
    private String securityLevel;

    /**
     * 来文性质
     */
    private String docNature;

    /**
     * 附件
     */
    private List<AttachmentInfo> attachments;

    /**
     * 租户Id
     */
    private String tenantId;

    /**
     * 关联文件id
     */
    private String associatedId;

    /**
     * 信息公开 1-公开 0-不公开
     */
    private String disabled;

}
