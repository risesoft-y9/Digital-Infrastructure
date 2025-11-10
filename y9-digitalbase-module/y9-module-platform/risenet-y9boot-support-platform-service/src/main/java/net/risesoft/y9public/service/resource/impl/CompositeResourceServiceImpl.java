package net.risesoft.y9public.service.resource.impl;

import static net.risesoft.consts.JpaPublicConsts.PUBLIC_TRANSACTION_MANAGER;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.model.platform.resource.App;
import net.risesoft.model.platform.resource.Resource;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.manager.resource.CompositeResourceManager;
import net.risesoft.y9public.manager.resource.Y9AppManager;
import net.risesoft.y9public.manager.resource.Y9MenuManager;
import net.risesoft.y9public.manager.resource.Y9OperationManager;
import net.risesoft.y9public.repository.resource.Y9AppRepository;
import net.risesoft.y9public.repository.resource.Y9DataCatalogRepository;
import net.risesoft.y9public.repository.resource.Y9MenuRepository;
import net.risesoft.y9public.repository.resource.Y9OperationRepository;
import net.risesoft.y9public.service.resource.CompositeResourceService;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
public class CompositeResourceServiceImpl implements CompositeResourceService {

    private final Y9AppRepository y9AppRepository;
    private final Y9MenuRepository y9MenuRepository;
    private final Y9OperationRepository y9OperationRepository;
    private final Y9DataCatalogRepository y9DataCatalogRepository;

    private final Y9AppManager y9AppManager;
    private final Y9MenuManager y9MenuManager;
    private final Y9OperationManager y9OperationManager;

    private final CompositeResourceManager compositeResourceManager;

    @Transactional(value = PUBLIC_TRANSACTION_MANAGER, readOnly = true)
    @Override
    public Optional<Resource> findByCustomIdAndParentId(String customId, String parentId,
        ResourceTypeEnum resourceType) {
        if (ResourceTypeEnum.APP.equals(resourceType)) {
            return y9AppRepository.findBySystemIdAndCustomId(parentId, customId)
                .map(PlatformModelConvertUtil::y9AppToApp);
        } else if (ResourceTypeEnum.MENU.equals(resourceType)) {
            return y9MenuRepository.findByParentIdAndCustomId(parentId, customId)
                .map(PlatformModelConvertUtil::y9MenuToMenu);
        } else {
            return y9OperationRepository.findByParentIdAndCustomId(parentId, customId)
                .map(PlatformModelConvertUtil::y9OperationToOperation);
        }
    }

    @Override
    public Resource findById(String id) {
        return compositeResourceManager.findResource(id)
            .map(PlatformModelConvertUtil::resourceBaseToResource)
            .orElse(null);
    }

    @Override
    public List<Resource> listByParentId(String parentId) {
        return PlatformModelConvertUtil.resourceBaseToResource(compositeResourceManager.listByParentId(parentId));
    }

    @Override
    public List<App> listRootResourceBySystemId(String systemId) {
        return PlatformModelConvertUtil.y9AppToApp(y9AppRepository.findBySystemIdOrderByTabIndex(systemId));
    }

    @Override
    public List<App> listRootResourceList() {
        List<Y9App> y9AppList = y9AppRepository.findAll(Sort.by("systemId", "tabIndex"));
        return PlatformModelConvertUtil.y9AppToApp(y9AppList);
    }

    private List<Resource> search(String name) {
        List<Y9ResourceBase> resourceList = new ArrayList<>();
        resourceList.addAll(y9AppRepository.findByNameContainingOrderByTabIndex(name));
        resourceList.addAll(y9MenuRepository.findByNameContainingOrderByTabIndex(name));
        resourceList.addAll(y9OperationRepository.findByNameContainingOrderByTabIndex(name));
        return PlatformModelConvertUtil.resourceBaseToResource(resourceList);
    }

    @Transactional(value = PUBLIC_TRANSACTION_MANAGER, readOnly = true)
    @Override
    public List<Resource> treeSearch(String name) {
        List<Resource> resourceList = this.search(name);
        Set<Resource> returnSet = new HashSet<>(resourceList);
        for (Resource resource : resourceList) {
            fillResourcesRecursivelyToRoot(resource, returnSet);
        }
        return returnSet.stream().sorted().collect(Collectors.toList());
    }

    @Transactional(value = PUBLIC_TRANSACTION_MANAGER, readOnly = true)
    @Override
    public List<Resource> findByIdIn(List<String> resourceIdList) {
        List<Resource> y9ResourceList = new ArrayList<>();
        for (String resourceId : resourceIdList) {
            Resource y9ResourceBase = this.findById(resourceId);
            if (y9ResourceBase != null) {
                y9ResourceList.add(y9ResourceBase);
            }
        }
        return y9ResourceList;
    }

    @Override
    public Resource findResourceParent(String resourceId) {
        Resource resource = findById(resourceId);
        if (resource != null) {
            return findResourceParent(resource.getId());
        }
        return null;
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void sort(String[] ids) {
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                String id = ids[i];
                Resource y9ResourceBase = this.findById(id);
                if (ResourceTypeEnum.APP.equals(y9ResourceBase.getResourceType())) {
                    y9AppManager.updateTabIndex(id, i);
                } else if (ResourceTypeEnum.MENU.equals(y9ResourceBase.getResourceType())) {
                    y9MenuManager.updateTabIndex(id, i);
                } else if (ResourceTypeEnum.OPERATION.equals(y9ResourceBase.getResourceType())) {
                    y9OperationManager.updateTabIndex(id, i);
                }
            }
        }
    }

    private void fillResourcesRecursivelyToRoot(Resource y9ResourceBase, Set<Resource> resourceSet) {
        if (StringUtils.isEmpty(y9ResourceBase.getParentId())) {
            return;
        }
        Resource parent = this.findById(y9ResourceBase.getParentId());
        if (parent == null) {
            return;
        }
        resourceSet.add(parent);
        fillResourcesRecursivelyToRoot(parent, resourceSet);
    }
}
