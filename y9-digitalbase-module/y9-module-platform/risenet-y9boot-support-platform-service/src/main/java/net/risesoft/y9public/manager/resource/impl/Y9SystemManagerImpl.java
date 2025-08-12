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
import net.risesoft.exception.SystemErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.manager.resource.Y9SystemManager;
import net.risesoft.y9public.repository.resource.Y9SystemRepository;

@Service
@CacheConfig(cacheNames = CacheNameConsts.SYSTEM)
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9SystemManagerImpl implements Y9SystemManager {

    private final Y9SystemRepository y9SystemRepository;

    @Override
    public Optional<Y9System> findByName(String systemName) {
        return y9SystemRepository.findByName(systemName);
    }

    @Transactional(readOnly = false)
    @Override
    public Y9System insert(Y9System y9System) {
        if (StringUtils.isBlank(y9System.getId())) {
            y9System.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        return y9SystemRepository.save(y9System);
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#y9System.id", condition = "#y9System.id!=null")
    public Y9System update(Y9System y9System) {
        return y9SystemRepository.save(y9System);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9System getById(String id) {
        return y9SystemRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(SystemErrorCodeEnum.SYSTEM_NOT_FOUND, id));
    }

    @Override
    public Y9System getByName(String systemName) {
        return y9SystemRepository.findByName(systemName)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(SystemErrorCodeEnum.SYSTEM_NOT_FOUND, systemName));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Optional<Y9System> findById(String id) {
        return y9SystemRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#id")
    public void delete(String id) {
        y9SystemRepository.deleteById(id);
    }
}
