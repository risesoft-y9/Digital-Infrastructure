package net.risesoft.api.v0.permission;

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

import net.risesoft.api.platform.v0.permission.PersonResourceApi;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.model.platform.resource.Menu;
import net.risesoft.model.platform.resource.Resource;
import net.risesoft.model.platform.resource.VueMenu;
import net.risesoft.service.permission.cache.Y9PersonToResourceAndAuthorityService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9EnumUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;

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
@RestController(value = "v0PersonResourceApiImpl")
@RequestMapping(value = "/services/rest/personResource", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Deprecated
public class PersonResourceApiImpl implements PersonResourceApi {

    private final Y9PersonToResourceAndAuthorityService y9PersonToResourceAndAuthorityService;
    private final VueMenuBuilder vueMenuBuilder;

    /**
     * 判断person对resource是否有指定的操作权限
     *
     * @param tenantId 租户id
     * @param personId 操作者唯一标识
     * @param resourceId 资源唯一标识
     * @param authority 操作类型 {@link AuthorityEnum}
     * @return Boolean 是否有权限
     * @since 9.6.0
     */
    @Override
    public boolean hasPermission(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("resourceId") @NotBlank String resourceId,
        @RequestParam("authority") Integer authority) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9PersonToResourceAndAuthorityService.hasPermission(personId, resourceId,
            Y9EnumUtil.valueOf(AuthorityEnum.class, authority));
    }

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
    @Override
    public boolean hasPermissionByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("customId") @NotBlank String customId,
        @RequestParam("authority") Integer authority) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9PersonToResourceAndAuthorityService.hasPermissionByCustomId(personId, customId,
            Y9EnumUtil.valueOf(AuthorityEnum.class, authority));
    }

    /**
     * 递归获得某一资源下,主体对象有相应权限的菜单
     *
     * @param tenantId 租户id
     * @param personId 操作者唯一标识
     * @param authority 操作类型 {@link AuthorityEnum}
     * @param resourceId 资源唯一标识
     * @return {@code List<VueMenu>} 有权限的子菜单
     * @since 9.6.0
     */
    @Override
    public List<VueMenu> listMenusRecursively(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("authority") Integer authority,
        @RequestParam("resourceId") @NotBlank String resourceId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<VueMenu> vueMenuList = new ArrayList<>();
        vueMenuBuilder.buildVueMenus(personId, Y9EnumUtil.valueOf(AuthorityEnum.class, authority), resourceId,
            vueMenuList);

        return vueMenuList;
    }

    /**
     * 获得某一资源下,主体对象有相应操作权限的子菜单
     *
     * @param tenantId 租户id
     * @param personId 操作者唯一标识
     * @param authority 操作类型 {@link AuthorityEnum}
     * @param resourceId 资源唯一标识
     * @return {@code List<Menu>} 有操作权限的子菜单
     * @since 9.6.0
     */
    @Override
    public List<Menu> listSubMenus(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("authority") Integer authority,
        @RequestParam("resourceId") @NotBlank String resourceId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Menu> y9MenuList = y9PersonToResourceAndAuthorityService.listSubMenus(personId, resourceId,
            Y9EnumUtil.valueOf(AuthorityEnum.class, authority));
        return Y9ModelConvertUtil.convert(y9MenuList, Menu.class);
    }

    /**
     * 获得某一资源下,主体对象有相应操作权限的子节点
     *
     * @param tenantId 租户id
     * @param personId 操作者唯一标识
     * @param authority 操作类型 {@link AuthorityEnum}
     * @param resourceId 资源唯一标识
     * @return {@code List<Resource>} 有操作权限的子节点
     * @since 9.6.0
     */
    @Override
    public List<Resource> listSubResources(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("authority") Integer authority,
        @RequestParam("resourceId") @NotBlank String resourceId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9ResourceBase> y9ResourceBaseList = y9PersonToResourceAndAuthorityService.listSubResources(personId,
            resourceId, Y9EnumUtil.valueOf(AuthorityEnum.class, authority));
        return Y9ModelConvertUtil.convert(y9ResourceBaseList, Resource.class);
    }
}
