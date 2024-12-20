package net.risesoft.service.org.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9DepartmentProp;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Organization;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9DepartmentManager;
import net.risesoft.repository.Y9DepartmentPropRepository;
import net.risesoft.repository.Y9DepartmentRepository;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9ModelConvertUtil;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class Y9DepartmentServiceImpl implements Y9DepartmentService {

    private final Y9DepartmentRepository y9DepartmentRepository;
    private final Y9DepartmentPropRepository y9DepartmentPropRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9DepartmentManager y9DepartmentManager;

    @Override
    @Transactional(readOnly = false)
    public Y9Department changeDisable(String id) {
        final Y9Department department = y9DepartmentManager.getByIdNotCache(id);
        Boolean isDisabled = !department.getDisabled();

        if (isDisabled) {
            // 检查所有子节点是否都禁用了，只有所有子节点都禁用了，当前部门才能禁用
            compositeOrgBaseManager.checkAllDescendantsDisabled(id);
        }

        Y9Department updatedDepartment = Y9ModelConvertUtil.convert(department, Y9Department.class);
        updatedDepartment.setDisabled(isDisabled);
        return y9DepartmentManager.saveOrUpdate(updatedDepartment);
    }

    /**
     * 判断是否移动到自己，或者自己的子节点里面，这种情况要排除，不让移动
     *
     * @param dept
     * @param parentId
     */
    private void checkMoveTarget(Y9Department dept, String parentId) {
        Set<Y9OrgBase> parentSet = new HashSet<>();
        recursionParent(parentId, parentSet);
        if (parentSet.contains(dept)) {
            throw new Y9BusinessException(OrgUnitErrorCodeEnum.MOVE_TO_SUB_DEPARTMENT_NOT_PERMITTED.getCode(),
                OrgUnitErrorCodeEnum.MOVE_TO_SUB_DEPARTMENT_NOT_PERMITTED.getDescription());
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        Y9Department y9Department = this.getById(id);

        y9DepartmentManager.delete(y9Department);

        // 发布事件，程序内部监听处理相关业务
        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Department));
    }

    @Override
    public boolean existsById(String id) {
        return y9DepartmentRepository.existsById(id);
    }

    @Override
    public Optional<Y9Department> findById(String id) {
        return y9DepartmentManager.findById(id);
    }

    @Override
    public Y9Department getById(String id) {
        return y9DepartmentManager.getById(id);
    }

    private void getDeptTrees(String orgBaseId, List<Y9Department> deptList, Boolean disabled) {
        List<Y9Department> childrenList = this.listByParentIdAndDisabled(orgBaseId, disabled);
        if (childrenList.isEmpty()) {
            return;
        }
        deptList.addAll(childrenList);
        for (Y9Department orgDept : childrenList) {
            getDeptTrees(orgDept.getId(), deptList, disabled);
        }
    }

    @Override
    public List<Y9Department> list() {
        return y9DepartmentRepository.findAll();
    }

    @Override
    public List<Y9Department> list(List<String> ids) {
        List<Y9Department> y9DepartmentList = new ArrayList<>();
        for (String id : ids) {
            Optional<Y9Department> y9DepartmentOptional = findById(id);
            y9DepartmentOptional.ifPresent(y9DepartmentList::add);
        }
        return y9DepartmentList;
    }

    @Override
    public List<Y9Department> listBureau(String organizationId, Boolean disabled) {
        if (disabled == null) {
            return y9DepartmentRepository.findByBureauAndGuidPathContainingOrderByTabIndexAsc(Boolean.TRUE,
                organizationId);
        } else {
            return y9DepartmentRepository.findByBureauAndGuidPathContainingAndDisabledOrderByTabIndexAsc(Boolean.TRUE,
                organizationId, disabled);
        }
    }

    @Override
    public List<Y9Department> listBureauByNameLike(String name, Boolean disabled) {
        if (disabled == null) {
            return y9DepartmentRepository.findByBureauAndNameContainingOrderByGuidPathAsc(Boolean.TRUE, name);
        } else {
            return y9DepartmentRepository.findByBureauAndNameContainingAndDisabledOrderByGuidPathAsc(Boolean.TRUE, name,
                disabled);
        }
    }

    @Override
    public List<Y9Department> listByDn(String dn, Boolean disabled) {
        if (disabled == null) {
            return y9DepartmentRepository.findByDn(dn);
        } else {
            return y9DepartmentRepository.findByDnAndDisabled(dn, disabled);
        }
    }

    @Override
    public List<Y9Department> listByNameLike(String name, Boolean disabled) {
        if (disabled == null) {
            return y9DepartmentRepository.findByNameContainingOrderByTabIndexAsc(name);
        } else {
            return y9DepartmentRepository.findByNameContainingAndDisabledOrderByTabIndexAsc(name, disabled);
        }
    }

    @Override
    public List<Y9Department> listByParentId(String parentId, Boolean disabled) {
        if (disabled == null) {
            return y9DepartmentRepository.findByParentIdOrderByTabIndexAsc(parentId);
        } else {
            return y9DepartmentRepository.findByParentIdAndDisabledOrderByTabIndexAsc(parentId, disabled);
        }
    }

    private List<Y9Department> listByParentIdAndDisabled(String orgBaseId, boolean disabled) {
        return y9DepartmentRepository.findByParentIdAndDisabled(orgBaseId, disabled);
    }

    @Override
    public List<Y9OrgBase> listDepartmentPropOrgUnits(String deptId, Integer category, Boolean inherit,
        Boolean disabled) {
        List<Y9DepartmentProp> y9DepartmentPropList =
            y9DepartmentPropRepository.findByDeptIdAndCategoryOrderByTabIndex(deptId, category);

        if (!y9DepartmentPropList.isEmpty()) {
            return getY9OrgBaseList(y9DepartmentPropList, disabled);
        }

        if (Boolean.TRUE.equals(inherit)) {
            return this.listInheritableDepartmentPropOrgUnits(deptId, category, disabled);
        }

        return List.of();
    }

    private List<Y9OrgBase> getY9OrgBaseList(List<Y9DepartmentProp> y9DepartmentPropList, Boolean disabled) {
        List<Y9OrgBase> y9OrgBaseList = new ArrayList<>();
        for (Y9DepartmentProp prop : y9DepartmentPropList) {
            Y9OrgBase y9OrgBase = compositeOrgBaseManager.getPersonOrPosition(prop.getOrgBaseId());
            if (disabled == null || disabled.equals(y9OrgBase.getDisabled())) {
                y9OrgBaseList.add(y9OrgBase);
            }
        }
        return y9OrgBaseList;
    }

    @Override
    public List<Y9OrgBase> listInheritableDepartmentPropOrgUnits(String deptId, Integer category, Boolean disabled) {
        List<Y9DepartmentProp> y9DepartmentPropList = new ArrayList<>();

        String currentDeptId = deptId;
        while (true) {
            Optional<Y9Department> currentDepartmentOptional = y9DepartmentManager.findById(currentDeptId);
            if (currentDepartmentOptional.isEmpty()) {
                break;
            }
            currentDeptId = currentDepartmentOptional.get().getParentId();
            List<Y9DepartmentProp> currentDepartmentPropList =
                y9DepartmentPropRepository.findByDeptIdAndCategoryOrderByTabIndex(currentDeptId, category);
            if (!currentDepartmentPropList.isEmpty()) {
                y9DepartmentPropList.addAll(currentDepartmentPropList);
                break;
            }
        }

        return getY9OrgBaseList(y9DepartmentPropList, disabled);
    }

    @Override
    public List<Y9Department> listRecursivelyByParentId(String orgUnitId, Boolean disabled) {
        List<Y9Department> deptList = new ArrayList<>();
        getDeptTrees(orgUnitId, deptList, disabled);
        return deptList;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Department move(String deptId, String parentId) {
        final Y9Department y9Department = y9DepartmentManager.getByIdNotCache(deptId);
        checkMoveTarget(y9Department, parentId);

        Y9Department updatedDepartment = Y9ModelConvertUtil.convert(y9Department, Y9Department.class);
        updatedDepartment.setParentId(parentId);
        updatedDepartment.setTabIndex(compositeOrgBaseManager.getNextSubTabIndex(parentId));
        return y9DepartmentManager.saveOrUpdate(updatedDepartment);
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onParentDepartmentDeleted(Y9EntityDeletedEvent<Y9Department> event) {
        Y9Department parentDepartment = event.getEntity();
        // 删除部门时其下部门也要删除
        removeByParentId(parentDepartment.getId());
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onParentDepartmentUpdated(Y9EntityUpdatedEvent<Y9Department> event) {
        Y9Department originDepartment = event.getOriginEntity();
        Y9Department updatedDepartment = event.getUpdatedEntity();

        if (Y9OrgUtil.isCurrentOrAncestorChanged(originDepartment, updatedDepartment)) {
            List<Y9Department> deptList = y9DepartmentRepository.findByParentId(updatedDepartment.getId());
            for (Y9Department dept : deptList) {
                this.saveOrUpdate(dept);
            }
        }
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onParentOrganizationDeleted(Y9EntityDeletedEvent<Y9Organization> event) {
        Y9Organization y9Organization = event.getEntity();
        // 删除组织时其下部门也要删除
        removeByParentId(y9Organization.getId());
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onParentOrganizationUpdated(Y9EntityUpdatedEvent<Y9Organization> event) {
        Y9Organization originOrganization = event.getOriginEntity();
        Y9Organization updatedOrganization = event.getUpdatedEntity();

        if (Y9OrgUtil.isRenamed(originOrganization, updatedOrganization)) {
            List<Y9Department> deptList = y9DepartmentRepository.findByParentId(updatedOrganization.getId());
            for (Y9Department dept : deptList) {
                this.saveOrUpdate(dept);
            }
        }
    }

    private void recursionParent(String parentId, Set<Y9OrgBase> parentSet) {
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(parentId);
        parentSet.add(parent);
        if (parent.getOrgType().equals(OrgTypeEnum.DEPARTMENT)) {
            Y9Department dept = (Y9Department)parent;
            recursionParent(dept.getParentId(), parentSet);
        }
    }

    @Transactional(readOnly = false)
    public void removeByParentId(String parentId) {
        List<Y9Department> y9DepartmentList = this.listByParentId(parentId, Boolean.FALSE);
        for (Y9Department y9Department : y9DepartmentList) {
            this.delete(y9Department.getId());
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void removeDepartmentProp(String deptId, Integer category, String orgBaseId) {
        y9DepartmentPropRepository.deleteByDeptIdAndCategoryAndOrgBaseId(deptId, category, orgBaseId);
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9Department> saveOrder(List<String> deptIds) {
        List<Y9Department> orgDeptList = new ArrayList<>();

        int tabIndex = 0;
        for (String deptId : deptIds) {
            orgDeptList.add(y9DepartmentManager.updateTabIndex(deptId, ++tabIndex));
        }
        return orgDeptList;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Department saveOrUpdate(Y9Department dept) {
        return y9DepartmentManager.saveOrUpdate(dept);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Department saveProperties(String id, String properties) {
        return y9DepartmentManager.saveProperties(id, properties);
    }

    @Override
    @Transactional(readOnly = false)
    public void setDepartmentPropOrgUnits(String deptId, Integer category, List<String> orgBaseIds) {
        for (String orgBaseId : orgBaseIds) {
            Optional<Y9DepartmentProp> optionalY9DepartmentProp =
                y9DepartmentPropRepository.findByDeptIdAndOrgBaseIdAndCategory(deptId, orgBaseId, category);
            if (optionalY9DepartmentProp.isEmpty()) {
                Y9DepartmentProp y9DepartmentProp = new Y9DepartmentProp();
                y9DepartmentProp.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                y9DepartmentProp.setDeptId(deptId);
                y9DepartmentProp.setOrgBaseId(orgBaseId);
                y9DepartmentProp.setCategory(category);

                Integer tabIndex = y9DepartmentPropRepository.getMaxTabIndex(deptId, category).orElse(1);
                y9DepartmentProp.setTabIndex(++tabIndex);
                y9DepartmentPropRepository.save(y9DepartmentProp);
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Department updateTabIndex(String id, int tabIndex) {
        return y9DepartmentManager.updateTabIndex(id, tabIndex);
    }

}
