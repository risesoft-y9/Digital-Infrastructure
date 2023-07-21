package net.risesoft.y9public.manager.resource.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.risesoft.consts.CacheNameConsts;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.ResourceTypeEnum;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.entity.resource.Y9Operation;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.manager.resource.Y9ResourceBaseManager;
import net.risesoft.y9public.repository.resource.Y9AppRepository;
import net.risesoft.y9public.repository.resource.Y9MenuRepository;
import net.risesoft.y9public.repository.resource.Y9OperationRepository;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9ResourceBaseManagerImpl implements Y9ResourceBaseManager {

    private final Y9AppRepository y9AppRepository;
    private final Y9MenuRepository y9MenuRepository;
    private final Y9OperationRepository y9OperationRepository;

    @Cacheable(cacheNames = CacheNameConsts.RESOURCE_APP, key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9App findAppById(String id) {
        return y9AppRepository.findById(id).orElse(null);
    }

    @Cacheable(cacheNames = CacheNameConsts.RESOURCE_MENU, key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Menu findMenuById(String id) {
        return y9MenuRepository.findById(id).orElse(null);
    }

    @Cacheable(cacheNames = CacheNameConsts.RESOURCE_OPERATION, key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Operation findOperationById(String id) {
        return y9OperationRepository.findById(id).orElse(null);
    }

    @Override
    public Y9ResourceBase findByCustomIdAndParentId(String customId, String parentId, Integer resourceType) {
        if (ResourceTypeEnum.APP.getValue().equals(resourceType)) {
            return y9AppRepository.findBySystemIdAndCustomId(parentId, customId);
        } else if (ResourceTypeEnum.MENU.getValue().equals(resourceType)) {
            return y9MenuRepository.findByParentIdAndCustomId(parentId, customId);
        } else {
            return y9OperationRepository.findByParentIdAndCustomId(parentId, customId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Y9ResourceBase findById(String id) {
        Y9App y9App = this.findAppById(id);
        if (y9App != null) {
            return y9App;
        }
        Y9Menu y9Menu = this.findMenuById(id);
        if (y9Menu != null) {
            return y9Menu;
        }
        Y9Operation y9Operation = this.findOperationById(id);
        if (y9Operation != null) {
            return y9Operation;
        }
        return null;
    }

    @Override
    public Y9ResourceBase findByIdAndResourceType(String resourceId, Integer resourceType) {
        if (ResourceTypeEnum.APP.getValue().equals(resourceType)) {
            return this.findAppById(resourceId);
        } else if (ResourceTypeEnum.MENU.getValue().equals(resourceType)) {
            return this.findMenuById(resourceId);
        } else {
            return this.findOperationById(resourceId);
        }
    }

    @Override
    public List<Y9ResourceBase> listByParentId(String parentId) {
        List<Y9ResourceBase> y9ResourceBaseList = new ArrayList<>();
        y9ResourceBaseList.addAll(y9MenuRepository.findByParentIdOrderByTabIndex(parentId));
        y9ResourceBaseList.addAll(y9OperationRepository.findByParentIdOrderByTabIndex(parentId));
        return y9ResourceBaseList;
    }

    @Override
    public List<Y9ResourceBase> listChildrenById(String resourceId) {
        List<Y9ResourceBase> y9ResourceBaseList = new ArrayList<>();
        y9ResourceBaseList.addAll(y9MenuRepository.findByParentIdOrderByTabIndex(resourceId));
        y9ResourceBaseList.addAll(y9OperationRepository.findByParentIdOrderByTabIndex(resourceId));
        return y9ResourceBaseList;
    }

    @Override
    public List<Y9ResourceBase> listRootResourceBySystemId(String systemId) {
        return new ArrayList<>(y9AppRepository.findBySystemIdOrderByTabIndex(systemId));
    }

    @Override
    public List<Y9ResourceBase> listRootResourceList() {
        return new ArrayList<>(y9AppRepository.findAll(Sort.by("systemId", "tabIndex")));
    }

    private void recursionUpToRoot(Y9ResourceBase y9ResourceBase, List<Y9ResourceBase> returnList) {
        if (StringUtils.isEmpty(y9ResourceBase.getParentId())) {
            return;
        }
        Y9ResourceBase parent = this.findById(y9ResourceBase.getParentId());
        if (!returnList.contains(parent)) {
            returnList.add(parent);
        }
        recursionUpToRoot(parent, returnList);
    }

    @Override
    public List<Y9ResourceBase> searchByName(String name) {
        List<Y9ResourceBase> resourceList = new ArrayList<>();
        resourceList.addAll(y9AppRepository.findByNameContainingOrderByTabIndex(name));
        resourceList.addAll(y9MenuRepository.findByNameContainingOrderByTabIndex(name));
        resourceList.addAll(y9OperationRepository.findByNameContainingOrderByTabIndex(name));
        return resourceList;
    }

    @Override
    public List<Y9ResourceBase> treeSearch(String name) {
        List<Y9ResourceBase> resourceList = new ArrayList<>();
        resourceList.addAll(this.searchByName(name));
        List<Y9ResourceBase> returnList = new ArrayList<>(resourceList);
        for (Y9ResourceBase resource : resourceList) {
            recursionUpToRoot(resource, returnList);
        }
        return returnList;
    }
}
