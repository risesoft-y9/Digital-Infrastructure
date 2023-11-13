package net.risesoft.entity.relation;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

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
@org.hibernate.annotations.Table(comment = "组织节点与角色关联表", appliesTo = "Y9_ORG_ORGBASES_ROLES")
@NoArgsConstructor
@Data
public class Y9OrgBasesToRoles extends BaseEntity {

    private static final long serialVersionUID = 3967117431373531659L;

    /** 主键 */
    @Id
    @GenericGenerator(name = "OrgBasesToRoles", strategy = "native")
    @GeneratedValue(generator = "OrgBasesToRoles")
    @Column(name = "ID")
    @Comment("主键")
    private Integer id;

    /** 角色id */
    @Column(name = "ROLE_ID", length = 38, nullable = false)
    @Comment("角色id")
    private String roleId;

    /** 人员或部门组织机构等唯一标识 */
    @Column(name = "ORG_ID", length = 38, nullable = false)
    @Comment("人员或部门组织机构等唯一标识")
    private String orgId;

    /**
     * 组织类型
     * 
     * {@link OrgTypeEnum}
     */
    @Column(name = "ORG_TYPE", length = 255)
    @Comment("组织类型")
    @Convert(converter = EnumConverter.OrgTypeEnumConverter.class)
    protected OrgTypeEnum orgType;

    /** 父节点唯一标识 */
    @Column(name = "ORG_PARENT_ID")
    @Comment("父节点唯一标识")
    private String parentId;

    /** 关联排序号 */
    @Column(name = "ORG_ORDER", nullable = false)
    @Comment("关联排序号")
    private Integer orgOrder;

    /** 是否为负角色关联 */
    @Type(type = "numeric_boolean")
    @Column(name = "NEGATIVE", nullable = false)
    @Comment("是否为负角色关联")
    @ColumnDefault("0")
    private Boolean negative = Boolean.FALSE;

}