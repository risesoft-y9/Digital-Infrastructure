package net.risesoft.model.dataservice;

import java.io.Serializable;

import lombok.Data;

/**
 * 项目管理
 * 
 * @author whz
 *
 */
@Data
public class ProjectManageModel implements Serializable {

    private static final long serialVersionUID = 4481543568290507813L;

    // 主键id
    private String projectId;

    private String agreementNum;// 合同编号

    private String agreementName;// 合同名称

    private String assigeeName;// 签约方名称

    private String assignTime;// 签署时间

    private String projectMoney;// 项目金额

    private String payCondition;// 付款条件

    private String billingTime;// 开票时间

    private String billingMoney;// 开票金额

    private String remitTime;// 回款时间

    private String remitMoney;// 汇款金额

    private String projectPerson;// 项目负责人

    private String checkTime;// 验收时间

    private String attachment;// 附件

    private String remark;// 备注

    private String userName;// 添加人名称

    private String userID;// 添加人ID

    private String filePath;// 文件路径

}
