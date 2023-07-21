package net.risesoft.manager.org.impl;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.entity.Y9Department;
import net.risesoft.exception.DepartmentErrorCodeEnum;
import net.risesoft.manager.org.Y9DepartmentManager;
import net.risesoft.repository.Y9DepartmentRepository;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;

@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@CacheConfig(cacheNames = CacheNameConsts.ORG_DEPARTMENT)
@Service
@RequiredArgsConstructor
public class Y9DepartmentManagerImpl implements Y9DepartmentManager {

    private final Y9DepartmentRepository y9DepartmentRepository;

    @Override
    @Cacheable(key = "#id", condition = "#id != null", unless = "#result == null")
    public Y9Department getById(String id) {
        return y9DepartmentRepository.findById(id).orElseThrow(() -> Y9ExceptionUtil.notFoundException(DepartmentErrorCodeEnum.DEPARTMENT_NOT_FOUND, id));
    }
    
}
