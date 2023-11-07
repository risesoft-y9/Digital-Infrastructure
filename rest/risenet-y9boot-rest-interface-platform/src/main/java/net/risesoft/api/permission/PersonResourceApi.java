package net.risesoft.api.permission;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

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
     * 判断人员对资源是否有指定的操作权限
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param resourceId 资源id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断是否有权限
     * @since 9.6.0
     */
    @GetMapping("/hasPermission")
    Y9Result<Boolean> hasPermission(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("resourceId") @NotBlank String resourceId,
        @RequestParam("authority") Integer authority);

    /**
     * 判断人员对 customId 对应的资源是否有指定的操作权限
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param customId 自定义id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断是否有权限
     * @since 9.6.0
     */
    @GetMapping("/hasPermissionByCustomId")
    Y9Result<Boolean> hasPermissionByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("customId") @NotBlank String customId,
        @RequestParam("authority") Integer authority);

    /**
     * 递归获得某一资源下，人员有相应权限的菜单和按钮（树形）
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @param resourceId 资源id
     * @return {@code Y9Result<List<VueMenu>>} 通用请求返回对象 - data 是有权限的菜单和按钮（树形）
     * @since 9.6.0
     */
    @GetMapping("/listMenusRecursively")
    Y9Result<List<VueMenu>> listMenusRecursively(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("authority") Integer authority,
        @RequestParam("resourceId") @NotBlank String resourceId);

    /**
     * 获得某一资源下，人员有相应操作权限的菜单资源集合
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @param resourceId 资源id
     * @return {@code Y9Result<List<Menu>>} 通用请求返回对象 - data 是有权限的菜单资源集合
     * @since 9.6.0
     */
    @GetMapping("/listSubMenus")
    Y9Result<List<Menu>> listSubMenus(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("authority") Integer authority,
        @RequestParam("resourceId") @NotBlank String resourceId);

    /**
     * 获得某一资源下，人员有相应操作权限的子资源集合
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @param resourceId 资源id
     * @return {@code Y9Result<List<Resource>>} 有权限的子资源集合
     * @since 9.6.0
     */
    @GetMapping("/listSubResources")
    Y9Result<List<Resource>> listSubResources(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("authority") Integer authority,
        @RequestParam("resourceId") @NotBlank String resourceId);
}
