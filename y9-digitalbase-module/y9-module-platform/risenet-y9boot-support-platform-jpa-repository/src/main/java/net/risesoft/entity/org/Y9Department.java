package net.risesoft.entity.org;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.consts.DefaultConsts;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.org.Department;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.util.Y9BeanUtil;

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
@Comment("部门实体表")
@Data
@NoArgsConstructor
public class Y9Department extends Y9OrgBase {

    private static final long serialVersionUID = 231356577350213851L;

    {
        super.setOrgType(OrgTypeEnum.DEPARTMENT);
    }

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
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Column(name = "BUREAU", nullable = false)
    @Comment("是否委办局")
    @ColumnDefault("0")
    private Boolean bureau = false;

    public Y9Department(Department department, Y9OrgBase parent, Integer nextSubTabIndex) {
        Y9BeanUtil.copyProperties(department, this);

        if (StringUtils.isBlank(this.id)) {
            this.id = Y9IdGenerator.genId(IdType.SNOWFLAKE);
        }
        if (DefaultConsts.TAB_INDEX.equals(this.tabIndex)) {
            this.tabIndex = nextSubTabIndex;
        }
        this.parentId = parent.getId();
        this.dn = Y9OrgUtil.buildDn(OrgTypeEnum.DEPARTMENT, this.name, parent.getDn());
        this.guidPath = Y9OrgUtil.buildGuidPath(parent.getGuidPath(), this.id);
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }

    public void changeParent(Y9OrgBase parent, Integer nextSubTabIndex) {
        if (parent.getGuidPath().contains(this.id)) {
            // 判断是否移动到自己，或者自己的子节点里面，这种情况要排除，不让移动
            throw new Y9BusinessException(OrgUnitErrorCodeEnum.MOVE_TO_SUB_DEPARTMENT_NOT_PERMITTED.getCode(),
                OrgUnitErrorCodeEnum.MOVE_TO_SUB_DEPARTMENT_NOT_PERMITTED.getDescription());
        }
        this.parentId = parent.getId();
        this.tabIndex = nextSubTabIndex;
        this.dn = Y9OrgUtil.buildDn(OrgTypeEnum.DEPARTMENT, this.name, parent.getDn());
        this.guidPath = Y9OrgUtil.buildGuidPath(parent.getGuidPath(), this.id);
    }

    public void update(Department department, Y9OrgBase parent) {
        Y9BeanUtil.copyProperties(department, this);

        this.dn = Y9OrgUtil.buildDn(OrgTypeEnum.DEPARTMENT, this.name, parent.getDn());
        this.guidPath = Y9OrgUtil.buildGuidPath(parent.getGuidPath(), this.id);
    }

    public void changeTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public void changeProperties(String properties) {
        this.properties = properties;
    }

    public Boolean changeDisabled(boolean isAllDescendantsDisabled) {
        boolean targetStatus = !this.disabled;
        if (targetStatus && !isAllDescendantsDisabled) {
            // 检查所有后代节点是否都禁用了，只有所有后代节点都禁用了，当前部门才能禁用
            throw Y9ExceptionUtil.businessException(OrgUnitErrorCodeEnum.NOT_ALL_DESCENDENTS_DISABLED);
        }
        this.disabled = targetStatus;
        return targetStatus;
    }
}