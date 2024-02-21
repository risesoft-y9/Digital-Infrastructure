package net.risesoft.model.dataservice;

import java.io.Serializable;

import lombok.Data;

/**
 * 贷款
 * 
 *
 */
@Data
public class CreditModel implements Serializable {

    private static final long serialVersionUID = 1990230797967053186L;

    // 主键id
    private String creditId;

    // 年份
    private String year;

    // 月份
    private String month;

    // 日期
    private String day;

    // 凭证号数
    private String docNum;

    // 摘要
    private String remark;

    // 结算号
    private String setNum;

    // 对方科目
    private String oppNum;

    // 借
    private String debit;

    // 贷
    private String credit;

    // 方向
    private String dirct;

    // 余额
    private String balance;

    // 账户
    private OrganizationModel organization;

}
