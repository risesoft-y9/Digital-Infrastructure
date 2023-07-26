package net.risesoft.manager.org.impl;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.entity.Y9Organization;
import net.risesoft.exception.OrganizationErrorCodeEnum;
import net.risesoft.manager.org.Y9OrganizationManager;
import net.risesoft.repository.Y9OrganizationRepository;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;

/**
 * 组织 manager 实现类
 * 
 * @author shidaobang
 * @date 2023/07/26
 * @since 9.6.3
 */
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@CacheConfig(cacheNames = CacheNameConsts.ORG_ORGANIZATION)
@RequiredArgsConstructor
public class Y9OrganizationManagerImpl implements Y9OrganizationManager {
    
    private final Y9OrganizationRepository y9OrganizationRepository;
    
    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#y9Organization.id", condition = "#y9Organization.id!=null")
    public Y9Organization save(Y9Organization y9Organization) {
        return y9OrganizationRepository.save(y9Organization);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Organization findById(String id) {
        return y9OrganizationRepository.findById(id).orElse(null);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Organization getById(String id) {
        return y9OrganizationRepository.findById(id).orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrganizationErrorCodeEnum.ORGANIZATION_NOT_FOUND, id));
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#y9Organization.id", condition = "#y9Organization.id!=null")
    public void delete(Y9Organization y9Organization) {
        y9OrganizationRepository.delete(y9Organization);
    }
}
