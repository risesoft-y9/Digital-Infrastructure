package net.risesoft.api.permission;

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
@RequestMapping(value = "/services/rest/positionResource", produces = MediaType.APPLICATION_JSON_VALUE)
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
    public boolean hasPermission(@RequestParam String tenantId, @RequestParam String positionId, @RequestParam String resourceId, @RequestParam Integer authority) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        return y9PositionToResourceAndAuthorityService.hasPermission(positionId, resourceId, authority);
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
    public boolean hasPermissionByCustomId(String tenantId, String positionId, String customId, Integer authority) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        return y9PositionToResourceAndAuthorityService.hasPermissionByCustomId(positionId, customId, authority);
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
    public List<Resource> listSubMenus(@RequestParam String tenantId, @RequestParam String positionId, @RequestParam Integer authority, @RequestParam String resourceId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Menu> y9MenuList = y9PositionToResourceAndAuthorityService.listSubMenus(positionId, resourceId, ResourceTypeEnum.MENU.getValue(), authority);
        return Y9ModelConvertUtil.convert(y9MenuList, Resource.class);
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
    public List<Resource> listSubResources(@RequestParam String tenantId, @RequestParam String positionId, @RequestParam Integer authority, @RequestParam(required = false) String resourceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        List<Y9ResourceBase> returnResourceList = y9PositionToResourceAndAuthorityService.listSubResources(positionId, resourceId, authority);
        return Y9ModelConvertUtil.convert(returnResourceList, Resource.class);
    }
}
