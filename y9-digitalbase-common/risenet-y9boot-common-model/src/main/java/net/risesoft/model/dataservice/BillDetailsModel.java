package net.risesoft.model.dataservice;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * 收支明细表
 *
 */
@Data
public class BillDetailsModel implements Serializable {

    private static final long serialVersionUID = -2267559754570588745L;

    // 主键id
    private String id;

    // 年
    private String dataYear;

    // 月
    private String dataMonth;

    // 日
    private String dataDay;

    // 统计时间
    private String addTime;

    // 交易名称
    private String transName;

    // 交易金额
    private BigDecimal transMoney;

    // 交易类型，Min-日常收入，Mout-日常支出，Bin-借入款，Bout-归还借款，Lin-收到借出的还款，Lout-借出款
    private String transType;

    // 类型，0-支出，1-收入
    private String billType;

    // 团队Id
    private String teamId;

}
