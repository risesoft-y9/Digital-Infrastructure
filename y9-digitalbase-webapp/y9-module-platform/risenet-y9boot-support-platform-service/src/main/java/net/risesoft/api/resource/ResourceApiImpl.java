package net.risesoft.api.resource;

import java.util.List;
import java.util.Optional;

import jakarta.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.resource.ResourceApi;
import net.risesoft.enums.platform.ResourceTypeEnum;
import net.risesoft.model.platform.Resource;
import net.risesoft.pojo.Y9Result;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.service.resource.CompositeResourceService;
import net.risesoft.y9public.service.resource.Y9MenuService;

/**
 * 资源管理组件
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
@RequestMapping(value = "/services/rest/v1/resource", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ResourceApiImpl implements ResourceApi {

    private final CompositeResourceService compositeResourceService;
    private final Y9MenuService y9MenuService;

    /**
     * 创建菜单资源
     *
     * @param resourceId 资源id
     * @param resourceName 资源名称
     * @param parentResourceId 父资源id
     * @param customId 自定义标识
     * @return {@code Y9Result<Resource>} 通用请求返回对象 - data 是新创建的菜单资源对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Resource> createMenuResource(@RequestParam("resourceId") @NotBlank String resourceId,
        @RequestParam("resourceName") @NotBlank String resourceName,
        @RequestParam("parentResourceId") @NotBlank String parentResourceId,
        @RequestParam("customId") @NotBlank String customId) {
        Y9ResourceBase parentResource = compositeResourceService.findById(parentResourceId);
        Y9Menu y9Menu;
        Optional<Y9Menu> y9MenuOptional = y9MenuService.findById(resourceId);
        if (y9MenuOptional.isEmpty()) {
            y9Menu = new Y9Menu();
            y9Menu.setId(resourceId);
        } else {
            y9Menu = y9MenuOptional.get();
        }
        if (parentResource != null) {
            y9Menu.setAppId(parentResource.getAppId());
            y9Menu.setSystemId(parentResource.getSystemId());
            y9Menu.setParentId(parentResourceId);
        }
        y9Menu.setName(resourceName);
        y9Menu.setInherit(Boolean.FALSE);
        y9Menu.setHidden(Boolean.FALSE);
        y9Menu.setEnabled(Boolean.TRUE);
        if (StringUtils.isNotBlank(customId)) {
            y9Menu.setCustomId(customId);
        }
        y9Menu = y9MenuService.saveOrUpdate(y9Menu);
        return Y9Result.success(ModelConvertUtil.resourceBaseToResource(y9Menu));
    }

    /**
     * 根据customId和parentId获取资源
     *
     * @param customId 自定义id
     * @param parentId 资源id
     * @param resourceType 资源类型 {@link ResourceTypeEnum}
     * @return {@code Y9Result<Resource>} 通用请求返回对象 - data 是资源对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Resource> findByCustomIdAndParentId(@RequestParam("customId") @NotBlank String customId,
        @RequestParam("parentId") @NotBlank String parentId,
        @RequestParam("resourceType") ResourceTypeEnum resourceType) {
        Y9ResourceBase y9ResourceBase =
            compositeResourceService.findByCustomIdAndParentId(customId, parentId, resourceType).orElse(null);
        return Y9Result.success(ModelConvertUtil.resourceBaseToResource(y9ResourceBase));
    }

    /**
     * 获得指定资源的父资源
     *
     * @param resourceId：资源的唯一标识
     * @return {@code Y9Result<Resource>} 通用请求返回对象 - data 是父资源对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Resource> getParentResource(@RequestParam("resourceId") @NotBlank String resourceId) {
        Y9ResourceBase y9ResourceBase = compositeResourceService.findById(resourceId);
        Y9ResourceBase parent = compositeResourceService.findById(y9ResourceBase.getParentId());
        return Y9Result.success(ModelConvertUtil.resourceBaseToResource(parent));
    }

    /**
     * 根据id获取资源对象
     *
     * @param resourceId 资源唯一标示
     * @return {@code Y9Result<Resource>} 通用请求返回对象 - data 是资源对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Resource> getResource(@RequestParam("resourceId") @NotBlank String resourceId) {
        Y9ResourceBase y9ResourceBase = compositeResourceService.findById(resourceId);
        return Y9Result.success(ModelConvertUtil.resourceBaseToResource(y9ResourceBase));
    }

    /**
     * 获取指定资源的子菜单资源
     *
     * @param resourceId 资源id
     * @return {@code Y9Result<List<Resource>>} 通用请求返回对象 - data 是资源对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Resource>> listSubMenus(@RequestParam("resourceId") @NotBlank String resourceId) {
        List<Y9Menu> y9MenuList = y9MenuService.findByParentId(resourceId);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9MenuList, Resource.class));
    }

    /**
     * 获得指定资源的子资源
     *
     * @param resourceId 资源唯一标识
     * @return {@code Y9Result<List<Resource>>} 通用请求返回对象 - data 是资源对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Resource>> listSubResources(@RequestParam("resourceId") @NotBlank String resourceId) {
        List<Y9ResourceBase> y9ResourceBaseList = compositeResourceService.listRootResourceBySystemId(resourceId);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9ResourceBaseList, Resource.class));
    }

}
