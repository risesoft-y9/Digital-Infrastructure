package net.risesoft.manager.org.impl;

import java.util.Optional;

import net.risesoft.util.PlatformModelConvertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.consts.DefaultConsts;
import net.risesoft.consts.InitDataConsts;
import net.risesoft.entity.org.Y9Department;
import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9DepartmentManager;
import net.risesoft.repository.org.Y9DepartmentRepository;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;

@CacheConfig(cacheNames = CacheNameConsts.ORG_DEPARTMENT)
@Service
@RequiredArgsConstructor
public class Y9DepartmentManagerImpl implements Y9DepartmentManager {

    private final Y9DepartmentRepository y9DepartmentRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;

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
    public Y9Department insert(Y9Department dept) {
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(dept.getParentId());

        if (StringUtils.isBlank(dept.getId())) {
            dept.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            dept.setDisabled(false);
            dept.setTabIndex((null == dept.getTabIndex() || DefaultConsts.TAB_INDEX.equals(dept.getTabIndex()))
                ? compositeOrgBaseManager.getNextSubTabIndex(parent.getId()) : dept.getTabIndex());
        }
        dept.setTenantId(Y9LoginUserHolder.getTenantId());
        dept.setVersion(InitDataConsts.Y9_VERSION);
        dept.setDn(Y9OrgUtil.buildDn(OrgTypeEnum.DEPARTMENT, dept.getName(), parent.getDn()));
        dept.setParentId(parent.getId());
        dept.setGuidPath(Y9OrgUtil.buildGuidPath(parent.getGuidPath(), dept.getId()));

        final Y9Department savedDepartment = y9DepartmentRepository.save(dept);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedDepartment));

        return savedDepartment;
    }

    @Override
    @CacheEvict(key = "#dept.id")
    public Y9Department update(Y9Department dept) {
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(dept.getParentId());
        Y9Department currentDepartment = this.getById(dept.getId());
        Y9Department originalDepartment = PlatformModelConvertUtil.convert(currentDepartment, Y9Department.class);

        Y9BeanUtil.copyProperties(dept, currentDepartment, "tenantId");
        currentDepartment.setDn(Y9OrgUtil.buildDn(OrgTypeEnum.DEPARTMENT, dept.getName(), parent.getDn()));
        currentDepartment.setGuidPath(Y9OrgUtil.buildGuidPath(parent.getGuidPath(), dept.getId()));

        final Y9Department savedDepartment = y9DepartmentRepository.save(currentDepartment);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originalDepartment, savedDepartment));

        return savedDepartment;
    }

    @Override
    @CacheEvict(key = "#id")
    public Y9Department updateTabIndex(String id, int tabIndex) {
        final Y9Department department = this.getById(id);

        Y9Department updatedDepartment = PlatformModelConvertUtil.convert(department, Y9Department.class);
        updatedDepartment.setTabIndex(tabIndex);
        return this.update(updatedDepartment);
    }
}
