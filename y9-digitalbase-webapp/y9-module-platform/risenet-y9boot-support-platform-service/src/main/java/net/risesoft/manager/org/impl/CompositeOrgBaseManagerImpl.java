package net.risesoft.manager.org.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9Manager;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.repository.Y9DepartmentRepository;
import net.risesoft.repository.Y9GroupRepository;
import net.risesoft.repository.Y9ManagerRepository;
import net.risesoft.repository.Y9OrganizationRepository;
import net.risesoft.repository.Y9PersonRepository;
import net.risesoft.repository.Y9PositionRepository;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class CompositeOrgBaseManagerImpl implements CompositeOrgBaseManager {

    private final Y9DepartmentRepository y9DepartmentRepository;
    private final Y9GroupRepository y9GroupRepository;
    private final Y9OrganizationRepository y9OrganizationRepository;
    private final Y9PersonRepository y9PersonRepository;
    private final Y9PositionRepository y9PositionRepository;
    private final Y9ManagerRepository y9ManagerRepository;

    private void buildGuidPath(StringBuilder sb, Y9OrgBase y9OrgBase) {
        if (OrgTypeEnum.PERSON.equals(y9OrgBase.getOrgType())) {
            // 遍历开始是person，尾部的逗号不用加
            sb.insert(0, y9OrgBase.getId());

            Y9Person person = (Y9Person)y9OrgBase;
            Y9OrgBase parent = this.getOrgUnitAsParent(person.getParentId());
            buildGuidPath(sb, parent);
        } else if (OrgTypeEnum.POSITION.equals(y9OrgBase.getOrgType())) {
            // 遍历开始时，一定是position，尾部的逗号不用加
            sb.insert(0, String.format("%05d", y9OrgBase.getTabIndex()));

            Y9Position position = (Y9Position)y9OrgBase;
            Y9OrgBase parent = this.getOrgUnitAsParent(position.getParentId());
            buildGuidPath(sb, parent);
        } else if (OrgTypeEnum.MANAGER.equals(y9OrgBase.getOrgType())) {
            // 遍历开始是person，尾部的逗号不用加
            sb.insert(0, y9OrgBase.getId());

            Y9Manager person = (Y9Manager)y9OrgBase;
            Y9OrgBase parent = this.getOrgUnitAsParent(person.getParentId());
            buildGuidPath(sb, parent);
        } else if (OrgTypeEnum.DEPARTMENT.equals(y9OrgBase.getOrgType())) {
            sb.insert(0, y9OrgBase.getId() + ",");

            Y9Department dept = (Y9Department)y9OrgBase;
            Y9OrgBase parent = this.getOrgUnitAsParent(dept.getParentId());
            buildGuidPath(sb, parent);
        } else if (OrgTypeEnum.ORGANIZATION.equals(y9OrgBase.getOrgType())) {
            Y9Organization org = (Y9Organization)y9OrgBase;
            // 遍历结束时，一定是org
            sb.insert(0, org.getId() + ",");
        }
    }

    @Override
    public String buildGuidPath(Y9OrgBase y9OrgBase) {
        StringBuilder sb = new StringBuilder();
        buildGuidPath(sb, y9OrgBase);
        return sb.toString();
    }

    @Override
    public String buildOrderedPath(Y9OrgBase y9OrgBase) {
        StringBuilder sb = new StringBuilder();
        buildOrderedPath(sb, y9OrgBase);
        return sb.toString();
    }

    @Override
    public void checkAllDescendantsDisabled(String orgUnitId) {
        Y9OrgBase orgUnit = getOrgUnit(orgUnitId);
        String guidPathPrefix = orgUnit.getGuidPath() + ",";

        if (y9PersonRepository.countByDisabledAndGuidPathContaining(Boolean.FALSE, guidPathPrefix) > 0) {
            throw Y9ExceptionUtil.businessException(OrgUnitErrorCodeEnum.NOT_ALL_PERSONS_DISABLED);
        }

        if (y9PositionRepository.countByDisabledAndGuidPathContaining(Boolean.FALSE, guidPathPrefix) > 0) {
            throw Y9ExceptionUtil.businessException(OrgUnitErrorCodeEnum.NOT_ALL_POSITIONS_DISABLED);
        }

        if (y9GroupRepository.countByDisabledAndGuidPathContaining(Boolean.FALSE, guidPathPrefix) > 0) {
            throw Y9ExceptionUtil.businessException(OrgUnitErrorCodeEnum.NOT_ALL_GROUPS_DISABLED);
        }

        if (y9DepartmentRepository.countByDisabledAndGuidPathContaining(Boolean.FALSE, guidPathPrefix) > 0) {
            throw Y9ExceptionUtil.businessException(OrgUnitErrorCodeEnum.NOT_ALL_DEPARTMENTS_DISABLED);
        }
    }

    @Override
    public Optional<Y9OrgBase> findOrgUnit(String orgUnitId) {
        if (StringUtils.isBlank(orgUnitId)) {
            return Optional.empty();
        }

        Y9OrgBase y9OrgBase = this.findOrganizationById(orgUnitId);
        if (y9OrgBase != null) {
            return Optional.of(y9OrgBase);
        }
        y9OrgBase = this.findDepartmentById(orgUnitId);
        if (y9OrgBase != null) {
            return Optional.of(y9OrgBase);
        }
        y9OrgBase = this.findPersonById(orgUnitId);
        if (y9OrgBase != null) {
            return Optional.of(y9OrgBase);
        }
        y9OrgBase = this.findPositionById(orgUnitId);
        if (y9OrgBase != null) {
            return Optional.of(y9OrgBase);
        }
        y9OrgBase = this.findGroupById(orgUnitId);
        if (y9OrgBase != null) {
            return Optional.of(y9OrgBase);
        }
        y9OrgBase = y9ManagerRepository.findById(orgUnitId).orElse(null);
        return Optional.ofNullable(y9OrgBase);
    }

    @Override
    public Optional<Y9OrgBase> findOrgUnitAsParent(String orgUnitId) {
        Y9OrgBase y9Department = this.findDepartmentById(orgUnitId);
        if (y9Department != null) {
            return Optional.of(y9Department);
        }

        Y9OrgBase organization = this.findOrganizationById(orgUnitId);
        return Optional.ofNullable(organization);
    }

    @Override
    public Optional<Y9OrgBase> findOrgUnitBureau(String orgUnitId) {
        Optional<Y9OrgBase> y9OrgBaseOptional = this.findOrgUnit(orgUnitId);
        if (y9OrgBaseOptional.isPresent()) {
            Y9OrgBase y9OrgBase = y9OrgBaseOptional.get();

            if (OrgTypeEnum.ORGANIZATION.equals(y9OrgBase.getOrgType())) {
                // 往上找不到作为委办局的部门，最终返回组织机构
                return Optional.of(y9OrgBase);
            }

            if (OrgTypeEnum.DEPARTMENT.equals(y9OrgBase.getOrgType())) {
                Y9Department dept = (Y9Department)y9OrgBase;
                if (Boolean.TRUE.equals(dept.getBureau())) {
                    return Optional.of(dept);
                }
            }

            return findOrgUnitBureau(y9OrgBase.getParentId());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Y9OrgBase> findOrgUnitParent(String orgUnitId) {
        Optional<Y9OrgBase> y9OrgBaseOptional = this.findOrgUnit(orgUnitId);
        if (y9OrgBaseOptional.isPresent()) {
            Y9OrgBase orgBase = y9OrgBaseOptional.get();

            if (OrgTypeEnum.ORGANIZATION.equals(orgBase.getOrgType())) {
                // orgUnitId 对应的组织节点为组织机构时直接返回 null，因为组织机构已无父节点
                return Optional.empty();
            }

            return this.findOrgUnitAsParent(orgBase.getParentId());
        }
        return Optional.empty();
    }

    @Override
    public Integer getNextSubTabIndex(String parentId) {
        Integer maxTabIndex = -1;

        Integer maxDepartmentTabIndex =
            y9DepartmentRepository.findTopByParentIdOrderByTabIndexDesc(parentId).map(Y9OrgBase::getTabIndex).orElse(0);
        maxTabIndex = Math.max(maxDepartmentTabIndex, maxTabIndex);

        Integer maxGroupTabIndex =
            y9GroupRepository.findTopByParentIdOrderByTabIndexDesc(parentId).map(Y9OrgBase::getTabIndex).orElse(0);
        maxTabIndex = Math.max(maxGroupTabIndex, maxTabIndex);

        Integer maxPositionTabIndex =
            y9PositionRepository.findTopByParentIdOrderByTabIndexDesc(parentId).map(Y9OrgBase::getTabIndex).orElse(0);
        maxTabIndex = Math.max(maxPositionTabIndex, maxTabIndex);

        Integer maxPersonTabIndex =
            y9PersonRepository.findTopByParentIdOrderByTabIndexDesc(parentId).map(Y9OrgBase::getTabIndex).orElse(0);
        maxTabIndex = Math.max(maxPersonTabIndex, maxTabIndex);

        Integer maxManagerTabIndex =
            y9ManagerRepository.findTopByParentIdOrderByTabIndexDesc(parentId).map(Y9OrgBase::getTabIndex).orElse(0);
        maxTabIndex = Math.max(maxManagerTabIndex, maxTabIndex);

        return maxTabIndex + 1;
    }

    @Override
    public Y9OrgBase getOrgUnit(String orgUnitId) {
        return this.findOrgUnit(orgUnitId)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.ORG_UNIT_NOT_FOUND, orgUnitId));
    }

    @Override
    public Y9OrgBase getOrgUnitAsParent(String orgUnitId) {
        return this.findOrgUnitAsParent(orgUnitId).orElseThrow(
            () -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.ORG_UNIT_AS_PARENT_NOT_FOUND, orgUnitId));
    }

    @Override
    public Y9OrgBase getOrgUnitBureau(String orgUnitId) {
        return this.findOrgUnitBureau(orgUnitId).orElseThrow(
            () -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.ORG_UNIT_BUREAU_NOT_FOUND, orgUnitId));
    }

    @Override
    public Y9OrgBase getOrgUnitParent(String orgUnitId) {
        return this.findOrgUnitParent(orgUnitId).orElseThrow(
            () -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.ORG_UNIT_PARENT_NOT_FOUND, orgUnitId));
    }

    @Override
    public List<Y9Person> listAllDescendantPersons(String parentId) {
        List<Y9Person> personList = new ArrayList<>();
        fillPersonsRecursivelyToLeaf(parentId, personList);
        return personList;
    }

    @Override
    public List<Y9Person> listAllDescendantPersons(String parentId, Boolean disabled) {
        List<Y9Person> personList = new ArrayList<>();
        fillPersonsRecursivelyToLeaf(parentId, personList, disabled);
        return personList;
    }

    @Override
    public List<Y9Position> listAllDescendantPositions(String parentId) {
        List<Y9Position> positionList = new ArrayList<>();
        fillPositionsRecursivelyToLeaf(parentId, positionList);
        return positionList;
    }

    private void buildOrderedPath(StringBuilder sb, Y9OrgBase y9OrgBase) {
        if (OrgTypeEnum.PERSON.equals(y9OrgBase.getOrgType())) {
            // 遍历开始时，一定是person，尾部的逗号不用加
            sb.insert(0, String.format("%05d", y9OrgBase.getTabIndex()));

            Y9Person person = (Y9Person)y9OrgBase;
            Y9OrgBase parent = this.getOrgUnitAsParent(person.getParentId());
            buildOrderedPath(sb, parent);
        } else if (OrgTypeEnum.POSITION.equals(y9OrgBase.getOrgType())) {
            // 遍历开始时，一定是manager，尾部的逗号不用加
            sb.insert(0, String.format("%05d", y9OrgBase.getTabIndex()));

            Y9Position y9Position = (Y9Position)y9OrgBase;
            Y9OrgBase parent = this.getOrgUnitAsParent(y9Position.getParentId());
            buildOrderedPath(sb, parent);
        } else if (OrgTypeEnum.MANAGER.equals(y9OrgBase.getOrgType())) {
            // 遍历开始时，一定是manager，尾部的逗号不用加
            sb.insert(0, String.format("%05d", y9OrgBase.getTabIndex()));

            Y9Manager person = (Y9Manager)y9OrgBase;
            Y9OrgBase parent = this.getOrgUnitAsParent(person.getParentId());
            buildOrderedPath(sb, parent);
        } else if (OrgTypeEnum.DEPARTMENT.equals(y9OrgBase.getOrgType())) {
            sb.insert(0, String.format("%05d", y9OrgBase.getTabIndex()) + ",");

            Y9Department dept = (Y9Department)y9OrgBase;
            Y9OrgBase parent = this.getOrgUnitAsParent(dept.getParentId());
            buildOrderedPath(sb, parent);
        } else if (OrgTypeEnum.ORGANIZATION.equals(y9OrgBase.getOrgType())) {
            // 遍历结束时，一定是org
            sb.insert(0, String.format("%05d", y9OrgBase.getTabIndex()) + ",");
        }
    }

    @Cacheable(cacheNames = CacheNameConsts.ORG_DEPARTMENT, key = "#id", condition = "#id!=null",
        unless = "#result==null")
    public Y9Department findDepartmentById(String id) {
        return y9DepartmentRepository.findById(id).orElse(null);
    }

    private List<Y9Department> findDepartmentByParentId(String parentId) {
        return y9DepartmentRepository.findByParentIdOrderByTabIndexAsc(parentId);
    }

    @Cacheable(cacheNames = CacheNameConsts.ORG_GROUP, key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Group findGroupById(String id) {
        return y9GroupRepository.findById(id).orElse(null);
    }

    @Cacheable(cacheNames = CacheNameConsts.ORG_ORGANIZATION, key = "#id", condition = "#id!=null",
        unless = "#result==null")
    public Y9Organization findOrganizationById(String id) {
        return y9OrganizationRepository.findById(id).orElse(null);
    }

    @Cacheable(cacheNames = CacheNameConsts.ORG_PERSON, key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Person findPersonById(String id) {
        return y9PersonRepository.findById(id).orElse(null);
    }

    private List<Y9Person> findPersonByParentId(String parentId) {
        return y9PersonRepository.findByParentIdOrderByTabIndex(parentId);
    }

    private List<Y9Person> findPersonByParentIdAndDisabled(String parentId, boolean disabled) {
        return y9PersonRepository.findByParentIdAndDisabledOrderByTabIndex(parentId, disabled);
    }

    @Cacheable(cacheNames = CacheNameConsts.ORG_POSITION, key = "#id", condition = "#id!=null",
        unless = "#result==null")
    public Y9Position findPositionById(String id) {
        return y9PositionRepository.findById(id).orElse(null);
    }

    private List<Y9Position> findPositionByParentId(String parentId) {
        return y9PositionRepository.findByParentIdOrderByTabIndexAsc(parentId);
    }

    private void fillPositionsRecursivelyToLeaf(String parentId, List<Y9Position> positionList) {
        positionList.addAll(findPositionByParentId(parentId));

        List<Y9Department> y9DepartmentList = findDepartmentByParentId(parentId);
        for (Y9Department y9Department : y9DepartmentList) {
            fillPositionsRecursivelyToLeaf(y9Department.getId(), positionList);
        }
    }

    private void fillPersonsRecursivelyToLeaf(String parentId, List<Y9Person> personList) {
        personList.addAll(findPersonByParentId(parentId));

        List<Y9Department> deptList = findDepartmentByParentId(parentId);
        for (Y9Department dept : deptList) {
            fillPersonsRecursivelyToLeaf(dept.getId(), personList);
        }
    }

    private void fillPersonsRecursivelyToLeaf(String parentId, List<Y9Person> personList, Boolean disabled) {
        if (disabled == null) {
            personList.addAll(findPersonByParentId(parentId));
        } else {
            personList.addAll(findPersonByParentIdAndDisabled(parentId, disabled));
        }

        List<Y9Department> deptList = findDepartmentByParentId(parentId);
        for (Y9Department dept : deptList) {
            fillPersonsRecursivelyToLeaf(dept.getId(), personList, disabled);
        }
    }

}