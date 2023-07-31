package net.risesoft.y9public.manager.resource.impl;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.exception.OperationErrorCodeEnum;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9public.entity.resource.Y9Operation;
import net.risesoft.y9public.manager.resource.Y9OperationManager;
import net.risesoft.y9public.repository.resource.Y9OperationRepository;

/**
 * 按钮 manager 实现类
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
    
    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Operation findById(String id) {
        return y9OperationRepository.findById(id).orElse(null);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Operation getById(String id) {
        return y9OperationRepository.findById(id).orElseThrow(() -> Y9ExceptionUtil.notFoundException(OperationErrorCodeEnum.OPERATION_NOT_FOUND, id));
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#y9Operation.id", condition = "#y9Operation.id!=null")
    public Y9Operation save(Y9Operation y9Operation) {
        return y9OperationRepository.save(y9Operation);
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
        Y9Operation y9Operation = this.getById(id);
        y9Operation.setTabIndex(index);
        return this.save(y9Operation);
    }

}
