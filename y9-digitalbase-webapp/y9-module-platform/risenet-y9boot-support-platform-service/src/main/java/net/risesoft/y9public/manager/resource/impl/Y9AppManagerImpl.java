package net.risesoft.y9public.manager.resource.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.exception.ResourceErrorCodeEnum;
import net.risesoft.repository.identity.person.Y9PersonToResourceAndAuthorityRepository;
import net.risesoft.repository.identity.position.Y9PositionToResourceAndAuthorityRepository;
import net.risesoft.repository.permission.Y9AuthorizationRepository;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.tenant.Y9TenantApp;
import net.risesoft.y9public.manager.resource.Y9AppManager;
import net.risesoft.y9public.repository.resource.Y9AppRepository;
import net.risesoft.y9public.repository.tenant.Y9TenantAppRepository;

/**
 * 应用 Manager 实现类
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
@Service
@CacheConfig(cacheNames = CacheNameConsts.RESOURCE_APP)
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9AppManagerImpl implements Y9AppManager {

    private final Y9AppRepository y9AppRepository;
    private final Y9AuthorizationRepository y9AuthorizationRepository;
    private final Y9PersonToResourceAndAuthorityRepository y9PersonToResourceAndAuthorityRepository;
    private final Y9PositionToResourceAndAuthorityRepository y9PositionToResourceAndAuthorityRepository;
    private final Y9TenantAppRepository y9TenantAppRepository;

    @Override
    @Transactional(readOnly = false)
    public void deleteBySystemId(String systemId) {
        List<Y9App> appList = y9AppRepository.findBySystemId(systemId);
        for (Y9App app : appList) {
            this.delete(app.getId());
        }
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#id")
    public void delete(String id) {
        Y9App y9App = this.getById(id);
        y9AppRepository.delete(y9App);

        // 删除租户关联数据
        List<Y9TenantApp> y9TenantAppList = y9TenantAppRepository.findByTenantIdAndTenancy(id, true);
        for (Y9TenantApp y9TenantApp : y9TenantAppList) {
            Y9LoginUserHolder.setTenantId(y9TenantApp.getTenantId());
            this.deleteTenantRelatedByAppId(id);
        }

        y9TenantAppRepository.deleteAll(y9TenantAppList);
        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9App));
    }

    /**
     * 删除相关租户数据 <br/>
     * 切换不同的数据源 需开启新事务
     *
     * @param appId 应用id
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void deleteTenantRelatedByAppId(String appId) {
        y9AuthorizationRepository.deleteByResourceId(appId);
        y9PersonToResourceAndAuthorityRepository.deleteByResourceId(appId);
        y9PositionToResourceAndAuthorityRepository.deleteByResourceId(appId);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9App getById(String id) {
        return y9AppRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(ResourceErrorCodeEnum.APP_NOT_FOUND, id));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Optional<Y9App> findById(String id) {
        return y9AppRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#y9App.id")
    public Y9App save(Y9App y9App) {
        return y9AppRepository.save(y9App);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9App updateTabIndex(String id, int index) {
        Y9App y9App = this.getById(id);
        y9App.setTabIndex(index);
        return this.save(y9App);
    }

}
