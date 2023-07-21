package net.risesoft.api.resource;

import java.util.ArrayList;
import java.util.List;

import net.risesoft.y9.util.Y9ModelConvertUtil;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.ResourceTypeEnum;
import net.risesoft.model.Resource;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.manager.resource.Y9ResourceBaseManager;
import net.risesoft.y9public.repository.resource.Y9AppRepository;
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
@RestController
@RequestMapping(value = "/services/rest/resource", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ResourceApiImpl implements ResourceApi {

    private final Y9ResourceBaseManager y9ResourceBaseManager;
    private final Y9SystemService y9SystemService;
    private final Y9AppRepository y9AppRepository;
    private final Y9MenuService y9MenuService;

    /**
     * 创建菜单资源
     *
     * @param resourceId 资源id
     * @param resourceName 资源名称
     * @param parentResourceId 父资源id
     * @param customId 是否是菜单节点
     * @return Resource 新创建的资源对象
     * @since 9.6.0
     */
    @Override
    @PostMapping("/createMenuResource")
    public Resource createMenuResource(@RequestParam String resourceId, @RequestParam String resourceName, @RequestParam String parentResourceId, @RequestParam String customId) {
        Y9ResourceBase parentResoucre = y9ResourceBaseManager.findById(parentResourceId);
        Y9Menu y9Menu = y9MenuService.findById(resourceId);
        if (y9Menu == null) {
            y9Menu = new Y9Menu();
            y9Menu.setId(resourceId);
        }
        if (parentResoucre != null) {
            y9Menu.setAppId(parentResoucre.getAppId());
            y9Menu.setSystemId(parentResoucre.getSystemId());
        }
        y9Menu.setName(resourceName);
        y9Menu.setResourceType(ResourceTypeEnum.MENU.getValue());
        y9Menu.setInherit(Boolean.FALSE);
        y9Menu.setHidden(Boolean.FALSE);
        y9Menu.setEnabled(Boolean.TRUE);
        y9Menu = y9MenuService.saveOrUpdate(y9Menu);
        return ModelConvertUtil.resourceBaseToResource(y9Menu);
    }

    /**
     * 根据 customId 获取资源
     *
     * @param customId 自定义标识
     * @return Resource 资源对象
     * @since 9.6.0
     */
    @Override
    @GetMapping("/findByCustomId")
    public Resource findByCustomId(@RequestParam String customId) {
        return null;
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
    @GetMapping("/findByCustomIdAndParentId")
    public Resource findByCustomIdAndParentId(@RequestParam String customId, @RequestParam String parentId, @RequestParam Integer resourceType) {
        return ModelConvertUtil.resourceBaseToResource(y9ResourceBaseManager.findByCustomIdAndParentId(customId, parentId, resourceType));
    }

    /**
     * 获得指定资源的父资源
     *
     * @param resourceId 资源的唯一标识
     * @return Resource 父资源
     * @since 9.6.0
     */
    @Override
    @GetMapping("/getParentResource")
    public Resource getParentResource(@RequestParam String resourceId) {
        Y9ResourceBase acResource = y9ResourceBaseManager.findById(resourceId);
        if (acResource == null) {
            return null;
        }
        Y9ResourceBase parent = y9ResourceBaseManager.findById(acResource.getParentId());
        return ModelConvertUtil.resourceBaseToResource(parent);
    }

    /**
     * 获得指定资源对象
     *
     * @param resourceId 资源唯一标示
     * @return Resource 资源对象
     * @since 9.6.0
     */
    @Override
    @GetMapping("/getResource")
    public Resource getResource(@RequestParam String resourceId) {
        Y9ResourceBase acResource = y9ResourceBaseManager.findById(resourceId);
        return ModelConvertUtil.resourceBaseToResource(acResource);
    }

    /**
     * 根据系统标识获取该系统的资源树的顶级节点
     *
     * @param systemName 系统标识
     * @return Resource 资源节点
     * @since 9.6.0
     */
    @Override
    @GetMapping("/getRootResourceBySystemName")
    public Resource getRootResourceBySystemName(@RequestParam String systemName) {
        Y9System system = y9SystemService.findByName(systemName);
        if (system != null) {
            Y9App app = y9AppRepository.findBySystemIdAndCustomId(system.getId(), systemName);
            if (null != app) {
                return ModelConvertUtil.resourceBaseToResource(app);
            }
        }
        return null;
    }

    /**
     * 获取指定资源的子菜单资源
     *
     * @param resourceId 资源id
     * @return List&lt;Resource&gt; 资源对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listSubMenus")
    public List<Resource> listSubMenus(@RequestParam String resourceId) {
        List<Y9Menu> y9MenuList = y9MenuService.findByParentId(resourceId);
        return Y9ModelConvertUtil.convert(y9MenuList, Resource.class);
    }

    /**
     * 获得指定资源的子资源
     *
     * @param resourceId 资源唯一标识
     * @return List&lt;Resource&gt; 资源对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listSubResources")
    public List<Resource> listSubResources(@RequestParam String resourceId) {
        List<Y9ResourceBase> y9ResourceBaseList = y9ResourceBaseManager.listRootResourceBySystemId(resourceId);
        return Y9ModelConvertUtil.convert(y9ResourceBaseList, Resource.class);
    }

}
