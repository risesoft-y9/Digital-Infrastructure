package net.risesoft.manager.org.impl;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.entity.Y9Group;
import net.risesoft.exception.GroupErrorCodeEnum;
import net.risesoft.manager.org.Y9GroupManager;
import net.risesoft.repository.Y9GroupRepository;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;

@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@CacheConfig(cacheNames = CacheNameConsts.ORG_GROUP)
@RequiredArgsConstructor
public class Y9GroupManagerImpl implements Y9GroupManager {
    
    private final Y9GroupRepository y9GroupRepository;
    
    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Group getById(String id) {
        return y9GroupRepository.findById(id).orElseThrow(() -> Y9ExceptionUtil.notFoundException(GroupErrorCodeEnum.GROUP_NOT_FOUND, id));
    }
}
