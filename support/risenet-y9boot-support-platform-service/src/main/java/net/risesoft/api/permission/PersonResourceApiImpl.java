package net.risesoft.api.permission;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.AuthorityEnum;
import net.risesoft.enums.ResourceTypeEnum;
import net.risesoft.model.Menu;
import net.risesoft.model.Resource;
import net.risesoft.model.VueMenu;
import net.risesoft.service.identity.Y9PersonToResourceAndAuthorityService;
import net.risesoft.y9.Y9LoginUserHolder;
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
@RestController
@RequestMapping(value = "/services/rest/personResource", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
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
    @GetMapping("/hasPermission")
    public boolean hasPermission(@RequestParam String tenantId, @RequestParam String personId, @RequestParam String resourceId, @RequestParam Integer authority) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        return y9PersonToResourceAndAuthorityService.hasPermission(personId, resourceId, authority);
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
    @GetMapping("/hasPermissionByCustomId")
    public boolean hasPermissionByCustomId(@RequestParam String tenantId, @RequestParam String personId, @RequestParam String customId, @RequestParam Integer authority) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        return y9PersonToResourceAndAuthorityService.hasPermissionByCustomId(personId, customId, authority);
    }

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
    @Override
    @GetMapping("/listMenusRecursively")
    public List<VueMenu> listMenusRecursively(@RequestParam String tenantId, @RequestParam String personId, @RequestParam Integer authority, @RequestParam String resourceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        List<VueMenu> vueMenuList = new ArrayList<>();
        vueMenuBuilder.buildVueMenus(personId, authority, resourceId, vueMenuList);

        return vueMenuList;
    }

    /**
     * 获得某一资源下,主体对象有相应操作权限的子菜单
     *
     * @param tenantId 租户id
     * @param personId 操作者唯一标识
     * @param authority 操作类型 {@link AuthorityEnum}
     * @param resourceId 资源唯一标识
     * @return List&lt;Resource&gt; 有操作权限的子菜单
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listSubMenus")
    public List<Menu> listSubMenus(@RequestParam String tenantId, @RequestParam String personId, @RequestParam Integer authority, @RequestParam String resourceId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Menu> y9MenuList = y9PersonToResourceAndAuthorityService.listSubMenus(personId, resourceId, ResourceTypeEnum.MENU.getValue(), authority);
        return Y9ModelConvertUtil.convert(y9MenuList, Menu.class);
    }

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
    @Override
    @GetMapping("/listSubResources")
    public List<Resource> listSubResources(@RequestParam String tenantId, @RequestParam String personId, @RequestParam Integer authority, @RequestParam String resourceId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9ResourceBase> y9ResourceBaseList = y9PersonToResourceAndAuthorityService.listSubResources(personId, resourceId, authority);
        return Y9ModelConvertUtil.convert(y9ResourceBaseList, Resource.class);
    }
}
