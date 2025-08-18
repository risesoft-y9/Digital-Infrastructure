package net.risesoft.model.platform.org;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 部门
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
public class Department extends OrgUnit {

    private static final long serialVersionUID = 1095290600377937606L;

    /**
     * 部门简称
     */
    private String aliasName;

    /**
     * 特定名称
     */
    private String deptGivenName;

    /**
     * 英文名称
     */
    private String enName;

    /**
     * 等级编码
     */
    private String gradeCode;

    /**
     * 区域代码
     */
    private String divisionCode;

    /**
     * 部门地址
     */
    private String deptAddress;

    /**
     * 办公室
     */
    private String deptOffice;

    /**
     * 传真号码
     */
    private String deptFax;

    /**
     * 电话号码
     */
    private String deptPhone;

    /**
     * 邮政编码
     */
    private String zipCode;

    /**
     * 成立时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date establishDate;

    /**
     * 是否委办局
     */
    private boolean bureau = false;

}