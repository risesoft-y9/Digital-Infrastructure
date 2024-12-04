package net.risesoft.api.permission;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.permission.PositionResourceApi;
import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.enums.platform.IdentityTypeEnum;
import net.risesoft.enums.platform.ResourceTypeEnum;
import net.risesoft.model.platform.Resource;
import net.risesoft.model.platform.VueMenu;
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
    private final VueMenuBuilder vueMenuBuilder;

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
    @Override
    public Y9Result<Boolean> hasPermission(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId,
        @RequestParam("resourceId") @NotBlank String resourceId, @RequestParam("authority") AuthorityEnum authority) {
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
     * @param authority 权限类型 {@link AuthorityEnum}
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断是否有权限
     * @since 9.6.0
     */
    @Override
    public Y9Result<Boolean> hasPermissionByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("customId") @NotBlank String customId,
        @RequestParam("authority") AuthorityEnum authority) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result
            .success(y9PositionToResourceAndAuthorityService.hasPermissionByCustomId(positionId, customId, authority));
    }

    /**
     * 递归获得某一资源下，岗位有相应权限的菜单和按钮（树形）
     *
     * @param tenantId 租户id
     * @param positionId 人员id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @param resourceId 资源id
     * @return {@code Y9Result<List<VueMenu>>} 通用请求返回对象 - data 是有权限的菜单和按钮（树形）
     * @since 9.6.8
     */
    @Override
    public Y9Result<List<VueMenu>> listMenusRecursively(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("authority") AuthorityEnum authority,
        @RequestParam("resourceId") @NotBlank String resourceId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<VueMenu> vueMenuList = new ArrayList<>();
        vueMenuBuilder.buildVueMenus(IdentityTypeEnum.POSITION, positionId, authority, resourceId, vueMenuList);

        return Y9Result.success(vueMenuList);
    }

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
    @Override
    public Y9Result<List<Resource>> listSubMenus(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("authority") AuthorityEnum authority,
        @RequestParam("resourceId") @NotBlank String resourceId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Menu> y9MenuList = y9PositionToResourceAndAuthorityService.listSubMenus(positionId, resourceId,
            ResourceTypeEnum.MENU, authority);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9MenuList, Resource.class));
    }

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
    @Override
    public Y9Result<List<Resource>> listSubResources(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("authority") AuthorityEnum authority,
        @RequestParam(name = "resourceId", required = false) String resourceId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9ResourceBase> returnResourceList =
            y9PositionToResourceAndAuthorityService.listSubResources(positionId, resourceId, authority);
        return Y9Result.success(Y9ModelConvertUtil.convert(returnResourceList, Resource.class));
    }
}
