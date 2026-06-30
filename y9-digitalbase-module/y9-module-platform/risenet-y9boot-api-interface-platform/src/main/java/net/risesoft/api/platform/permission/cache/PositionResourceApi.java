package net.risesoft.api.platform.permission.cache;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.model.platform.resource.App;
import net.risesoft.model.platform.resource.FrontendMenu;
import net.risesoft.model.platform.resource.Resource;
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
     * 递归获得某一资源下，岗位有相应权限的前端菜单和按钮（树形）
     *
     * @param tenantId 租户id
     * @param positionId 人员id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @param resourceId 资源id
     * @return {@code Y9Result<List<FrontendMenu>>} 通用请求返回对象 - data 是有权限的菜单和按钮（树形）
     * @since 9.6.8
     */
    @GetMapping("/listMenusRecursively")
    Y9Result<List<FrontendMenu>> listMenusRecursively(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("authority") AuthorityEnum authority,
        @RequestParam("resourceId") @NotBlank String resourceId);

    /**
     * 递归获得 customId 对应的某一资源下，岗位有相应权限的前端菜单和按钮（树形）
     *
     * @param tenantId 租户id
     * @param positionId 人员id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @param customId 自定义id
     * @return {@code Y9Result<List<FrontendMenu>>} 通用请求返回对象 - data 是有权限的菜单和按钮（树形）
     * @since 9.6.10
     */
    @GetMapping("/listMenusRecursivelyByCustomId")
    Y9Result<List<FrontendMenu>> listMenusRecursivelyByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("authority") AuthorityEnum authority,
        @RequestParam("customId") @NotBlank String customId);

    /**
     * 获得某一资源下，岗位有相应操作权限的子资源集合
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @param resourceId 资源id
     * @param resourceType 资源类型，为空时不筛选
     * @return {@code Y9Result<List<Resource>>} 有权限的子资源集合
     * @since 9.6.0
     */
    @GetMapping("/listSubResources")
    Y9Result<List<Resource>> listSubResources(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("authority") AuthorityEnum authority,
        @RequestParam(name = "resourceId", required = false) String resourceId,
        @RequestParam(name = "resourceType", required = false) ResourceTypeEnum resourceType);

    /**
     * 获得 customId 对应的某一资源下，岗位有相应操作权限的子资源集合
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @param customId 自定义id
     * @param resourceType 资源类型，为空时不筛选
     * @return {@code Y9Result<List<Resource>>} 有权限的子资源集合
     * @since 9.6.10
     */
    @GetMapping("/listSubResourcesByCustomId")
    Y9Result<List<Resource>> listSubResourcesByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("authority") AuthorityEnum authority,
        @RequestParam("customId") @NotBlank String customId,
        @RequestParam(name = "resourceType", required = false) ResourceTypeEnum resourceType);

    /**
     * 根据岗位id和操作类型，获取有权限的应用列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param authority 操作类型 {@link AuthorityEnum}
     * @return {@code Y9Result<List<App>>} 通用请求返回对象 - data 是应用列表
     * @since 9.6.0
     */
    @GetMapping("/listApps")
    Y9Result<List<App>> listApps(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("authority") AuthorityEnum authority);

}
