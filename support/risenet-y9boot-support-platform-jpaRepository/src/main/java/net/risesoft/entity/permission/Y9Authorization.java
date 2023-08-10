package net.risesoft.entity.permission;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;
import net.risesoft.enums.AuthorityEnum;
import net.risesoft.enums.AuthorizationPrincipalTypeEnum;

/**
 * 权限配置表：授权主体对Resource上执行的操作的配置。
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_AUTHORIZATION",
    indexes = {@Index(columnList = "PRINCIPAL_ID,RESOURCE_ID,AUTHORITY", unique = true),
        @Index(columnList = "TENANT_ID,PRINCIPAL_ID ASC", unique = false),
        @Index(columnList = "TENANT_ID,RESOURCE_ID ASC", unique = false)})
@Comment("权限配置表")
@NoArgsConstructor
@Data
public class Y9Authorization extends BaseEntity {

    private static final long serialVersionUID = 6269521087994988054L;

    /** 主键 */
    @Id
    @Column(name = "ID", length = 38)
    @Comment("主键")
    private String id;

    /** 租户id */
    @Column(name = "TENANT_ID", length = 38)
    @Comment("租户id")
    private String tenantId;

    /** 授权主体id */
    @Column(name = "PRINCIPAL_ID", length = 38, nullable = false)
    @Comment("授权主体的唯一标识")
    private String principalId;

    /**
     * 授权主体类型:0=角色；1=岗位；2=人员,3=组，4=部门，5=组织
     * 
     * {@link AuthorizationPrincipalTypeEnum}
     */
    @ColumnDefault("0")
    @Column(name = "PRINCIPAL_TYPE", nullable = false)
    @Comment("授权主体类型:0=角色；1=岗位；2=人员,3=组，4=部门，5=组织")
    private Integer principalType = AuthorizationPrincipalTypeEnum.ROLE.getValue();

    /** 授权主体的名称。冗余字段，纯显示用 */
    @Column(name = "PRINCIPAL_NAME", length = 255)
    @Comment("授权主体的名称。冗余字段，纯显示用")
    private String principalName;

    /** 资源id */
    @Column(name = "RESOURCE_ID", length = 38, nullable = false)
    @Comment("资源唯一标识符")
    private String resourceId;

    /**
     * 资源类型。冗余字段，纯显示用
     * 
     * {@link net.risesoft.enums.ResourceTypeEnum}
     */
    @Column(name = "RESOURCE_TYPE")
    @Comment("资源类型。冗余字段，纯显示用")
    private Integer resourceType;

    /** 资源名称。冗余字段，纯显示用 */
    @Column(name = "RESOURCE_NAME", length = 255)
    @Comment("资源名称。冗余字段，纯显示用")
    private String resourceName;

    /*@Type(type = "numeric_boolean")
    @Column(name = "INHERIT", nullable = false)
    @Comment("资源是否为继承上级节点的权限。冗余字段，纯显示用")
    private Boolean inherit;*/

    /**
     * 权限类型
     * 
     * {@link AuthorityEnum}
     */
    @Column(name = "AUTHORITY", nullable = false)
    @Comment("权限类型")
    private Integer authority;

    /** 授权人 */
    @Column(name = "AUTHORIZER", length = 30)
    @Comment("授权人")
    private String authorizer;

}
