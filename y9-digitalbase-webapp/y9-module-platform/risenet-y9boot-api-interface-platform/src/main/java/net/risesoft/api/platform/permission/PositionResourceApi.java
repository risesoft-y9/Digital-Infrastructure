package net.risesoft.api.platform.permission;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.model.platform.Resource;
import net.risesoft.pojo.Y9Result;

/**
 * 岗位资源权限组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
public interface PositionResourceApi {

    /**
     * 判断岗位对资源是否有指定的操作权限
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param resourceId 资源id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断是否有权限
     * @since 9.6.0
     */
    @GetMapping("/hasPermission")
    Y9Result<Boolean> hasPermission(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId,
        @RequestParam("resourceId") @NotBlank String resourceId, @RequestParam("authority") AuthorityEnum authority);

    /**
     * 判断岗位对 customId 对应的资源是否有指定的操作权限
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param customId 自定义id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断是否有权限
     * @since 9.6.0
     */
    @GetMapping("/hasPermissionByCustomId")
    Y9Result<Boolean> hasPermissionByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("customId") @NotBlank String customId,
        @RequestParam("authority") AuthorityEnum authority);

    /**
     * 获得某一资源下，岗位有相应操作权限的菜单资源集合
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @param resourceId 资源id
     * @return {@code Y9Result<List<Menu>>} 通用请求返回对象 - data 是有权限的菜单资源集合
     * @since 9.6.0
     */
    @GetMapping("/listSubMenus")
    Y9Result<List<Resource>> listSubMenus(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("authority") AuthorityEnum authority,
        @RequestParam("resourceId") @NotBlank String resourceId);

    /**
     * 获得某一资源下，岗位有相应操作权限的子资源集合
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @param resourceId 资源id
     * @return {@code Y9Result<List<Resource>>} 有权限的子资源集合
     * @since 9.6.0
     */
    @GetMapping("/listSubResources")
    Y9Result<List<Resource>> listSubResources(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("authority") AuthorityEnum authority,
        @RequestParam(name = "resourceId", required = false) String resourceId);
}
