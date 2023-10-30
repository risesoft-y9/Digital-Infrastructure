package net.risesoft.api.permission;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.enums.AuthorityEnum;
import net.risesoft.model.Menu;
import net.risesoft.model.Resource;
import net.risesoft.model.VueMenu;
import net.risesoft.pojo.Y9Result;

/**
 * 人员资源权限组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
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
    Y9Result<Boolean> hasPermission(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("resourceId") @NotBlank String resourceId,
        @RequestParam("authority") Integer authority);

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
    Y9Result<Boolean> hasPermissionByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("customId") @NotBlank String customId,
        @RequestParam("authority") Integer authority);

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
    Y9Result<List<VueMenu>> listMenusRecursively(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("authority") Integer authority,
        @RequestParam("resourceId") @NotBlank String resourceId);

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
    Y9Result<List<Menu>> listSubMenus(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("authority") Integer authority,
        @RequestParam("resourceId") @NotBlank String resourceId);

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
    Y9Result<List<Resource>> listSubResources(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("authority") Integer authority,
        @RequestParam("resourceId") @NotBlank String resourceId);
}
