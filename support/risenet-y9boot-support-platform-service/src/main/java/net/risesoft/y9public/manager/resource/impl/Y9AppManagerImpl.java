package net.risesoft.y9public.manager.resource.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.exception.AppErrorCodeEnum;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.manager.resource.Y9AppManager;
import net.risesoft.y9public.manager.role.Y9RoleManager;
import net.risesoft.y9public.manager.tenant.Y9TenantAppManager;
import net.risesoft.y9public.repository.resource.Y9AppRepository;

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

    private final Y9TenantAppManager y9TenantAppManager;
    private final Y9RoleManager y9RoleManager;

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
        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9App));

        y9RoleManager.deleteByApp(id);

        y9AppRepository.delete(y9App);

        y9TenantAppManager.deleteByAppId(id);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9App getById(String id) {
        return y9AppRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(AppErrorCodeEnum.APP_NOT_FOUND, id));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9App findById(String id) {
        return y9AppRepository.findById(id).orElse(null);
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
