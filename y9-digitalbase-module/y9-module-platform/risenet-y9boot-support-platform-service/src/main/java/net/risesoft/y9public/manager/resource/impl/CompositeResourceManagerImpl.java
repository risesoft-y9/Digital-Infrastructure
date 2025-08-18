package net.risesoft.y9public.manager.resource.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.exception.ResourceErrorCodeEnum;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9DataCatalog;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.entity.resource.Y9Operation;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.manager.resource.CompositeResourceManager;
import net.risesoft.y9public.repository.resource.Y9AppRepository;
import net.risesoft.y9public.repository.resource.Y9DataCatalogRepository;
import net.risesoft.y9public.repository.resource.Y9MenuRepository;
import net.risesoft.y9public.repository.resource.Y9OperationRepository;

/**
 * CompositeResourceManagerImpl
 * 
 * @author shidaobang
 * @date 2024/12/12
 * @since 9.6.8
 */
@Service
@RequiredArgsConstructor
public class CompositeResourceManagerImpl implements CompositeResourceManager {

    private final Y9AppRepository y9AppRepository;
    private final Y9MenuRepository y9MenuRepository;
    private final Y9OperationRepository y9OperationRepository;
    private final Y9DataCatalogRepository y9DataCatalogRepository;

    @Cacheable(cacheNames = CacheNameConsts.RESOURCE_MENU, key = "#id", condition = "#id!=null",
        unless = "#result==null")
    @Override
    public Y9Menu findMenuById(String id) {
        return y9MenuRepository.findById(id).orElse(null);
    }

    @Cacheable(cacheNames = CacheNameConsts.RESOURCE_OPERATION, key = "#id", condition = "#id!=null",
        unless = "#result==null")
    @Override
    public Y9Operation findOperationById(String id) {
        return y9OperationRepository.findById(id).orElse(null);
    }

    @Cacheable(cacheNames = CacheNameConsts.RESOURCE_APP, key = "#id", condition = "#id!=null",
        unless = "#result==null")
    @Override
    public Y9App findAppById(String id) {
        return y9AppRepository.findById(id).orElse(null);
    }

    @Cacheable(cacheNames = CacheNameConsts.RESOURCE_DATA_CATALOG, key = "#id", condition = "#id!=null",
        unless = "#result==null")
    @Override
    public Y9DataCatalog findDataCatalogById(String id) {
        return y9DataCatalogRepository.findById(id).orElse(null);
    }

    @Override
    public Y9ResourceBase getById(String id) {
        return this.findResource(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(ResourceErrorCodeEnum.RESOURCE_NOT_FOUND, id));
    }

    @Override
    public Y9ResourceBase getResourceAsParent(String resourceId) {
        return this.findResource(resourceId)
            .orElseThrow(
                () -> Y9ExceptionUtil.notFoundException(ResourceErrorCodeEnum.RESOURCE_PARENT_NOT_FOUND, resourceId));
    }

    @Override
    public Optional<Y9ResourceBase> findResource(String id) {
        Y9App y9App = this.findAppById(id);
        if (y9App != null) {
            return Optional.of(y9App);
        }
        Y9Menu y9Menu = this.findMenuById(id);
        if (y9Menu != null) {
            return Optional.of(y9Menu);
        }
        Y9Operation y9Operation = this.findOperationById(id);
        if (y9Operation != null) {
            return Optional.of(y9Operation);
        }

        Y9DataCatalog y9DataCatalog = this.findDataCatalogById(id);
        if (y9DataCatalog != null) {
            return Optional.of(y9DataCatalog);
        }
        return Optional.empty();
    }

    @Override
    public List<Y9ResourceBase> findByCustomId(String customId) {
        List<Y9ResourceBase> y9ResourceBaseList = new ArrayList<>();
        y9ResourceBaseList.addAll(y9AppRepository.findByCustomId(customId));
        y9ResourceBaseList.addAll(y9MenuRepository.findByCustomId(customId));
        y9ResourceBaseList.addAll(y9OperationRepository.findByCustomId(customId));
        return y9ResourceBaseList;
    }

    @Override
    public Y9ResourceBase findByIdAndResourceType(String resourceId, ResourceTypeEnum resourceType) {
        if (ResourceTypeEnum.APP.equals(resourceType)) {
            return this.findAppById(resourceId);
        } else if (ResourceTypeEnum.MENU.equals(resourceType)) {
            return this.findMenuById(resourceId);
        } else if (ResourceTypeEnum.OPERATION.equals(resourceType)) {
            return this.findOperationById(resourceId);
        } else {
            return this.findDataCatalogById(resourceId);
        }
    }

    @Override
    public List<Y9ResourceBase> listByParentId(String parentId) {
        List<Y9ResourceBase> y9ResourceBaseList = new ArrayList<>();
        y9ResourceBaseList.addAll(y9MenuRepository.findByParentIdOrderByTabIndex(parentId));
        y9ResourceBaseList.addAll(y9OperationRepository.findByParentIdOrderByTabIndex(parentId));
        y9ResourceBaseList.addAll(y9DataCatalogRepository.findByParentIdOrderByTabIndex(parentId));
        return y9ResourceBaseList;
    }

}
