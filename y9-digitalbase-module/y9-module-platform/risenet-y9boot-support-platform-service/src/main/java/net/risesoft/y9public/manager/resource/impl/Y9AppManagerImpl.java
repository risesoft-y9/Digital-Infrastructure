package net.risesoft.y9public.manager.resource.impl;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.exception.ResourceErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.manager.resource.Y9AppManager;
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
@RequiredArgsConstructor
@Slf4j
public class Y9AppManagerImpl implements Y9AppManager {

    private final Y9AppRepository y9AppRepository;

    @Override
    public void deleteBySystemId(String systemId) {
        List<Y9App> appList = y9AppRepository.findBySystemId(systemId);
        for (Y9App app : appList) {
            this.delete(app.getId());
        }
    }

    @Override
    @CacheEvict(key = "#id")
    public void delete(String id) {
        Y9App y9App = this.getByIdFromCache(id);
        y9AppRepository.delete(y9App);

        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9App));
    }

    @Override
    public Optional<Y9App> findById(String id) {
        return y9AppRepository.findById(id);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Optional<Y9App> findByIdFromCache(String id) {
        return y9AppRepository.findById(id);
    }

    @Override
    public Y9App getById(String id) {
        return y9AppRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(ResourceErrorCodeEnum.APP_NOT_FOUND, id));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9App getByIdFromCache(String id) {
        return this.getById(id);
    }

    @Override
    public Y9App insert(Y9App y9App) {
        if (StringUtils.isBlank(y9App.getId())) {
            y9App.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        y9App.setGuidPath(Y9OrgUtil.buildGuidPath(null, y9App.getId()));
        Y9App savedApp = y9AppRepository.save(y9App);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedApp));

        return savedApp;
    }

    @Override
    @CacheEvict(key = "#y9App.id")
    public Y9App update(Y9App y9App) {
        Y9App currentApp = this.getById(y9App.getId());
        Y9App originalApp = new Y9App();
        Y9BeanUtil.copyProperties(currentApp, originalApp);
        Y9BeanUtil.copyProperties(y9App, currentApp);

        currentApp.setGuidPath(Y9OrgUtil.buildGuidPath(null, currentApp.getId()));
        Y9App savedApp = y9AppRepository.save(currentApp);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originalApp, savedApp));

        return savedApp;
    }

    @Override
    public Y9App updateTabIndex(String id, int index) {
        Y9App y9App = this.getById(id);
        y9App.setTabIndex(index);
        return this.update(y9App);
    }

}
