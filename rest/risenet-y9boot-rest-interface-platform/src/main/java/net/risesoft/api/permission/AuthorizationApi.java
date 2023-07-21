package net.risesoft.api.permission;

import net.risesoft.enums.AuthorityEnum;

/**
 * 权限管理组件
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
public interface AuthorizationApi {

    /**
     * 保存授权信息
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param resourceId 资源id
     * @param roleId 角色id
     * @param authority 操作类型 {@link AuthorityEnum}
     * @since 9.6.0
     */
    void save(String tenantId, String personId, String resourceId, String roleId, Integer authority);

}
