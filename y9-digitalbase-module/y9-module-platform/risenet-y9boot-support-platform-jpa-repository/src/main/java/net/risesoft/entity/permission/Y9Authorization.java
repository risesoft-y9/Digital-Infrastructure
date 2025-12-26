package net.risesoft.entity.permission;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseTenantEntity;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.permission.AuthorizationPrincipalTypeEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.persistence.EnumConverter;

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
        @Index(name = "IDXEEFC34140C92B6E3361926E7", columnList = "PRINCIPAL_ID ASC", unique = false),
        @Index(name = "IDX29212FB15860130314C7AD85", columnList = "RESOURCE_ID ASC", unique = false)})
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "权限配置表", appliesTo = "Y9_ORG_AUTHORIZATION")
@NoArgsConstructor
@Data
public class Y9Authorization extends BaseTenantEntity {

    private static final long serialVersionUID = 6269521087994988054L;

    /** 主键 */
    @Id
    @Column(name = "ID", length = 38)
    @Comment("主键")
    private String id;

    /** 授权主体id */
    @Column(name = "PRINCIPAL_ID", length = 38, nullable = false)
    @Comment("授权主体的唯一标识")
    private String principalId;

    @ColumnDefault("0")
    @Column(name = "PRINCIPAL_TYPE", nullable = false)
    @Comment("授权主体类型:0=角色；1=岗位；2=人员,3=组，4=部门，5=组织")
    @Convert(converter = EnumConverter.AuthorizationPrincipalTypeEnumConverter.class)
    private AuthorizationPrincipalTypeEnum principalType = AuthorizationPrincipalTypeEnum.ROLE;

    /** 授权主体的名称。冗余字段，纯显示用 */
    @Column(name = "PRINCIPAL_NAME", length = 255)
    @Comment("授权主体的名称。冗余字段，纯显示用")
    private String principalName;

    /** 资源id */
    @Column(name = "RESOURCE_ID", length = 38, nullable = false)
    @Comment("资源唯一标识符")
    private String resourceId;

    @Column(name = "RESOURCE_TYPE", nullable = false)
    @Comment("资源类型。冗余字段，纯显示用")
    @Convert(converter = EnumConverter.ResourceTypeEnumConverter.class)
    private ResourceTypeEnum resourceType;

    /** 资源名称。冗余字段，纯显示用 */
    @Column(name = "RESOURCE_NAME", length = 255)
    @Comment("资源名称。冗余字段，纯显示用")
    private String resourceName;

    @Column(name = "AUTHORITY", nullable = false)
    @Comment("权限类型")
    @Convert(converter = EnumConverter.AuthorityEnumConverter.class)
    private AuthorityEnum authority;

    /** 授权人 */
    @Column(name = "AUTHORIZER", length = 30)
    @Comment("授权人")
    private String authorizer;

}
