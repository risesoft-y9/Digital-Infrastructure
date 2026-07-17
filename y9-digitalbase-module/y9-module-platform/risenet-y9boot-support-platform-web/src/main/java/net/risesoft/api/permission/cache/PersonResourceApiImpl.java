package net.risesoft.api.permission.cache;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.permission.FrontendMenuBuilder;
import net.risesoft.api.platform.permission.cache.PersonResourceApi;
import net.risesoft.enums.platform.org.IdentityTypeEnum;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.model.platform.resource.App;
import net.risesoft.model.platform.resource.FrontendMenu;
import net.risesoft.model.platform.resource.Resource;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.permission.cache.Y9PersonToResourceService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 人员资源权限查看组件
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
@RequestMapping(value = "/services/rest/v1/personResource", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PersonResourceApiImpl implements PersonResourceApi {

    private final Y9PersonToResourceService y9PersonToResourceService;
    private final FrontendMenuBuilder frontendMenuBuilder;

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
    @Override
    public Y9Result<Boolean> hasPermission(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("resourceId") @NotBlank String resourceId,
        @RequestParam("authority") AuthorityEnum authority) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PersonToResourceService.hasPermission(personId, resourceId, authority));
    }

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
    @Override
    public Y9Result<Boolean> hasPermissionByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("customId") @NotBlank String customId,
        @RequestParam("authority") AuthorityEnum authority) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PersonToResourceService.hasPermissionByCustomId(personId, customId, authority));
    }

    /**
     * 递归获得某一资源下，人员有相应权限的前端菜单和按钮（树形）
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @param resourceId 资源id
     * @return {@code Y9Result<List<FrontendMenu>>} 通用请求返回对象 - data 是有权限的菜单和按钮（树形）
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<FrontendMenu>> listMenusRecursively(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("authority") AuthorityEnum authority,
        @RequestParam("resourceId") @NotBlank String resourceId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<FrontendMenu> frontendMenuList = new ArrayList<>();
        frontendMenuBuilder.buildFrontendMenus(IdentityTypeEnum.PERSON, personId, authority, resourceId,
            frontendMenuList);

        return Y9Result.success(frontendMenuList);
    }

    /**
     * 递归获得 customId 对应的某一资源下，人员有相应权限的前端菜单和按钮（树形）
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @param customId 自定义id
     * @return {@code Y9Result<List<FrontendMenu>>} 通用请求返回对象 - data 是有权限的菜单和按钮（树形）
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<FrontendMenu>> listMenusRecursivelyByCustomId(
        @RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId,
        @RequestParam("authority") AuthorityEnum authority, @RequestParam("customId") @NotBlank String customId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<FrontendMenu> frontendMenuList = new ArrayList<>();
        frontendMenuBuilder.buildFrontendMenusByCustomId(IdentityTypeEnum.PERSON, personId, authority, customId,
            frontendMenuList);

        return Y9Result.success(frontendMenuList);
    }

    /**
     * 获得某一资源下，人员有相应操作权限的子资源集合
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @param resourceId 资源id
     * @param resourceType 资源类型
     * @return {@code Y9Result<List<Resource>>} 有权限的子资源集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Resource>> listSubResources(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("authority") AuthorityEnum authority,
        @RequestParam(name = "resourceId", required = false) String resourceId,
        @RequestParam(name = "resourceType", required = false) ResourceTypeEnum resourceType) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result
            .success(y9PersonToResourceService.listSubResources(personId, resourceId, authority, resourceType));
    }

    /**
     * 获得 customId 对应的某一资源下，人员有相应操作权限的子资源集合
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @param customId 自定义id
     * @param resourceType 资源类型
     * @return {@code Y9Result<List<Resource>>} 有权限的子资源集合
     * @since 9.6.10
     */
    @Override
    public Y9Result<List<Resource>> listSubResourcesByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("authority") AuthorityEnum authority,
        @RequestParam("customId") @NotBlank String customId,
        @RequestParam(name = "resourceType", required = false) ResourceTypeEnum resourceType) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result
            .success(y9PersonToResourceService.listSubResourcesByCustomId(personId, customId, authority, resourceType));
    }

    /**
     * 根据人员id和操作类型，获取有权限的应用列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @return {@code Y9Result<List<App>>} 通用请求返回对象 - data 是有权限的应用列表
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<App>> listApps(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("authority") AuthorityEnum authority) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PersonToResourceService.listAppsByAuthority(personId, authority));
    }
}
