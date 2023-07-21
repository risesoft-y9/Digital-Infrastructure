package net.risesoft.model.dataservice;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 账户
 * 
 * @author whz
 *
 */
@Data
public class OrganizationModel implements Serializable {

    private static final long serialVersionUID = -4451230769598211715L;

    /**
     * 主键id
     */
    private String orgId;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 组织负责人
     */
    private String orgLeader;

    /**
     * 组织现有员工数
     */
    private Integer orgCountEmployee;

    /**
     * 是否是分公司
     */
    private Boolean orgIsBranch;

    /**
     * 是否是平台
     */
    private Boolean orgIsPlat;

    private List<AccumulateModel> accumulate;

    private List<BankNoteModel> banknote;

    private List<DaySumModel> daysum;

    private List<MonthSumModel> monthsum;

    private List<NowSumModel> nowsum;

    private List<TotalModel> total;

    private List<CreditModel> credit;

    private List<DebitModel> debit;

}