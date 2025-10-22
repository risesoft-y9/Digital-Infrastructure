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
    public Optional<? extends Y9ResourceBase> findByCustomIdAndParentId(String customId, String parentId,
        ResourceTypeEnum resourceType) {
        if (ResourceTypeEnum.APP.equals(resourceType)) {
            return y9AppRepository.findBySystemIdAndCustomId(parentId, customId);
        } else if (ResourceTypeEnum.MENU.equals(resourceType)) {
            return y9MenuRepository.findByParentIdAndCustomId(parentId, customId);
        } else {
            return y9OperationRepository.findByParentIdAndCustomId(parentId, customId);
        }
    }

    @Override
    public Y9ResourceBase findById(String id) {
        return compositeResourceManager.findResource(id).orElse(null);
    }

    @Override
    public List<Y9ResourceBase> listByParentId(String parentId) {
        return compositeResourceManager.listByParentId(parentId);
    }

    @Override
    public List<Y9ResourceBase> listRootResourceBySystemId(String systemId) {
        return new ArrayList<>(y9AppRepository.findBySystemIdOrderByTabIndex(systemId));
    }

    @Override
    public List<Y9App> listRootResourceList() {
        return y9AppRepository.findAll(Sort.by("systemId", "tabIndex"));
    }

    @Transactional(value = PUBLIC_TRANSACTION_MANAGER, readOnly = true)
    @Override
    public List<Y9ResourceBase> searchByName(String name) {
        List<Y9ResourceBase> resourceList = new ArrayList<>();
        resourceList.addAll(y9AppRepository.findByNameContainingOrderByTabIndex(name));
        resourceList.addAll(y9MenuRepository.findByNameContainingOrderByTabIndex(name));
        resourceList.addAll(y9OperationRepository.findByNameContainingOrderByTabIndex(name));
        return resourceList;
    }

    @Transactional(value = PUBLIC_TRANSACTION_MANAGER, readOnly = true)
    @Override
    public List<Y9ResourceBase> treeSearch(String name) {
        List<Y9ResourceBase> resourceList = this.searchByName(name);
        Set<Y9ResourceBase> returnSet = new HashSet<>(resourceList);
        for (Y9ResourceBase resource : resourceList) {
            fillResourcesRecursivelyToRoot(resource, returnSet);
        }
        return returnSet.stream().sorted().collect(Collectors.toList());
    }

    @Transactional(value = PUBLIC_TRANSACTION_MANAGER, readOnly = true)
    @Override
    public List<Y9ResourceBase> findByIdIn(List<String> resourceIdList) {
        List<Y9ResourceBase> y9ResourceList = new ArrayList<>();
        for (String resourceId : resourceIdList) {
            Y9ResourceBase y9ResourceBase = this.findById(resourceId);
            if (y9ResourceBase != null) {
                y9ResourceList.add(y9ResourceBase);
            }
        }
        return y9ResourceList;
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void sort(String[] ids) {
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                String id = ids[i];
                Y9ResourceBase y9ResourceBase = this.findById(id);
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

    private void fillResourcesRecursivelyToRoot(Y9ResourceBase y9ResourceBase, Set<Y9ResourceBase> resourceSet) {
        if (StringUtils.isEmpty(y9ResourceBase.getParentId())) {
            return;
        }
        Y9ResourceBase parent = this.findById(y9ResourceBase.getParentId());
        if (parent == null) {
            return;
        }
        resourceSet.add(parent);
        fillResourcesRecursivelyToRoot(parent, resourceSet);
    }
}
