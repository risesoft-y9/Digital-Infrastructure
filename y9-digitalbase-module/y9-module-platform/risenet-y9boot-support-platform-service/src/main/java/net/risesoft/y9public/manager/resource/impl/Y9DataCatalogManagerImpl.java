package net.risesoft.y9public.manager.resource.impl;

import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.exception.ResourceErrorCodeEnum;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9public.entity.resource.Y9DataCatalog;
import net.risesoft.y9public.manager.resource.Y9DataCatalogManager;
import net.risesoft.y9public.repository.resource.Y9DataCatalogRepository;

/**
 * 数据目录 Manager 实现类
 *
 * @author shidaobang
 * @date 2025/08/28
 */
@Service
@CacheConfig(cacheNames = CacheNameConsts.RESOURCE_DATA_CATALOG)
@RequiredArgsConstructor
public class Y9DataCatalogManagerImpl implements Y9DataCatalogManager {

    private final Y9DataCatalogRepository y9DataCatalogRepository;

    @Override
    public Optional<Y9DataCatalog> findById(String id) {
        return y9DataCatalogRepository.findById(id);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Optional<Y9DataCatalog> findByIdFromCache(String id) {
        return y9DataCatalogRepository.findById(id);
    }

    @Override
    public Y9DataCatalog getById(String id) {
        return y9DataCatalogRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(ResourceErrorCodeEnum.DATA_CATALOG_NOT_FOUND, id));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9DataCatalog getByIdFromCache(String id) {
        return this.getById(id);
    }

    @Override
    public Y9DataCatalog insert(Y9DataCatalog y9DataCatalog) {
        return y9DataCatalogRepository.save(y9DataCatalog);
    }

    @Override
    @CacheEvict(key = "#y9DataCatalog.id", condition = "#y9DataCatalog.id!=null")
    public Y9DataCatalog update(Y9DataCatalog y9DataCatalog) {
        return y9DataCatalogRepository.save(y9DataCatalog);
    }
}
