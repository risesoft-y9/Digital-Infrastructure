package net.risesoft.y9public.manager.resource.impl;

import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.exception.ResourceErrorCodeEnum;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9public.entity.resource.Y9Operation;
import net.risesoft.y9public.manager.resource.Y9OperationManager;
import net.risesoft.y9public.repository.resource.Y9OperationRepository;

/**
 * 按钮 manager 实现类
 * 
 * @author shidaobang
 * @date 2023/07/26
 * @since 9.6.3
 */
@Service
@CacheConfig(cacheNames = CacheNameConsts.RESOURCE_OPERATION)
@RequiredArgsConstructor
public class Y9OperationManagerImpl implements Y9OperationManager {

    private final Y9OperationRepository y9OperationRepository;

    @Override
    public Optional<Y9Operation> findById(String id) {
        return y9OperationRepository.findById(id);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Optional<Y9Operation> findByIdFromCache(String id) {
        return y9OperationRepository.findById(id);
    }

    @Override
    public Y9Operation getById(String id) {
        return y9OperationRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(ResourceErrorCodeEnum.OPERATION_NOT_FOUND, id));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Operation getByIdFromCache(String id) {
        return this.getById(id);
    }

    @Override
    public Y9Operation insert(Y9Operation y9Operation) {
        Y9Operation savedOperation = y9OperationRepository.save(y9Operation);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedOperation));

        return savedOperation;
    }

    @Override
    @CacheEvict(key = "#y9Operation.id", condition = "#y9Operation.id!=null")
    public Y9Operation update(Y9Operation y9Operation, Y9Operation originalOperation) {
        Y9Operation savedOperation = y9OperationRepository.save(y9Operation);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originalOperation, savedOperation));

        return savedOperation;
    }

    @Override
    @CacheEvict(key = "#y9Operation.id", condition = "#y9Operation.id!=null")
    public void delete(Y9Operation y9Operation) {
        y9OperationRepository.delete(y9Operation);
    }

    @Override
    @CacheEvict(key = "#id", condition = "#id!=null")
    public Y9Operation updateTabIndex(String id, int index) {
        Y9Operation currentOperation = this.getById(id);
        Y9Operation originalOperation = PlatformModelConvertUtil.convert(currentOperation, Y9Operation.class);

        currentOperation.changeTabIndex(index);
        return this.update(currentOperation, originalOperation);
    }

}
