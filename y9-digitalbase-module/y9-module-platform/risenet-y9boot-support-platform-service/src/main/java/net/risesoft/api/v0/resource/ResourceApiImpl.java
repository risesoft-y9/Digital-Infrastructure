package net.risesoft.api.v0.resource;

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

import net.risesoft.api.platform.v0.resource.ResourceApi;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.model.platform.System;
import net.risesoft.model.platform.resource.Menu;
import net.risesoft.model.platform.resource.Resource;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.util.Y9EnumUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.repository.resource.Y9AppRepository;
import net.risesoft.y9public.service.resource.CompositeResourceService;
import net.risesoft.y9public.service.resource.Y9MenuService;
import net.risesoft.y9public.service.resource.Y9SystemService;

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
@RestController(value = "v0ResourceApiImpl")
@RequestMapping(value = "/services/rest/resource", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Deprecated
public class ResourceApiImpl implements ResourceApi {

    private final CompositeResourceService compositeResourceService;
    private final Y9SystemService y9SystemService;
    private final Y9AppRepository y9AppRepository;
    private final Y9MenuService y9MenuService;

    /**
     * 创建菜单资源
     *
     * @param resourceId 资源id
     * @param resourceName 资源名称
     * @param parentResourceId 父资源id
     * @param customId 自定义id
     * @return Resource 新创建的资源对象
     * @since 9.6.0
     */
    @Override
    public Resource createMenuResource(@RequestParam("resourceId") @NotBlank String resourceId,
        @RequestParam("resourceName") @NotBlank String resourceName,
        @RequestParam("parentResourceId") @NotBlank String parentResourceId,
        @RequestParam("customId") @NotBlank String customId) {
        Resource parentResource = compositeResourceService.findById(parentResourceId);
        Menu y9Menu;
        Optional<Menu> y9MenuOptional = y9MenuService.findById(resourceId);
        if (y9MenuOptional.isEmpty()) {
            y9Menu = new Menu();
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
        return y9MenuService.saveOrUpdate(y9Menu);
    }

    /**
     * 根据 customId 和 parentId 获取资源
     *
     * @param customId 自定义标识
     * @param parentId 资源id
     * @return Resource 资源对象
     * @since 9.6.0
     */
    @Override
    public Resource findByCustomIdAndParentId(@RequestParam("customId") @NotBlank String customId,
        @RequestParam("parentId") @NotBlank String parentId, @RequestParam("resourceType") Integer resourceType) {
        return compositeResourceService
            .findByCustomIdAndParentId(customId, parentId, Y9EnumUtil.valueOf(ResourceTypeEnum.class, resourceType))
            .orElse(null);
    }

    /**
     * 获得指定资源的父资源
     *
     * @param resourceId 资源的唯一标识
     * @return Resource 父资源
     * @since 9.6.0
     */
    @Override
    public Resource getParentResource(@RequestParam("resourceId") @NotBlank String resourceId) {
        return compositeResourceService.findResourceParent(resourceId);
    }

    /**
     * 获得指定资源对象
     *
     * @param resourceId 资源唯一标示
     * @return Resource 资源对象
     * @since 9.6.0
     */
    @Override
    public Resource getResource(@RequestParam("resourceId") @NotBlank String resourceId) {
        return compositeResourceService.findById(resourceId);
    }

    /**
     * 根据系统标识获取该系统的资源树的顶级节点
     *
     * @param systemName 系统标识
     * @return Resource 资源节点
     * @since 9.6.0
     */
    @Override
    public Resource getRootResourceBySystemName(@RequestParam("systemName") @NotBlank String systemName) {
        Optional<System> y9SystemOptional = y9SystemService.findByName(systemName);
        if (y9SystemOptional.isPresent()) {
            Y9App app =
                y9AppRepository.findBySystemIdAndCustomId(y9SystemOptional.get().getId(), systemName).orElse(null);
            return PlatformModelConvertUtil.resourceBaseToResource(app);
        }
        return null;
    }

    /**
     * 获取指定资源的子菜单资源
     *
     * @param resourceId 资源id
     * @return {@code List<Resource>} 资源对象集合
     * @since 9.6.0
     */
    @Override
    public List<Menu> listSubMenus(@RequestParam("resourceId") @NotBlank String resourceId) {
        return y9MenuService.findByParentId(resourceId);
    }

    /**
     * 获得指定资源的子资源
     *
     * @param resourceId 资源唯一标识
     * @return {@code List<Resource>} 资源对象集合
     * @since 9.6.0
     */
    @Override
    public List<Resource> listSubResources(@RequestParam("resourceId") @NotBlank String resourceId) {
        return compositeResourceService.listByParentId(resourceId);
    }

}
