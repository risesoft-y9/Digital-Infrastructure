package net.risesoft.model.platform.permission;

import javax.validation.constraints.NotBlank;

import lombok.Data;

import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.permission.AuthorizationPrincipalTypeEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.model.BaseModel;

/**
 * 授权配置
 *
 * @author shidaobang
 * @date 2022/09/14
 */
@Data
public class Authorization extends BaseModel {

    private static final long serialVersionUID = -2921655307296670562L;

    /** 主键 */
    private String id;

    /** 租户id */
    private String tenantId;

    /** 授权主体id */
    @NotBlank
    private String principalId;

    /**
     * 授权主体类型
     *
     * {@link AuthorizationPrincipalTypeEnum}
     */
    private AuthorizationPrincipalTypeEnum principalType = AuthorizationPrincipalTypeEnum.ROLE;

    /** 授权主体的名称。冗余字段，纯显示用 */
    private String principalName;

    /** 资源id */
    @NotBlank
    private String resourceId;

    /**
     * 资源类型。冗余字段，纯显示用
     *
     * {@link ResourceTypeEnum}
     */
    private ResourceTypeEnum resourceType;

    /** 资源名称。冗余字段，纯显示用 */
    private String resourceName;

    /**
     * 权限类型
     *
     * {@link AuthorityEnum}
     */
    private AuthorityEnum authority;

    /** 授权人 */
    private String authorizer;
}
