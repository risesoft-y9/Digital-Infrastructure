package net.risesoft.api.v0.permission;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.model.platform.Resource;

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
@Deprecated
public interface PositionResourceApi {

    /**
     * 判断person对resource是否有指定的操作权限
     *
     * @param tenantId 租户id
     * @param positionId 操作者唯一标识
     * @param resourceId 资源唯一标识
     * @param authority 操作类型 {@link AuthorityEnum}
     * @return Boolean 是否有权限
     * @since 9.6.0
     */
    @GetMapping("/hasPermission")
    boolean hasPermission(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId,
        @RequestParam("resourceId") @NotBlank String resourceId, @RequestParam("authority") Integer authority);

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
    @GetMapping("/hasPermissionByCustomId")
    boolean hasPermissionByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("customId") @NotBlank String customId,
        @RequestParam("authority") Integer authority);

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
    @GetMapping("/listSubMenus")
    List<Resource> listSubMenus(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("authority") Integer authority,
        @RequestParam("resourceId") @NotBlank String resourceId);

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
    @GetMapping("/listSubResources")
    List<Resource> listSubResources(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("authority") Integer authority,
        @RequestParam(name = "resourceId", required = false) String resourceId);
}
