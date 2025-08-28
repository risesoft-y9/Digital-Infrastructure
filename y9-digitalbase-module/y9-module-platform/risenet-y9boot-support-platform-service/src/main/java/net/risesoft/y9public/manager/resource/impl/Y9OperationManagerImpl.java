package net.risesoft.y9public.manager.resource.impl;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.exception.ResourceErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.entity.resource.Y9Operation;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.manager.resource.CompositeResourceManager;
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
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9OperationManagerImpl implements Y9OperationManager {

    private final Y9OperationRepository y9OperationRepository;

    private final CompositeResourceManager compositeResourceManager;

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

    @Transactional(readOnly = false)
    @Override
    public Y9Operation insert(Y9Operation y9Operation) {
        Y9ResourceBase parent = compositeResourceManager.getResourceAsParent(y9Operation.getParentId());

        if (StringUtils.isBlank(y9Operation.getId())) {
            y9Operation.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        y9Operation.setGuidPath(Y9OrgUtil.buildGuidPath(parent.getGuidPath(), y9Operation.getId()));
        Integer tabIndex = getNextIndexByParentId(y9Operation.getParentId());
        y9Operation.setTabIndex(tabIndex);
        Y9Operation savedOperation = y9OperationRepository.save(y9Operation);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedOperation));

        return savedOperation;
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#y9Operation.id", condition = "#y9Operation.id!=null")
    public Y9Operation update(Y9Operation y9Operation) {
        Y9ResourceBase parent = compositeResourceManager.getResourceAsParent(y9Operation.getParentId());

        Y9Operation currentOperation = this.getByIdFromCache(y9Operation.getId());
        Y9Operation originalOperation = new Y9Operation();
        Y9BeanUtil.copyProperties(currentOperation, originalOperation);
        Y9BeanUtil.copyProperties(y9Operation, currentOperation);

        currentOperation.setGuidPath(Y9OrgUtil.buildGuidPath(parent.getGuidPath(), currentOperation.getId()));
        Y9Operation savedOperation = y9OperationRepository.save(currentOperation);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originalOperation, savedOperation));

        return savedOperation;
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#y9Operation.id", condition = "#y9Operation.id!=null")
    public void delete(Y9Operation y9Operation) {
        y9OperationRepository.delete(y9Operation);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Operation updateTabIndex(String id, int index) {
        Y9Operation y9Operation = this.getByIdFromCache(id);
        y9Operation.setTabIndex(index);
        return this.update(y9Operation);
    }

    private Integer getNextIndexByParentId(String parentId) {
        return y9OperationRepository.findTopByParentIdOrderByTabIndexDesc(parentId)
            .map(Y9Operation::getTabIndex)
            .orElse(0) + 1;
    }

}
