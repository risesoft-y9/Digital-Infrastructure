package net.risesoft.manager.org.impl;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.consts.InitDataConsts;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9DepartmentManager;
import net.risesoft.repository.Y9DepartmentRepository;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;

@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@CacheConfig(cacheNames = CacheNameConsts.ORG_DEPARTMENT)
@Service
@RequiredArgsConstructor
public class Y9DepartmentManagerImpl implements Y9DepartmentManager {

    private final Y9DepartmentRepository y9DepartmentRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;

    @Override
    @CacheEvict(key = "#y9Department.id")
    @Transactional(readOnly = false)
    public void delete(Y9Department y9Department) {
        y9DepartmentRepository.delete(y9Department);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Optional<Y9Department> findById(String id) {
        return y9DepartmentRepository.findById(id);
    }

    @Override
    public Optional<Y9Department> findByIdNotCache(String id) {
        return y9DepartmentRepository.findById(id);
    }

    @Override
    public Y9Department getByIdNotCache(String id) {
        return y9DepartmentRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.DEPARTMENT_NOT_FOUND, id));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id != null", unless = "#result == null")
    public Y9Department getById(String id) {
        return y9DepartmentRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.DEPARTMENT_NOT_FOUND, id));
    }

    @Override
    @CacheEvict(key = "#y9Department.id")
    @Transactional(readOnly = false)
    public Y9Department save(Y9Department y9Department) {
        return y9DepartmentRepository.save(y9Department);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Department saveOrUpdate(Y9Department dept) {
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(dept.getParentId());

        if (StringUtils.isNotEmpty(dept.getId())) {
            Optional<Y9Department> y9DepartmentOptional = this.findByIdNotCache(dept.getId());
            if (y9DepartmentOptional.isPresent()) {
                Y9Department originDepartment = new Y9Department();
                Y9Department updatedDepartment = y9DepartmentOptional.get();
                Y9BeanUtil.copyProperties(updatedDepartment, originDepartment);

                Y9BeanUtil.copyProperties(dept, updatedDepartment);

                updatedDepartment.setDn(Y9OrgUtil.buildDn(OrgTypeEnum.DEPARTMENT, dept.getName(), parent.getDn()));
                updatedDepartment.setGuidPath(Y9OrgUtil.buildGuidPath(parent.getGuidPath(), dept.getId()));

                final Y9Department savedDepartment = this.save(updatedDepartment);

                Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originDepartment, savedDepartment));

                return savedDepartment;
            }
        } else {
            dept.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        dept.setTenantId(Y9LoginUserHolder.getTenantId());
        dept.setVersion(InitDataConsts.Y9_VERSION);
        dept.setDisabled(false);
        dept.setTabIndex(compositeOrgBaseManager.getNextSubTabIndex(parent.getId()));
        dept.setDn(Y9OrgUtil.buildDn(OrgTypeEnum.DEPARTMENT, dept.getName(), parent.getDn()));
        dept.setParentId(parent.getId());
        dept.setGuidPath(Y9OrgUtil.buildGuidPath(parent.getGuidPath(), dept.getId()));

        final Y9Department savedDepartment = this.save(dept);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedDepartment));

        return savedDepartment;
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#id")
    public Y9Department saveProperties(String id, String properties) {
        final Y9Department department = this.getById(id);

        Y9Department updateDepartment = Y9ModelConvertUtil.convert(department, Y9Department.class);
        updateDepartment.setProperties(properties);
        return saveOrUpdate(updateDepartment);
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#id")
    public Y9Department updateTabIndex(String id, int tabIndex) {
        final Y9Department department = this.getById(id);

        Y9Department updatedDepartment = Y9ModelConvertUtil.convert(department, Y9Department.class);
        updatedDepartment.setTabIndex(tabIndex);
        return this.saveOrUpdate(updatedDepartment);
    }
}
