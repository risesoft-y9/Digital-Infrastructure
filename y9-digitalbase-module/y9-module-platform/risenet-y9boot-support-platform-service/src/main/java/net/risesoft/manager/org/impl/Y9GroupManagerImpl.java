package net.risesoft.manager.org.impl;

import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.entity.org.Y9Group;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.manager.org.Y9GroupManager;
import net.risesoft.repository.org.Y9GroupRepository;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;

@Service
@CacheConfig(cacheNames = CacheNameConsts.ORG_GROUP)
@RequiredArgsConstructor
public class Y9GroupManagerImpl implements Y9GroupManager {

    private final Y9GroupRepository y9GroupRepository;

    @Override
    @CacheEvict(key = "#y9Group.id")
    public void delete(Y9Group y9Group) {
        y9GroupRepository.delete(y9Group);

        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Group));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null")
    public Optional<Y9Group> findByIdFromCache(String id) {
        return y9GroupRepository.findById(id);
    }

    @Override
    public Optional<Y9Group> findById(String id) {
        return y9GroupRepository.findById(id);
    }

    @Override
    public Y9Group getById(String id) {
        return y9GroupRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.GROUP_NOT_FOUND, id));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Group getByIdFromCache(String id) {
        return this.getById(id);
    }

    @CacheEvict(key = "#y9Group.id")
    public Y9Group save(Y9Group y9Group) {
        return y9GroupRepository.save(y9Group);
    }

    @Override
    public Y9Group insert(Y9Group group) {
        Y9Group savedGroup = this.save(group);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedGroup));

        return savedGroup;
    }

    @Override
    public Y9Group update(Y9Group group, Y9Group originalGroup) {
        Y9Group savedGroup = this.save(group);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originalGroup, savedGroup));

        return savedGroup;
    }
}
