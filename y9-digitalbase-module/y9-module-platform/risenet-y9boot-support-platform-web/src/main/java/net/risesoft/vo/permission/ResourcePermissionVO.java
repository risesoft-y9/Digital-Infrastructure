package net.risesoft.vo.permission;

import java.util.List;

import lombok.Data;

import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.permission.AuthorizationPrincipalTypeEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;

/**
 * 主体的拥有的某个系统所有资源权限详情
 * 
 * @author shidaobang
 * @date 2023/07/05
 * @since 9.6.2
 */
@Data
public class ResourcePermissionVO {

    /** 系统id */
    private String systemId;

    /** 系统中文名称 */
    private String systemCnName;

    /** 资源列表 */
    private List<Resource> resourceList;

    @Data
    public static class Resource {
        /** 资源 */
        private String resourceName;

        /** 资源层级 */
        private int level = 0;

        /** 资源类型 */
        private ResourceTypeEnum resourceType;

        private Boolean enabled;

        /** 权限详细列表 */
        private List<PermissionDetail> permissionDetailList;
    }

    @Data
    public static class PermissionDetail {
        /** 权限类型 */
        private AuthorityEnum authority;

        /** 授权主体类型 */
        private AuthorizationPrincipalTypeEnum principalType;

        /** 资源是否为继承上级节点的权限 */
        private Boolean inherit;

        /** 授权主体名称 */
        private String principalName;
    }
}
