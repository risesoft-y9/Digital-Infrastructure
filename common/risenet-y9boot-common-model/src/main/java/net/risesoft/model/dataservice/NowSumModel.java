package net.risesoft.model.dataservice;

import java.io.Serializable;

import lombok.Data;

/**
 * 累计
 * 
 * @author whz
 *
 */
@Data
public class NowSumModel implements Serializable {

    private static final long serialVersionUID = 2989501317771910652L;

    /**
     * 主键id
     */
    private String nowSumId;

    /**
     * 年份
     */
    private String year;

    /**
     * 月份
     */
    private String month;

    /**
     * 日期
     */
    private String day;

    /**
     * 摘要
     */
    private String remark;

    /**
     * 借
     */
    private String debit;

    /**
     * 贷
     */
    private String credit;

    /**
     * 方向
     */
    private String dirct;

    /**
     * 余额
     */
    private String balance;

    /**
     * 账户
     */
    private OrganizationModel organization;

}
