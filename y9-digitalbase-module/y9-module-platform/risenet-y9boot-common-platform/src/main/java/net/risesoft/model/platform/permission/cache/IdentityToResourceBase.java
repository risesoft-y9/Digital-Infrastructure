package net.risesoft.model.platform.permission.cache;

import lombok.Data;

import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.model.BaseModel;

/**
 * 人员/岗位拥有的资源基类
 *
 * @author shidaobang
 * @date 2025/11/05
 */
@Data
public abstract class IdentityToResourceBase extends BaseModel {

    private static final long serialVersionUID = 8525671041203288234L;

    /** 主键id */
    protected String id;

    /** 租户id */
    protected String tenantId;

    /** 权限配置id 方便找到权限来源及权限缓存的处理 */
    protected String authorizationId;

    /** 权限类型 */
    protected AuthorityEnum authority;

    /** 资源id */
    protected String resourceId;

    /** 资源是否为继承上级节点的权限 */
    protected Boolean inherit;

    /** 资源类型：0=应用，1=菜单，2=操作 */
    protected ResourceTypeEnum resourceType;

    /** 父资源id */
    protected String parentResourceId;

    /** 应用id */
    protected String appId;

    /** 系统id */
    protected String systemId;
}
