package net.risesoft.model.dataservice;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * 账户统计表
 * 
 * @author mjh
 *
 */
@Data
public class StatisticalModel implements Serializable {

    private static final long serialVersionUID = 1573615103313201577L;

    // 主键id
    private String id;

    // 年份
    private String dataYear;

    // 月份
    private String dataMonth;

    // 统计时间
    private String addTime;

    // 年度累计收入
    private BigDecimal yearIncome;

    // 年度累计支出
    private BigDecimal yearPay;

    // 年度借款借出
    private BigDecimal yearLend;

    // 年度借款借入
    private BigDecimal yearBorrow;

    // 账面余额
    private BigDecimal balance;

    // 本季度预计收入
    private String expectIncome;

    // 本季度预计支出
    private String expectPay;

    // 团队Id
    private String teamId;

}
