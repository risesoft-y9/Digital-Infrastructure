package net.risesoft.manager.org.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.consts.OrgLevelConsts;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9Manager;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.enums.OrgTypeEnum;
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
@Slf4j
@RequiredArgsConstructor
public class CompositeOrgBaseManagerImpl implements CompositeOrgBaseManager {

    private final Y9DepartmentRepository y9DepartmentRepository;
    private final Y9GroupRepository y9GroupRepository;
    private final Y9OrganizationRepository y9OrganizationRepository;
    private final Y9PersonRepository y9PersonRepository;
    private final Y9PositionRepository y9PositionRepository;
    private final Y9ManagerRepository y9ManagerRepository;

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

            if (OrgTypeEnum.ORGANIZATION.getEnName().equals(y9OrgBase.getOrgType())) {
                // 往上找不到作为委办局的部门，最终返回组织机构
                return Optional.of(y9OrgBase);
            }

            if (OrgTypeEnum.DEPARTMENT.getEnName().equals(y9OrgBase.getOrgType())) {
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

            if (OrgTypeEnum.ORGANIZATION.getEnName().equals(orgBase.getOrgType())) {
                // orgUnitId 对应的组织节点为组织机构时直接返回 null，因为组织机构已无父节点
                return Optional.empty();
            }

            return this.findOrgUnitAsParent(orgBase.getParentId());
        }
        return Optional.empty();
    }

    @Override
    public Integer getMaxSubTabIndex(String parentId, OrgTypeEnum orgType) {
        Integer maxTabIndex;
        switch (orgType) {
            case DEPARTMENT:
                maxTabIndex = y9DepartmentRepository.findTopByParentIdOrderByTabIndexDesc(parentId)
                    .map(Y9OrgBase::getTabIndex).orElse(0);
                break;
            case GROUP:
                maxTabIndex = y9GroupRepository.findTopByParentIdOrderByTabIndexDesc(parentId)
                    .map(Y9OrgBase::getTabIndex).orElse(0);
                break;
            case POSITION:
                maxTabIndex = y9PositionRepository.findTopByParentIdOrderByTabIndexDesc(parentId)
                    .map(Y9OrgBase::getTabIndex).orElse(0);
                break;
            case PERSON:
                maxTabIndex = y9PersonRepository.findTopByParentIdOrderByTabIndexDesc(parentId)
                    .map(Y9OrgBase::getTabIndex).orElse(0);
                break;
            case MANAGER:
                maxTabIndex = y9ManagerRepository.findTopByParentIdOrderByTabIndexDesc(parentId)
                    .map(Y9OrgBase::getTabIndex).orElse(0);
                break;
            default:
                maxTabIndex = 0;
        }
        return maxTabIndex + 1;
    }

    @Override
    @Transactional(readOnly = true)
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

    @Cacheable(cacheNames = CacheNameConsts.ORG_ORGANIZATION, key = "#id", condition = "#id!=null",
        unless = "#result==null")
    public Y9Organization findOrganizationById(String id) {
        return y9OrganizationRepository.findById(id).orElse(null);
    }

    @Cacheable(cacheNames = CacheNameConsts.ORG_DEPARTMENT, key = "#id", condition = "#id!=null",
        unless = "#result==null")
    public Y9Department findDepartmentById(String id) {
        return y9DepartmentRepository.findById(id).orElse(null);
    }

    @Cacheable(cacheNames = CacheNameConsts.ORG_PERSON, key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Person findPersonById(String id) {
        return y9PersonRepository.findById(id).orElse(null);
    }

    @Cacheable(cacheNames = CacheNameConsts.ORG_POSITION, key = "#id", condition = "#id!=null",
        unless = "#result==null")
    public Y9Position findPositionById(String id) {
        return y9PositionRepository.findById(id).orElse(null);
    }

    @Cacheable(cacheNames = CacheNameConsts.ORG_GROUP, key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Group findGroupById(String id) {
        return y9GroupRepository.findById(id).orElse(null);
    }

    @Override
    public List<Y9Person> listAllPersonsRecursionDownward(String parentId) {
        List<Y9Person> personList = new ArrayList<>();
        getPersonListByDownwardRecursion(parentId, personList);
        return personList;
    }

    @Override
    public List<Y9Person> listAllPersonsRecursionDownward(String parentId, Boolean disabled) {
        List<Y9Person> personList = new ArrayList<>();
        getPersonListByDownwardRecursion(parentId, personList, disabled);
        return personList;
    }

    @Override
    public List<Y9Position> listAllPositionsRecursionDownward(String parentId) {
        List<Y9Position> positionList = new ArrayList<>();
        getAllPositionListByDownwardRecursion(parentId, positionList);
        return positionList;
    }

    @Override
    public void recursivelyUpdateProperties(Y9OrgBase parent) {
        // 部门
        List<Y9Department> deptList = findDepartmentByParentId(parent.getId());
        for (Y9Department dept : deptList) {
            dept.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.DEPARTMENT) + dept.getName() + OrgLevelConsts.SEPARATOR
                + parent.getDn());
            dept.setGuidPath(parent.getGuidPath() + OrgLevelConsts.SEPARATOR + dept.getId());
            y9DepartmentRepository.save(dept);
            recursivelyUpdateProperties(dept);
        }

        // 用户组
        List<Y9Group> groupList = findGroupByParentId(parent.getId());
        for (Y9Group group : groupList) {
            group.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.GROUP) + group.getName() + OrgLevelConsts.SEPARATOR
                + parent.getDn());
            group.setGuidPath(parent.getGuidPath() + OrgLevelConsts.SEPARATOR + group.getId());
            y9GroupRepository.save(group);
        }

        // 岗位
        List<Y9Position> positionList = findPositionByParentId(parent.getId());
        for (Y9Position position : positionList) {
            position.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.POSITION) + position.getName()
                + OrgLevelConsts.SEPARATOR + parent.getDn());
            position.setGuidPath(parent.getGuidPath() + OrgLevelConsts.SEPARATOR + position.getId());
            y9PositionRepository.save(position);
        }

        // 人员
        List<Y9Person> personList = findPersonByParentId(parent.getId());
        for (Y9Person person : personList) {
            person.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.PERSON) + person.getName() + OrgLevelConsts.SEPARATOR
                + parent.getDn());
            person.setGuidPath(parent.getGuidPath() + OrgLevelConsts.SEPARATOR + person.getId());
            y9PersonRepository.save(person);
        }
    }

    private void buildGuidPath(StringBuilder sb, Y9OrgBase y9OrgBase) {
        if (OrgTypeEnum.PERSON.getEnName().equals(y9OrgBase.getOrgType())) {
            // 遍历开始是person，尾部的逗号不用加
            sb.insert(0, y9OrgBase.getId());

            Y9Person person = (Y9Person)y9OrgBase;
            Y9OrgBase parent = this.getOrgUnitAsParent(person.getParentId());
            buildGuidPath(sb, parent);
        } else if (OrgTypeEnum.POSITION.getEnName().equals(y9OrgBase.getOrgType())) {
            // 遍历开始时，一定是position，尾部的逗号不用加
            sb.insert(0, String.format("%05d", y9OrgBase.getTabIndex()));

            Y9Position position = (Y9Position)y9OrgBase;
            Y9OrgBase parent = this.getOrgUnitAsParent(position.getParentId());
            buildGuidPath(sb, parent);
        } else if (OrgTypeEnum.MANAGER.getEnName().equals(y9OrgBase.getOrgType())) {
            // 遍历开始是person，尾部的逗号不用加
            sb.insert(0, y9OrgBase.getId());

            Y9Manager person = (Y9Manager)y9OrgBase;
            Y9OrgBase parent = this.getOrgUnitAsParent(person.getParentId());
            buildGuidPath(sb, parent);
        } else if (OrgTypeEnum.DEPARTMENT.getEnName().equals(y9OrgBase.getOrgType())) {
            sb.insert(0, y9OrgBase.getId() + ",");

            Y9Department dept = (Y9Department)y9OrgBase;
            Y9OrgBase parent = this.getOrgUnitAsParent(dept.getParentId());
            buildGuidPath(sb, parent);
        } else if (OrgTypeEnum.ORGANIZATION.getEnName().equals(y9OrgBase.getOrgType())) {
            Y9Organization org = (Y9Organization)y9OrgBase;
            // 遍历结束时，一定是org
            sb.insert(0, org.getId() + ",");
        }
    }

    private void buildOrderedPath(StringBuilder sb, Y9OrgBase y9OrgBase) {
        if (OrgTypeEnum.PERSON.getEnName().equals(y9OrgBase.getOrgType())) {
            // 遍历开始时，一定是person，尾部的逗号不用加
            sb.insert(0, String.format("%05d", y9OrgBase.getTabIndex()));

            Y9Person person = (Y9Person)y9OrgBase;
            Y9OrgBase parent = this.getOrgUnitAsParent(person.getParentId());
            buildOrderedPath(sb, parent);
        } else if (OrgTypeEnum.POSITION.getEnName().equals(y9OrgBase.getOrgType())) {
            // 遍历开始时，一定是manager，尾部的逗号不用加
            sb.insert(0, String.format("%05d", y9OrgBase.getTabIndex()));

            Y9Position y9Position = (Y9Position)y9OrgBase;
            Y9OrgBase parent = this.getOrgUnitAsParent(y9Position.getParentId());
            buildOrderedPath(sb, parent);
        } else if (OrgTypeEnum.MANAGER.getEnName().equals(y9OrgBase.getOrgType())) {
            // 遍历开始时，一定是manager，尾部的逗号不用加
            sb.insert(0, String.format("%05d", y9OrgBase.getTabIndex()));

            Y9Manager person = (Y9Manager)y9OrgBase;
            Y9OrgBase parent = this.getOrgUnitAsParent(person.getParentId());
            buildOrderedPath(sb, parent);
        } else if (OrgTypeEnum.DEPARTMENT.getEnName().equals(y9OrgBase.getOrgType())) {
            sb.insert(0, String.format("%05d", y9OrgBase.getTabIndex()) + ",");

            Y9Department dept = (Y9Department)y9OrgBase;
            Y9OrgBase parent = this.getOrgUnitAsParent(dept.getParentId());
            buildOrderedPath(sb, parent);
        } else if (OrgTypeEnum.ORGANIZATION.getEnName().equals(y9OrgBase.getOrgType())) {
            // 遍历结束时，一定是org
            sb.insert(0, String.format("%05d", y9OrgBase.getTabIndex()) + ",");
        }
    }

    private List<Y9Department> findDepartmentByParentId(String parentId) {
        return y9DepartmentRepository.findByParentIdOrderByTabIndexAsc(parentId);
    }

    private List<Y9Group> findGroupByParentId(String parentId) {
        return y9GroupRepository.findByParentIdOrderByTabIndexAsc(parentId);
    }

    private List<Y9Person> findPersonByParentId(String parentId) {
        return y9PersonRepository.findByParentIdOrderByTabIndex(parentId);
    }

    private List<Y9Person> findPersonByParentIdAndDisabled(String parentId, boolean disabled) {
        return y9PersonRepository.findByDisabledAndParentIdOrderByTabIndex(disabled, parentId);
    }

    private List<Y9Position> findPositionByParentId(String parentId) {
        return y9PositionRepository.findByParentIdOrderByTabIndexAsc(parentId);
    }

    private void getAllPositionListByDownwardRecursion(String parentId, List<Y9Position> positionList) {
        positionList.addAll(findPositionByParentId(parentId));

        List<Y9Department> y9DepartmentList = findDepartmentByParentId(parentId);
        for (Y9Department y9Department : y9DepartmentList) {
            getAllPositionListByDownwardRecursion(y9Department.getId(), positionList);
        }
    }

    private void getPersonListByDownwardRecursion(String parentId, List<Y9Person> personList) {
        personList.addAll(findPersonByParentId(parentId));

        List<Y9Department> deptList = findDepartmentByParentId(parentId);
        for (Y9Department dept : deptList) {
            getPersonListByDownwardRecursion(dept.getId(), personList);
        }
    }

    private void getPersonListByDownwardRecursion(String parentId, List<Y9Person> personList, Boolean disabled) {
        personList.addAll(findPersonByParentIdAndDisabled(parentId, disabled));

        List<Y9Department> deptList = findDepartmentByParentId(parentId);
        for (Y9Department dept : deptList) {
            getPersonListByDownwardRecursion(dept.getId(), personList, disabled);
        }
    }

}