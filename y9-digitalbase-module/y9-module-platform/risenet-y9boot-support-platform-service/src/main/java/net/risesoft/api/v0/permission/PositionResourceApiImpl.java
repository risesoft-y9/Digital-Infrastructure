package net.risesoft.api.v0.permission;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.v0.permission.PositionResourceApi;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.model.platform.resource.Menu;
import net.risesoft.model.platform.resource.Resource;
import net.risesoft.service.permission.cache.Y9PositionToResourceService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9EnumUtil;

/**
 * 岗位资源权限组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Primary
@Validated
@RestController(value = "v0PositionResourceApiImpl")
@RequestMapping(value = "/services/rest/positionResource", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Deprecated
public class PositionResourceApiImpl implements PositionResourceApi {

    private final Y9PositionToResourceService y9PositionToResourceService;

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
    @Override
    public boolean hasPermission(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId,
        @RequestParam("resourceId") @NotBlank String resourceId, @RequestParam("authority") Integer authority) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9PositionToResourceService.hasPermission(positionId, resourceId,
            Y9EnumUtil.valueOf(AuthorityEnum.class, authority));
    }

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
    @Override
    public boolean hasPermissionByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("customId") @NotBlank String customId,
        @RequestParam("authority") Integer authority) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9PositionToResourceService.hasPermissionByCustomId(positionId, customId,
            Y9EnumUtil.valueOf(AuthorityEnum.class, authority));
    }

    /**
     * 获得某一资源下,主体对象有相应操作权限的子菜单
     *
     * @param tenantId 租户id
     * @param positionId 操作者唯一标识
     * @param authority 操作类型 {@link AuthorityEnum}
     * @param resourceId 资源唯一标识
     * @return {@code List<Resource>} 有操作权限的子菜单
     * @since 9.6.0
     */
    @Override
    public List<Resource> listSubMenus(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("authority") Integer authority,
        @RequestParam("resourceId") @NotBlank String resourceId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Menu> y9MenuList = y9PositionToResourceService.listSubMenus(positionId, resourceId, ResourceTypeEnum.MENU,
            Y9EnumUtil.valueOf(AuthorityEnum.class, authority));
        return PlatformModelConvertUtil.convert(y9MenuList, Resource.class);
    }

    /**
     * 获得某一资源下,主体对象有相应操作权限的子节点
     *
     * @param tenantId 租户id
     * @param positionId 操作者唯一标识
     * @param authority 操作类型 {@link AuthorityEnum}
     * @param resourceId 资源唯一标识
     * @return {@code List<Resource>} 有操作权限的子节点
     * @since 9.6.0
     */
    @Override
    public List<Resource> listSubResources(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("authority") Integer authority,
        @RequestParam(name = "resourceId", required = false) String resourceId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9PositionToResourceService.listSubResources(positionId, resourceId,
            Y9EnumUtil.valueOf(AuthorityEnum.class, authority));
    }
}
