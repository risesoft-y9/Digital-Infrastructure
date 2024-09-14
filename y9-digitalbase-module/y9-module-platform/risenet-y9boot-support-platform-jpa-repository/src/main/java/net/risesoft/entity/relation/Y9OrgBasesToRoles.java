package net.risesoft.entity.relation;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.persistence.EnumConverter;

/**
 * 组织节点与角色关联表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_ORGBASES_ROLES", indexes = {@Index(columnList = "ROLE_ID,ORG_ID")})
@DynamicUpdate
@Comment("组织节点与角色关联表")
@NoArgsConstructor
@Data
public class Y9OrgBasesToRoles extends BaseEntity {

    private static final long serialVersionUID = 3967117431373531659L;
    /** 组织类型 */
    @Column(name = "ORG_TYPE", length = 255, nullable = false)
    @Comment("组织类型")
    @Convert(converter = EnumConverter.OrgTypeEnumConverter.class)
    protected OrgTypeEnum orgType;
    /** 主键 */
    @Id
    @Column(name = "ID")
    @Comment("主键")
    private String id;
    /** 角色id */
    @Column(name = "ROLE_ID", length = 38, nullable = false)
    @Comment("角色id")
    private String roleId;
    /** 人员或部门组织机构等唯一标识 */
    @Column(name = "ORG_ID", length = 38, nullable = false)
    @Comment("人员或部门组织机构等唯一标识")
    private String orgId;
    /** 父节点唯一标识 */
    @Column(name = "ORG_PARENT_ID")
    @Comment("父节点唯一标识")
    private String parentId;

    /** 关联排序号 */
    @Column(name = "ORG_ORDER", nullable = false)
    @Comment("关联排序号")
    private Integer orgOrder;

    /** 是否为负角色关联 */
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Column(name = "NEGATIVE", nullable = false)
    @Comment("是否为负角色关联")
    @ColumnDefault("0")
    private Boolean negative = Boolean.FALSE;

}