package net.risesoft.manager.org.impl;

import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.entity.org.Y9Department;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.manager.org.Y9DepartmentManager;
import net.risesoft.repository.org.Y9DepartmentRepository;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;

@CacheConfig(cacheNames = CacheNameConsts.ORG_DEPARTMENT)
@Service
@RequiredArgsConstructor
public class Y9DepartmentManagerImpl implements Y9DepartmentManager {

    private final Y9DepartmentRepository y9DepartmentRepository;

    @Override
    @CacheEvict(key = "#y9Department.id")
    public void delete(Y9Department y9Department) {
        y9DepartmentRepository.delete(y9Department);

        // 发布事件，程序内部监听处理相关业务
        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Department));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Optional<Y9Department> findByIdFromCache(String id) {
        return y9DepartmentRepository.findById(id);
    }

    @Override
    public Optional<Y9Department> findById(String id) {
        return y9DepartmentRepository.findById(id);
    }

    @Override
    public Y9Department getById(String id) {
        return y9DepartmentRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.DEPARTMENT_NOT_FOUND, id));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id != null", unless = "#result == null")
    public Y9Department getByIdFromCache(String id) {
        return this.getById(id);
    }

    @Override
    public Y9Department insert(Y9Department y9Department) {
        final Y9Department savedDepartment = y9DepartmentRepository.save(y9Department);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedDepartment));

        return savedDepartment;
    }

    @Override
    @CacheEvict(key = "#y9Department.id")
    public Y9Department update(Y9Department y9Department, Y9Department originalDepartment) {
        final Y9Department savedDepartment = y9DepartmentRepository.save(y9Department);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originalDepartment, savedDepartment));

        return savedDepartment;
    }

}
