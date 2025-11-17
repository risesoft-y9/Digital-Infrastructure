package net.risesoft.entity.org;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import net.risesoft.enums.platform.org.OrgTypeEnum;

/**
 * 部门实体
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_DEPARTMENT")
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "部门实体表", appliesTo = "Y9_ORG_DEPARTMENT")
@Data
@SuperBuilder
public class Y9Department extends Y9OrgBase {

    private static final long serialVersionUID = 231356577350213851L;

    /** 父节点id */
    @Column(name = "PARENT_ID", length = 38, nullable = false)
    @Comment("父节点id")
    private String parentId;

    /** 部门简称 */
    @Column(name = "ALIAS_NAME", length = 255)
    @Comment("部门简称")
    private String aliasName;

    /** 特定名称 */
    @Column(name = "DEPT_GIVEN_NAME", length = 255)
    @Comment("特定名称")
    private String deptGivenName;

    /** 英文名称 */
    @Column(name = "EN_NAME", length = 255)
    @Comment("英文名称")
    private String enName;

    /** 区域代码 */
    @Column(name = "DIVISION_SCODE", length = 255)
    @Comment("区域代码")
    private String divisionCode;

    /** 等级编码 */
    @Column(name = "GRADE_CODE", length = 255)
    @Comment("等级编码")
    private String gradeCode;

    /** 等级名称 */
    @Column(name = "GRADE_CODE_NAME", length = 255)
    @Comment("等级名称")
    private String gradeCodeName;

    /** 部门类型 */
    @Column(name = "DEPT_TYPE", length = 255)
    @Comment("部门类型")
    private String deptType;

    /** 部门类型名称 */
    @Column(name = "DEPT_TYPE_NAME", length = 255)
    @Comment("部门类型名称")
    private String deptTypeName;

    /** 部门地址 */
    @Column(name = "DEPT_ADDRESS", length = 255)
    @Comment("部门地址")
    private String deptAddress;

    /** 办公室 */
    @Column(name = "DEPT_OFFICE", length = 255)
    @Comment("办公室")
    private String deptOffice;

    /** 传真号码 */
    @Column(name = "DEPT_FAX", length = 255)
    @Comment("传真号码")
    private String deptFax;

    /** 电话号码 */
    @Column(name = "DEPT_PHONE", length = 255)
    @Comment("电话号码")
    private String deptPhone;

    /** 邮政编码 */
    @Column(name = "ZIP_CODE", length = 255)
    @Comment("邮政编码")
    private String zipCode;

    /** 成立时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.DATE)
    @Column(name = "ESTABLISH_DATE")
    @Comment("成立时间")
    private Date establishDate;

    /** 是否委办局 */
    @Type(type = "numeric_boolean")
    @Column(name = "BUREAU", nullable = false)
    @Comment("是否委办局")
    @ColumnDefault("0")
    @Builder.Default
    private Boolean bureau = false;

    public Y9Department() {
        super.setOrgType(OrgTypeEnum.DEPARTMENT);
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }
}