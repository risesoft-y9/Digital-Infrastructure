package net.risesoft.service.org.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.org.Y9Department;
import net.risesoft.entity.org.Y9DepartmentProp;
import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.entity.org.Y9Organization;
import net.risesoft.enums.AuditLogEnum;
import net.risesoft.enums.platform.org.DepartmentPropCategoryEnum;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9DepartmentManager;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.repository.org.Y9DepartmentRepository;
import net.risesoft.service.org.Y9DepartmentPropService;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9EnumUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9.util.Y9StringUtil;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class Y9DepartmentServiceImpl implements Y9DepartmentService {

    private final Y9DepartmentRepository y9DepartmentRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9DepartmentManager y9DepartmentManager;

    private final Y9DepartmentPropService y9DepartmentPropService;

    @Override
    @Transactional(readOnly = false)
    public Y9Department changeDisable(String id) {
        Y9Department currentDepartment = y9DepartmentManager.getById(id);
        Y9Department originalDepartment = Y9ModelConvertUtil.convert(currentDepartment, Y9Department.class);

        Boolean disableStatusToUpdate = !currentDepartment.getDisabled();
        if (disableStatusToUpdate) {
            // 检查所有子节点是否都禁用了，只有所有子节点都禁用了，当前部门才能禁用
            compositeOrgBaseManager.checkAllDescendantsDisabled(id);
        }

        currentDepartment.setDisabled(disableStatusToUpdate);
        Y9Department savedDepartment = y9DepartmentManager.update(currentDepartment);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.DEPARTMENT_UPDATE_DISABLED.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.DEPARTMENT_UPDATE_DISABLED.getDescription(),
                currentDepartment.getName(), disableStatusToUpdate ? "禁用" : "启用"))
            .objectId(id)
            .oldObject(originalDepartment)
            .currentObject(savedDepartment)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedDepartment;
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

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.DEPARTMENT_DELETE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.DEPARTMENT_DELETE.getDescription(), y9Department.getName()))
            .objectId(id)
            .oldObject(y9Department)
            .currentObject(null)
            .build();
        Y9Context.publishEvent(auditLogEvent);
    }

    @Override
    public Optional<Y9Department> findById(String id) {
        return y9DepartmentManager.findByIdFromCache(id);
    }

    @Override
    public Y9Department getById(String id) {
        return y9DepartmentManager.getByIdFromCache(id);
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
        List<Y9DepartmentProp> y9DepartmentPropList = y9DepartmentPropService.listByDeptIdAndCategory(deptId,
            Y9EnumUtil.valueOf(DepartmentPropCategoryEnum.class, category));
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
            Optional<Y9Department> currentDepartmentOptional = y9DepartmentManager.findByIdFromCache(currentDeptId);
            if (currentDepartmentOptional.isEmpty()) {
                break;
            }
            currentDeptId = currentDepartmentOptional.get().getParentId();
            List<Y9DepartmentProp> currentDepartmentPropList = y9DepartmentPropService
                .listByDeptIdAndCategory(currentDeptId, Y9EnumUtil.valueOf(DepartmentPropCategoryEnum.class, category));
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
    public Y9Department move(String id, String parentId) {
        Y9OrgBase parentToMove = compositeOrgBaseManager.getOrgUnitAsParent(parentId);
        Y9Department currentDepartment = y9DepartmentManager.getById(id);
        Y9Department originalDepartment = Y9ModelConvertUtil.convert(currentDepartment, Y9Department.class);

        checkMoveTarget(currentDepartment, parentId);

        currentDepartment.setParentId(parentId);
        currentDepartment.setTabIndex(compositeOrgBaseManager.getNextSubTabIndex(parentId));
        Y9Department savedDepartment = y9DepartmentManager.update(currentDepartment);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.DEPARTMENT_UPDATE_PARENTID.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.DEPARTMENT_UPDATE_PARENTID.getDescription(),
                originalDepartment.getName(), parentToMove.getName()))
            .objectId(id)
            .oldObject(originalDepartment)
            .currentObject(savedDepartment)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedDepartment;
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
    public void onParentDepartmentDeleted(Y9EntityDeletedEvent<Y9Department> event) {
        Y9Department parentDepartment = event.getEntity();
        // 删除部门时其下部门也要删除
        removeByParentId(parentDepartment.getId());
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onParentUpdated(Y9EntityUpdatedEvent<? extends Y9OrgBase> event) {
        Y9OrgBase originOrgBase = event.getOriginEntity();
        Y9OrgBase updatedOrgBase = event.getUpdatedEntity();

        if (Y9OrgUtil.isCurrentOrAncestorChanged(originOrgBase, updatedOrgBase)) {
            List<Y9Department> deptList = y9DepartmentRepository.findByParentId(updatedOrgBase.getId());
            for (Y9Department dept : deptList) {
                y9DepartmentManager.update(dept);
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
        y9DepartmentPropService.deleteByDeptIdAndCategoryAndOrgBaseId(deptId,
            Y9EnumUtil.valueOf(DepartmentPropCategoryEnum.class, category), orgBaseId);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Department saveOrUpdate(Y9Department dept) {
        if (StringUtils.isNotBlank(dept.getId())) {
            Optional<Y9Department> departmentOptional = y9DepartmentManager.findById(dept.getId());
            if (departmentOptional.isPresent()) {
                Y9Department originalDepartment =
                    Y9ModelConvertUtil.convert(departmentOptional.get(), Y9Department.class);
                Y9Department savedDepartment = y9DepartmentManager.update(dept);

                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(AuditLogEnum.DEPARTMENT_UPDATE.getAction())
                    .description(
                        Y9StringUtil.format(AuditLogEnum.DEPARTMENT_UPDATE.getDescription(), savedDepartment.getName()))
                    .objectId(savedDepartment.getId())
                    .oldObject(originalDepartment)
                    .currentObject(savedDepartment)
                    .build();
                Y9Context.publishEvent(auditLogEvent);

                return savedDepartment;
            }
        }

        Y9Department savedDepartment = y9DepartmentManager.insert(dept);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.DEPARTMENT_CREATE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.DEPARTMENT_CREATE.getDescription(), dept.getName()))
            .objectId(dept.getId())
            .oldObject(null)
            .currentObject(savedDepartment)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedDepartment;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Department saveProperties(String id, String properties) {
        Y9Department currentDepartment = y9DepartmentManager.getById(id);
        Y9Department originalDepartment = Y9ModelConvertUtil.convert(currentDepartment, Y9Department.class);

        currentDepartment.setProperties(properties);
        Y9Department savedDepartment = y9DepartmentManager.update(currentDepartment);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.DEPARTMENT_UPDATE_PROPERTIES.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.DEPARTMENT_UPDATE_PROPERTIES.getDescription(),
                savedDepartment.getName(), savedDepartment.getProperties()))
            .objectId(id)
            .oldObject(originalDepartment)
            .currentObject(savedDepartment)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedDepartment;
    }

    @Override
    @Transactional(readOnly = false)
    public void setDepartmentPropOrgUnits(String deptId, Integer category, List<String> orgBaseIds) {
        for (String orgBaseId : orgBaseIds) {
            Y9DepartmentProp y9DepartmentProp = new Y9DepartmentProp();
            y9DepartmentProp.setDeptId(deptId);
            y9DepartmentProp.setOrgBaseId(orgBaseId);
            y9DepartmentProp.setCategory(category);
            y9DepartmentPropService.saveOrUpdate(y9DepartmentProp);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Department updateTabIndex(String id, int tabIndex) {
        Y9Department originalDepartment =
            Y9ModelConvertUtil.convert(y9DepartmentManager.getById(id), Y9Department.class);
        Y9Department savedDepartment = y9DepartmentManager.updateTabIndex(id, tabIndex);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.DEPARTMENT_UPDATE_TABINDEX.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.DEPARTMENT_UPDATE_TABINDEX.getDescription(),
                originalDepartment.getName(), savedDepartment.getTabIndex()))
            .objectId(id)
            .oldObject(originalDepartment)
            .currentObject(savedDepartment)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedDepartment;
    }

    @Override
    public Page<Y9Department> page(List<String> orgIdList, Y9PageQuery pageQuery) {
        Pageable pageable =
            PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by(Sort.Direction.ASC, "createTime"));
        Specification<Y9Department> spec = new Specification<>() {
            private static final long serialVersionUID = -95805766801894738L;

            @Override
            public Predicate toPredicate(Root<Y9Department> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                for (String id : orgIdList) {
                    predicates.add(cb.like(root.get("guidPath").as(String.class), "%" + id + "%"));
                }
                return cb.or(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return y9DepartmentRepository.findAll(spec, pageable);
    }
}
