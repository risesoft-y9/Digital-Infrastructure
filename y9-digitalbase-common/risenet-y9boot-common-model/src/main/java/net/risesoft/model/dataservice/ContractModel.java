package net.risesoft.model.dataservice;

import java.io.Serializable;

import lombok.Data;

/**
 * 合同管理
 */
@Data
public class ContractModel implements Serializable {

    private static final long serialVersionUID = 6415611816488109803L;

    // 主键id
    private String id;

    private String type;// 类别

    private String join;// 加入联盟

    private String companyName;// 公司名称

    private String products;// 主营业务及产品

    private String linkman;// 联系人

    private String duty;// 职务

    private String mobile;// 联系方式

    private String time;// 合作时间

    private String number;// 合同编号

    private String projects;// 合作项目

    private String form;// 合作形式

    private String procurement;// 采购内容

    private String remark;// 合作备忘

    private String market;// 市场机会

    private String createDate;// 创建时间

}
