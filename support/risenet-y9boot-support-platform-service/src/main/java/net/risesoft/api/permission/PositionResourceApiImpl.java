package net.risesoft.api.permission;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.AuthorityEnum;
import net.risesoft.enums.ResourceTypeEnum;
import net.risesoft.model.Resource;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.identity.Y9PositionToResourceAndAuthorityService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;

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
@RestController
@RequestMapping(value = "/services/rest/v1/positionResource", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PositionResourceApiImpl implements PositionResourceApi {

    private final Y9PositionToResourceAndAuthorityService y9PositionToResourceAndAuthorityService;

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
    public Y9Result<Boolean> hasPermission(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId,
        @RequestParam("resourceId") @NotBlank String resourceId, @RequestParam("authority") Integer authority) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result
            .success(y9PositionToResourceAndAuthorityService.hasPermission(positionId, resourceId, authority));
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
    public Y9Result<Boolean> hasPermissionByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("customId") @NotBlank String customId,
        @RequestParam("authority") Integer authority) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result
            .success(y9PositionToResourceAndAuthorityService.hasPermissionByCustomId(positionId, customId, authority));
    }

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
    @Override
    public Y9Result<List<Resource>> listSubMenus(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("authority") Integer authority,
        @RequestParam("resourceId") @NotBlank String resourceId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Menu> y9MenuList = y9PositionToResourceAndAuthorityService.listSubMenus(positionId, resourceId,
            ResourceTypeEnum.MENU.getValue(), authority);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9MenuList, Resource.class));
    }

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
    @Override
    public Y9Result<List<Resource>> listSubResources(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("authority") Integer authority,
        @RequestParam(name = "resourceId", required = false) String resourceId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9ResourceBase> returnResourceList =
            y9PositionToResourceAndAuthorityService.listSubResources(positionId, resourceId, authority);
        return Y9Result.success(Y9ModelConvertUtil.convert(returnResourceList, Resource.class));
    }
}
