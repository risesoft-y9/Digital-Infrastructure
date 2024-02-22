package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

@Data
public class WorkOrderModel implements Serializable {

    private static final long serialVersionUID = -2196986462380652871L;

    private String guid;//

    /**
     * 流程编号,与Y9表单一致，存放流程编号
     */
    private String processInstanceId;

    /**
     * 工作流实例id
     */
    private String realProcessInstanceId;

    /**
     * 编号
     */
    private String number;

    /**
     * 文件标题
     */
    private String title;

    /**
     * 工单类型
     */
    private String workOrderType;

    /**
     * 紧急程度
     */
    private String urgency;

    /**
     * 创建时间，排序用
     */
    private String createTime;

    /**
     * 创建日期
     */
    private String createDate;

    /**
     * 创建人Id
     */
    private String userId;

    /**
     * 创建人
     */
    private String userName;

    /**
     * 联系电话
     */
    private String mobile;

    /**
     * QQ
     */
    private String QQ;

    /**
     * 提交租户id
     */
    private String tenantId;

    /**
     * 提交租户名称
     */
    private String tenantName;

    /**
     * 纯文本描述
     */
    private String description;

    /**
     * 带html格式描述
     */
    private String htmlDescription;

    /**
     * 建议
     */
    private String suggest;

    /**
     * 结果反馈
     */
    private String resultFeedback;

    /**
     * 处理类型，0为草稿，1为未处理，2为处理中，3为已处理
     */
    private String handleType;

    /**
     * 处理人
     */
    private String handler;

    /**
     * 处理人联系方式
     */
    private String handlerMobile;

}
