package net.risesoft.api.permission;

import java.util.List;

import net.risesoft.enums.AuthorityEnum;
import net.risesoft.model.Resource;

/**
 * 岗位资源权限组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
public interface PositionResourceApi {
    /**
     * 判断岗位对资源是否有指定的操作权限
     *
     * @param tenantId 租户id
     * @param positionId 操作者唯一标识
     * @param resourceId 资源唯一标识
     * @param authority 操作类型 {@link AuthorityEnum}
     * @return Boolean 是否有权限
     * @since 9.6.0
     */
    boolean hasPermission(String tenantId, String positionId, String resourceId, Integer authority);

    /**
     * 判断岗位对 customId 对应的资源是否有指定的操作权限
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param customId 自定义id
     * @param authority 操作类型 {@link AuthorityEnum}
     * @return boolean
     * @since 9.6.0
     */
    boolean hasPermissionByCustomId(String tenantId, String positionId, String customId, Integer authority);

    /**
     * 获得某一资源下,主体对象有相应操作权限的子菜单
     *
     * @param tenantId 租户id
     * @param positionId 操作者唯一标识
     * @param authority 操作类型 {@link AuthorityEnum}
     * @param resourceId 资源唯一标识
     * @return List&lt;Resource&gt; 有操作权限的子菜单
     * @since 9.6.0
     */
    List<Resource> listSubMenus(String tenantId, String positionId, Integer authority, String resourceId);

    /**
     * 获得某一资源下,主体对象有相应操作权限的子节点
     *
     * @param tenantId 租户id
     * @param positionId 操作者唯一标识
     * @param authority 操作类型 {@link AuthorityEnum}
     * @param resourceId 资源唯一标识
     * @return List&lt;Resource&gt; 有操作权限的子节点
     * @since 9.6.0
     */
    List<Resource> listSubResources(String tenantId, String positionId, Integer authority, String resourceId);
}
