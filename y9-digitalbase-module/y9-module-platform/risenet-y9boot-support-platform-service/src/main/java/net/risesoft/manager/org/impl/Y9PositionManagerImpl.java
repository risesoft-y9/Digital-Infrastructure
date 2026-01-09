package net.risesoft.manager.org.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9PositionManager;
import net.risesoft.repository.org.Y9PositionRepository;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;

@Service
@CacheConfig(cacheNames = CacheNameConsts.ORG_POSITION)
@RequiredArgsConstructor
public class Y9PositionManagerImpl implements Y9PositionManager {

    private final Y9PositionRepository y9PositionRepository;
    private final Y9PersonsToPositionsRepository y9PersonsToPositionsRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;

    @Override
    @CacheEvict(key = "#y9Position.id")
    public void delete(Y9Position y9Position) {
        y9PositionRepository.delete(y9Position);

        // 发布事件，程序内部监听处理相关业务
        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Position));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Optional<Y9Position> findByIdFromCache(String id) {
        return y9PositionRepository.findById(id);
    }

    @Override
    public Optional<Y9Position> findById(String id) {
        return y9PositionRepository.findById(id);
    }

    @Override
    public Y9Position getById(String id) {
        return y9PositionRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.POSITION_NOT_FOUND, id));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Position getByIdFromCache(String id) {
        return this.getById(id);
    }

    @Override
    @CacheEvict(key = "#position.id")
    public Y9Position save(Y9Position position) {
        return y9PositionRepository.save(position);
    }

    @Override
    public Y9Position insert(Y9Position position) {
        final Y9Position savedPosition = save(position);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedPosition));

        return savedPosition;
    }

    @CacheEvict(key = "#position.id")
    @Override
    public Y9Position update(Y9Position position, Y9Position originalPosition) {
        final Y9Position savedY9Position = save(position);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originalPosition, savedY9Position));

        return savedY9Position;
    }

    @Override
    public List<Y9Position> listByPersonId(String personId, Boolean disabled) {
        List<Y9PersonsToPositions> ppsList =
            y9PersonsToPositionsRepository.findByPersonIdOrderByPositionOrderAsc(personId);
        List<Y9Position> pList = new ArrayList<>();
        for (Y9PersonsToPositions pps : ppsList) {
            Y9Position y9Position = this.getByIdFromCache(pps.getPositionId());
            if (disabled == null || disabled.equals(y9Position.getDisabled())) {
                pList.add(y9Position);
            }
        }
        return pList;
    }
}
