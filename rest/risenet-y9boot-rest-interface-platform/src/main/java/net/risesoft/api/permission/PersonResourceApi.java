package net.risesoft.api.permission;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.enums.AuthorityEnum;
import net.risesoft.model.Menu;
import net.risesoft.model.Resource;
import net.risesoft.model.VueMenu;

/**
 * 人员资源权限组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
public interface PersonResourceApi {

    /**
     * 判断actor对resource是否有指定的操作权限
     *
     * @param tenantId 租户id
     * @param personId 操作者唯一标识
     * @param resourceId 资源唯一标识
     * @param authority 操作类型 {@link AuthorityEnum}
     * @return Boolean 是否有权限
     * @since 9.6.0
     */
    @GetMapping("/hasPermission")
    boolean hasPermission(@RequestParam("tenantId") String tenantId, @RequestParam("personId") String personId, @RequestParam("resourceId") String resourceId, @RequestParam("authority") Integer authority);

    /**
     * 判断 person 对 customId 对应的 resource 是否有指定的操作权限
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param customId 自定义id
     * @param authority 操作类型 {@link AuthorityEnum}
     * @return boolean
     * @since 9.6.0
     */
    @GetMapping("/hasPermissionByCustomId")
    boolean hasPermissionByCustomId(@RequestParam("tenantId") String tenantId, @RequestParam("personId") String personId, @RequestParam("customId") String customId, @RequestParam("authority") Integer authority);

    /**
     * 递归获得某一资源下,主体对象有相应权限的菜单
     *
     * @param tenantId 租户id
     * @param personId 操作者唯一标识
     * @param authority 操作类型 {@link AuthorityEnum}
     * @param resourceId 资源唯一标识
     * @return List&lt;Resource&gt; 有权限的子菜单
     * @since 9.6.0
     */
    @GetMapping("/listMenusRecursively")
    List<VueMenu> listMenusRecursively(@RequestParam("tenantId") String tenantId, @RequestParam("personId") String personId, @RequestParam("authority") Integer authority, @RequestParam("resourceId") String resourceId);

    /**
     * 获得某一资源下,主体对象有相应操作权限的子节点(子节点必须为菜单)
     *
     * @param tenantId 租户id
     * @param personId 操作者唯一标识
     * @param authority 操作类型 {@link AuthorityEnum}
     * @param resourceId 资源唯一标识
     * @return List&lt;Resource&gt; 有操作权限的子菜单
     * @since 9.6.0
     */
    @GetMapping("/listSubMenus")
    List<Menu> listSubMenus(@RequestParam("tenantId") String tenantId, @RequestParam("personId") String personId, @RequestParam("authority") Integer authority, @RequestParam("resourceId") String resourceId);

    /**
     * 获得某一资源下,主体对象有相应操作权限的子节点
     *
     * @param tenantId 租户id
     * @param personId 操作者唯一标识
     * @param authority 操作类型 {@link AuthorityEnum}
     * @param resourceId 资源唯一标识
     * @return List&lt;Resource&gt; 有操作权限的子节点
     * @since 9.6.0
     */
    @GetMapping("/listSubResources")
    List<Resource> listSubResources(@RequestParam("tenantId") String tenantId, @RequestParam("personId") String personId, @RequestParam("authority") Integer authority, @RequestParam("resourceId") String resourceId);
}
