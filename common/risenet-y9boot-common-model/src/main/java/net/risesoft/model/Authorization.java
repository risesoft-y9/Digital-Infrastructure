package net.risesoft.model;


import java.io.Serializable;

import lombok.Data;

import net.risesoft.enums.AuthorityEnum;
import net.risesoft.enums.AuthorizationPrincipalTypeEnum;

/**
 * 授权配置
 *
 * @author shidaobang
 * @date 2022/09/14
 */
@Data
public class Authorization implements Serializable {
    private static final long serialVersionUID = -2921655307296670562L;

    /** 主键 */
    private String id;

    /** 租户id */
    private String tenantId;

    /** 授权主体id */
    private String principalId;

    /**
     * 授权主体类型:0=角色；1=岗位；2=人员,3=组，4=部门，5=组织
     *
     * {@link AuthorizationPrincipalTypeEnum}
     */
    private Integer principalType = AuthorizationPrincipalTypeEnum.ROLE.getValue();

    /** 授权主体的名称。冗余字段，纯显示用 */
    private String principalName;

    /** 资源id */
    private String resourceId;

    /**
     * 资源类型。冗余字段，纯显示用
     *
     * {@link net.risesoft.enums.ResourceTypeEnum}
     */
    private Integer resourceType;

    /** 资源名称。冗余字段，纯显示用 */
    private String resourceName;

    /**
     * 权限类型
     *
     * {@link AuthorityEnum}
     */
    private Integer authority;

    /** 授权人 */
    private String authorizer;
}
