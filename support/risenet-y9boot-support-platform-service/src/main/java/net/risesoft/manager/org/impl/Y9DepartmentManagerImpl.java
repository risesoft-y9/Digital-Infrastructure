package net.risesoft.manager.org.impl;

import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.entity.Y9Department;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.manager.org.Y9DepartmentManager;
import net.risesoft.model.Department;
import net.risesoft.repository.Y9DepartmentRepository;
import net.risesoft.util.Y9PublishServiceUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;
import net.risesoft.y9.util.Y9ModelConvertUtil;

@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@CacheConfig(cacheNames = CacheNameConsts.ORG_DEPARTMENT)
@Service
@RequiredArgsConstructor
public class Y9DepartmentManagerImpl implements Y9DepartmentManager {

    private final Y9DepartmentRepository y9DepartmentRepository;

    @Override
    @Cacheable(key = "#id", condition = "#id != null", unless = "#result == null")
    public Y9Department getById(String id) {
        return y9DepartmentRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.DEPARTMENT_NOT_FOUND, id));
    }

    @Override
    @CacheEvict(key = "#y9Department.id")
    @Transactional(readOnly = false)
    public void delete(Y9Department y9Department) {
        y9DepartmentRepository.delete(y9Department);
    }

    @Override
    @CacheEvict(key = "#y9Department.id")
    @Transactional(readOnly = false)
    public Y9Department save(Y9Department y9Department) {
        return y9DepartmentRepository.save(y9Department);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Optional<Y9Department> findById(String id) {
        return y9DepartmentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Department updateTabIndex(String id, int tabIndex) {
        Y9Department department = this.getById(id);
        department.setTabIndex(tabIndex);
        department = this.save(department);

        Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(department, Department.class),
            Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_DEPARTMENT_TABINDEX, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "更新部门排序号", department.getName() + "的排序号更新为" + tabIndex);

        return department;
    }
}
