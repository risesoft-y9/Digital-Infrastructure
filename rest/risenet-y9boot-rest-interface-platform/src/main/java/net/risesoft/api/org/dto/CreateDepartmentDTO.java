package net.risesoft.api.org.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

/**
 * @author shidaobang
 * @date 2023/11/03
 * @since 9.6.3
 */
@Getter
@Setter
public class CreateDepartmentDTO extends CreateOrgUnitBaseDTO {

    private static final long serialVersionUID = -6127966977769572837L;

    /** 父ID */
    @NotBlank
    private String parentId;

    /** 部门简称 */
    private String aliasName;

    /** 部门地址 */
    private String deptAddress;

    /** 传真号码 */
    private String deptFax;

    /** 特定名称 */
    private String deptGivenName;

    /** 办公室 */
    private String deptOffice;

    /** 电话号码 */
    private String deptPhone;

    /** 部门类型 */
    private String deptType;

    /** 区域代码 */
    private String divisionCode;

    /** 英文名称 */
    private String enName;

    /** 成立时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date establishDate;

    /** 等级编码 */
    private String gradeCode;

    /** 邮政编码 */
    private String zipCode;

    /** 部门类型名称 */
    private String deptTypeName;

    /** 等级名称 */
    private String gradeCodeName;

    /** 是否委办局 */
    private Boolean bureau;

}
