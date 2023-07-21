package net.risesoft.manager.org.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.consts.OrgLevelConsts;
import net.risesoft.consts.TreeTypeConsts;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9Manager;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9OrgBaseDeleted;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToGroups;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.OrgTypeEnum;
import net.risesoft.manager.org.Y9OrgBaseManager;
import net.risesoft.model.Department;
import net.risesoft.model.Group;
import net.risesoft.model.Organization;
import net.risesoft.model.Person;
import net.risesoft.model.PersonsGroups;
import net.risesoft.model.PersonsPositions;
import net.risesoft.model.Position;
import net.risesoft.repository.Y9DepartmentRepository;
import net.risesoft.repository.Y9GroupRepository;
import net.risesoft.repository.Y9ManagerRepository;
import net.risesoft.repository.Y9OrgBaseDeletedRepository;
import net.risesoft.repository.Y9OrganizationRepository;
import net.risesoft.repository.Y9PersonRepository;
import net.risesoft.repository.Y9PositionRepository;
import net.risesoft.repository.relation.Y9PersonsToGroupsRepository;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.util.Y9PublishServiceUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;

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
public class Y9OrgBaseManagerImpl implements Y9OrgBaseManager {

    private final Y9PersonsToGroupsRepository y9PersonsToGroupsRepository;
    private final Y9PersonsToPositionsRepository y9PersonsToPositionsRepository;
    private final Y9DepartmentRepository y9DepartmentRepository;
    private final Y9GroupRepository y9GroupRepository;
    private final Y9OrganizationRepository y9OrganizationRepository;
    private final Y9PersonRepository y9PersonRepository;
    private final Y9PositionRepository y9PositionRepository;
    private final Y9ManagerRepository y9ManagerRepository;
    private final Y9OrgBaseDeletedRepository y9OrgBaseDeletedRepository;

    private void deptIdUpToOrg(String parentId, List<Y9OrgBase> childrenList, String id) {
        Y9Department parentDept = findDepartmentById(parentId);
        if (parentDept != null && parentDept.getParentId().equals(id)) {
            if (!childrenList.isEmpty()) {
                for (Y9OrgBase base : childrenList) {
                    Y9Department y9Department = findDepartmentById(base.getId());
                    if (y9Department != null && parentDept.getDn().contains(y9Department.getDn())) {
                    } else {
                        childrenList.add(findDepartmentById(parentId));
                    }
                }
            } else {
                childrenList.add(findDepartmentById(parentId));
            }
        } else {
            deptIdUpToOrg(parentDept.getParentId(), childrenList, id);
        }
    }

    @Cacheable(cacheNames = CacheNameConsts.ORG_GROUP, key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Group findGroupById(String id) {
        return y9GroupRepository.findById(id).orElse(null);
    }

    @Cacheable(cacheNames = CacheNameConsts.ORG_DEPARTMENT, key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Department findDepartmentById(String id) {
        return y9DepartmentRepository.findById(id).orElse(null);
    }

    @Cacheable(cacheNames = CacheNameConsts.ORG_ORGANIZATION, key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Organization findOrganizationById(String id) {
        return y9OrganizationRepository.findById(id).orElse(null);
    }

    @Cacheable(cacheNames = CacheNameConsts.ORG_PERSON, key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Person findPersonById(String id) {
        return y9PersonRepository.findById(id).orElse(null);
    }

    @Cacheable(cacheNames = CacheNameConsts.ORG_POSITION, key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Position findPositionById(String id) {
        return y9PositionRepository.findById(id).orElse(null);
    }

    private List<Y9Person> findByNameContainingAndDisabled(String name) {
        return y9PersonRepository.findByNameContainingAndDisabled(name, false);
    }

    private List<Y9Department> findDepartmentByNameLike(String name) {
        return y9DepartmentRepository.findByNameContainingOrderByTabIndexAsc(name);
    }

    private List<Y9Department> findDepartmentByNameLike(String name, String dnName) {
        return y9DepartmentRepository.findByNameContainingAndDnContainingOrderByTabIndexAsc(name, dnName);
    }

    private List<Y9Department> findDepartmentByParentId(String parentId) {
        return y9DepartmentRepository.findByParentIdOrderByTabIndexAsc(parentId);
    }
    
    private List<Y9Group> findGroupByNameLike(String name) {
        return y9GroupRepository.findByNameContainingOrderByTabIndexAsc(name);
    }

    private List<Y9Group> findGroupByNameLike(String name, String dnName) {
        return y9GroupRepository.findByNameContainingAndDnContainingOrderByTabIndex(name, dnName);
    }

    private List<Y9Group> findGroupByParentId(String parentId) {
        return y9GroupRepository.findByParentIdOrderByTabIndexAsc(parentId);
    }

    private List<Y9Manager> findManagerByNameLike(String name) {
        return y9ManagerRepository.findByNameContainingAndGlobalManagerFalse(name);
    }
    
    private List<Y9Manager> findManagerByNameLike(String name, String dnName) {
        return y9ManagerRepository.findByNameContainingAndDnContaining(name, dnName);
    }

    private List<Y9Person> findPersonByGroupId(String groupId) {
        List<Y9PersonsToGroups> pgs = y9PersonsToGroupsRepository.findByGroupIdOrderByPersonOrder(groupId);
        List<String> personIdList = pgs.stream().map(Y9PersonsToGroups::getPersonId).collect(Collectors.toList());
        List<Y9Person> personList = new ArrayList<>();
        for (String personId : personIdList) {
            personList.add(findPersonById(personId));
        }
        return personList;
    }

    private List<Y9Person> findPersonByNameLike(String name) {
        return y9PersonRepository.findByNameContaining(name);
    }

    private List<Y9Person> findPersonByNameLike(String name, String dnName) {
        return y9PersonRepository.findByNameContainingAndDnContaining(name, dnName);
    }

    private List<Y9Person> findPersonByParentId(String parentId) {
        return y9PersonRepository.findByParentIdOrderByTabIndex(parentId);
    }

    private List<Y9Person> findPersonByParentIdAndDisabled(String parentId, boolean disabled) {
        return y9PersonRepository.findByDisabledAndParentIdOrderByTabIndex(disabled, parentId);
    }

    private List<Y9Person> findPersonByParentIdAndDisabledAndName(String parentId, boolean disabled, String name) {
        return y9PersonRepository.findByParentIdAndDisabledAndNameContainingOrderByTabIndex(parentId, disabled, name);
    }

    private List<Y9Person> findPersonByPositionId(String positionId) {
        List<Y9PersonsToPositions> pps = y9PersonsToPositionsRepository.findByPositionIdOrderByPersonOrder(positionId);
        List<String> personIdList = pps.stream().map(Y9PersonsToPositions::getPersonId).collect(Collectors.toList());
        List<Y9Person> personList = new ArrayList<>();
        for (String personId : personIdList) {
            personList.add(findPersonById(personId));
        }
        return personList;
    }

    private List<Y9Position> findPositionByNameLike(String name) {
        return y9PositionRepository.findByNameContainingOrderByTabIndexAsc(name);
    }

    private List<Y9Position> findPositionByNameLike(String name, String dnName) {
        return y9PositionRepository.findByNameContainingAndDnContainingOrderByTabIndexAsc(name, dnName);
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

    @Override
    public Y9OrgBase getOrgUnitBureau(String orgUnitId) {
        Y9OrgBase y9OrgBase = this.getOrgBase(orgUnitId);
        if (OrgTypeEnum.ORGANIZATION.getEnName().equals(y9OrgBase.getOrgType())) {
            return y9OrgBase;
        } else if (OrgTypeEnum.DEPARTMENT.getEnName().equals(y9OrgBase.getOrgType())) {
            Y9Department dept = (Y9Department) y9OrgBase;
            if (Boolean.TRUE.equals(dept.getBureau())) {
                return dept;
            } else {
                return getOrgUnitBureau(dept.getParentId());
            }
        } else {
            return getOrgUnitBureau(y9OrgBase.getParentId());
        } 
    }

    @Override
    public List<Y9OrgBase> getDeptManageTree(String id) {
        List<Y9OrgBase> childrenList = new CopyOnWriteArrayList<>();
        childrenList.addAll(findDepartmentByParentId(id));
        List<Y9Manager> list = y9ManagerRepository.findByParentIdAndGlobalManagerFalse(id);
        childrenList.addAll(list);
        try {
            // ORGBase实现了comparable接口，按照tabIndex字段升序排列
            Collections.sort(childrenList);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        return childrenList;
    }

    @Override
    public void getGuidPathRecursiveUp(StringBuilder sb, Y9OrgBase y9OrgBase) {
        if (OrgTypeEnum.PERSON.getEnName().equals(y9OrgBase.getOrgType())) {
            // 遍历开始是person，尾部的逗号不用加
            sb.insert(0, y9OrgBase.getId());

            Y9Person person = (Y9Person)y9OrgBase;
            Y9OrgBase parent = this.getOrgBase(person.getParentId());
            getGuidPathRecursiveUp(sb, parent);
        } else if (OrgTypeEnum.POSITION.getEnName().equals(y9OrgBase.getOrgType())) {
            // 遍历开始时，一定是position，尾部的逗号不用加
            sb.insert(0, String.format("%05d", y9OrgBase.getTabIndex()));

            Y9Position position = (Y9Position)y9OrgBase;
            Y9OrgBase parent = this.getOrgBase(position.getParentId());
            getOrderedPathRecursiveUp(sb, parent);
        } else if (OrgTypeEnum.MANAGER.getEnName().equals(y9OrgBase.getOrgType())) {
            // 遍历开始是person，尾部的逗号不用加
            sb.insert(0, y9OrgBase.getId());

            Y9Manager person = (Y9Manager)y9OrgBase;
            Y9OrgBase parent = this.getOrgBase(person.getParentId());
            getGuidPathRecursiveUp(sb, parent);
        } else if (OrgTypeEnum.DEPARTMENT.getEnName().equals(y9OrgBase.getOrgType())) {
            sb.insert(0, y9OrgBase.getId() + ",");

            Y9Department dept = (Y9Department)y9OrgBase;
            Y9OrgBase parent = this.getOrgBase(dept.getParentId());
            getGuidPathRecursiveUp(sb, parent);
        } else if (OrgTypeEnum.ORGANIZATION.getEnName().equals(y9OrgBase.getOrgType())) {
            Y9Organization org = (Y9Organization)y9OrgBase;
            // 遍历结束时，一定是org
            sb.insert(0, org.getId() + ",");
        }
    }

    @Override
    public Integer getMaxSubTabIndex(String parentId, OrgTypeEnum orgType) {
        Integer maxTabIndex = 0;
        switch (orgType) {
            case DEPARTMENT:
                Y9Department y9Department = y9DepartmentRepository.findTopByParentIdOrderByTabIndexDesc(parentId);
                if (null != y9Department && y9Department.getTabIndex() != null) {
                    maxTabIndex = y9Department.getTabIndex();
                }
                break;
            case GROUP:
                Y9Group y9Group = y9GroupRepository.findTopByParentIdOrderByTabIndexDesc(parentId);
                if (null != y9Group && y9Group.getTabIndex() != null) {
                    maxTabIndex = y9Group.getTabIndex();
                }
                break;
            case POSITION:
                Y9Position y9Position = y9PositionRepository.findTopByParentIdOrderByTabIndexDesc(parentId);
                if (null != y9Position && y9Position.getTabIndex() != null) {
                    maxTabIndex = y9Position.getTabIndex();
                }
                break;
            case PERSON:
                Y9Person y9Person = y9PersonRepository.findTopByParentIdOrderByTabIndexDesc(parentId);
                if (null != y9Person && y9Person.getTabIndex() != null) {
                    maxTabIndex = y9Person.getTabIndex();
                }
                break;
            case MANAGER:
                Y9Manager y9Manager = y9ManagerRepository.findTopByParentIdOrderByTabIndexDesc(parentId);
                if (null != y9Manager && y9Manager.getTabIndex() != null) {
                    maxTabIndex = y9Manager.getTabIndex();
                }
                break;
            default:
                maxTabIndex = 0;
        }
        return maxTabIndex + 1;
    }

    @Override
    public void getOrderedPathRecursiveUp(StringBuilder sb, Y9OrgBase y9OrgBase) {
        if (OrgTypeEnum.PERSON.getEnName().equals(y9OrgBase.getOrgType())) {
            // 遍历开始时，一定是person，尾部的逗号不用加
            sb.insert(0, String.format("%05d", y9OrgBase.getTabIndex()));

            Y9Person person = (Y9Person)y9OrgBase;
            Y9OrgBase parent = this.getOrgBase(person.getParentId());
            getOrderedPathRecursiveUp(sb, parent);
        } else if (OrgTypeEnum.MANAGER.getEnName().equals(y9OrgBase.getOrgType())) {
            // 遍历开始时，一定是manager，尾部的逗号不用加
            sb.insert(0, String.format("%05d", y9OrgBase.getTabIndex()));

            Y9Manager person = (Y9Manager)y9OrgBase;
            Y9OrgBase parent = this.getOrgBase(person.getParentId());
            getOrderedPathRecursiveUp(sb, parent);
        } else if (OrgTypeEnum.DEPARTMENT.getEnName().equals(y9OrgBase.getOrgType())) {
            sb.insert(0, String.format("%05d", y9OrgBase.getTabIndex()) + ",");

            Y9Department dept = (Y9Department)y9OrgBase;
            Y9OrgBase parent = this.getOrgBase(dept.getParentId());
            getOrderedPathRecursiveUp(sb, parent);
        } else if (OrgTypeEnum.ORGANIZATION.getEnName().equals(y9OrgBase.getOrgType())) {
            // 遍历结束时，一定是org
            sb.insert(0, String.format("%05d", y9OrgBase.getTabIndex()) + ",");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Y9OrgBase getOrgBase(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        Y9OrgBase y9OrgBase = this.findOrganizationById(id);
        if (y9OrgBase != null) {
            return y9OrgBase;
        }
        y9OrgBase = this.findDepartmentById(id);
        if (y9OrgBase != null) {
            return y9OrgBase;
        }
        y9OrgBase = this.findPersonById(id);
        if (y9OrgBase != null) {
            return y9OrgBase;
        }
        y9OrgBase = this.findPositionById(id);
        if (y9OrgBase != null) {
            return y9OrgBase;
        }
        y9OrgBase = this.findGroupById(id);
        if (y9OrgBase != null) {
            return y9OrgBase;
        }
        y9OrgBase = y9ManagerRepository.findById(id).orElse(null);
        if (y9OrgBase != null) {
            return y9OrgBase;
        }

        return null;
    }

    @Override
    public Y9OrgBase getOrgBaseDeletedByOrgId(String orgId) {
        Y9OrgBaseDeleted deleted = y9OrgBaseDeletedRepository.findByOrgId(orgId);
        if (deleted != null) {
            String jsonContent = deleted.getJsonContent();
            if (deleted.getOrgType().equals(OrgTypeEnum.ORGANIZATION.getEnName())) {
                return Y9JsonUtil.readValue(jsonContent, Y9Organization.class);
            }
            if (deleted.getOrgType().equals(OrgTypeEnum.DEPARTMENT.getEnName())) {
                return Y9JsonUtil.readValue(jsonContent, Y9Department.class);
            }
            if (deleted.getOrgType().equals(OrgTypeEnum.GROUP.getEnName())) {
                return Y9JsonUtil.readValue(jsonContent, Y9Group.class);
            }
            if (deleted.getOrgType().equals(OrgTypeEnum.POSITION.getEnName())) {
                return Y9JsonUtil.readValue(jsonContent, Y9Position.class);
            }
            if (deleted.getOrgType().equals(OrgTypeEnum.PERSON.getEnName())) {
                return Y9JsonUtil.readValue(jsonContent, Y9Person.class);
            } 
            if (deleted.getOrgType().equals(OrgTypeEnum.MANAGER.getEnName())) {
                return Y9JsonUtil.readValue(jsonContent, Y9Manager.class);
            }
        }
        return null;
    }

    private void getOrgUnitListByDownwardRecursion(List<Y9OrgBase> childrenList, String orgId) {
        List<Y9Department> depts = findDepartmentByParentId(orgId);
        if (!depts.isEmpty()) {
            childrenList.addAll(depts);
        }
        List<Y9Person> persons = findPersonByParentId(orgId);
        if (!persons.isEmpty()) {
            childrenList.addAll(persons);
        }
        List<Y9Group> groups = findGroupByParentId(orgId);
        if (!groups.isEmpty()) {
            childrenList.addAll(groups);
        }
        List<Y9Position> positions = findPositionByParentId(orgId);
        if (!positions.isEmpty()) {
            childrenList.addAll(positions);
        }
        for (Y9Department y9Department : depts) {
            getOrgUnitListByDownwardRecursion(childrenList, y9Department.getId());
        }
    }

    private void getOrgUnitListByUpwardRecursion(String parentId, List<Y9OrgBase> baseList) {
        Y9OrgBase parent = getOrgBase(parentId);
        if (parent == null) {
            LOGGER.debug("parentId={}", parentId);
        } else {
            if (!baseList.stream().anyMatch(base -> base.getId().equals(parent.getId()))) {
                baseList.add(parent);
            }
            if (parent.getOrgType().equals(OrgTypeEnum.DEPARTMENT.getEnName())) {
                Y9Department departmentParent = (Y9Department)parent;
                getOrgUnitListByUpwardRecursion(departmentParent.getParentId(), baseList);
            }
        }
    }

    @Override
    public Y9OrgBase getParent(String orgUnitId) {
        Y9OrgBase orgBase = this.getOrgBase(orgUnitId);
        Y9OrgBase parent = null;
        if (orgBase != null) {
            parent = findDepartmentById(orgBase.getParentId());
            if (parent != null) {
                return parent;
            }
            parent = findOrganizationById(orgBase.getParentId());
        }
        return parent;
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

    private void getPersonListByDownwardRecursion(String parentId, List<Y9Person> personList, Boolean disabled, String name) {
        personList.addAll(findPersonByParentIdAndDisabledAndName(parentId, disabled, name));

        List<Y9Department> deptList = findDepartmentByParentId(parentId);
        for (Y9Department dept : deptList) {
            getPersonListByDownwardRecursion(dept.getId(), personList, disabled, name);
        }
    }
    
    @Override
    public HashMap<String, Serializable> getSyncMap(String syncId, String orgType, Integer needRecursion) {
        HashMap<String, Serializable> dataMap = new HashMap<>(16);
        dataMap.put(Y9OrgEventConst.ORG_TYPE, orgType);
        dataMap.put(Y9OrgEventConst.SYNC_ID, syncId);
        dataMap.put(Y9OrgEventConst.SYNC_RECURSION, needRecursion);
        if (OrgTypeEnum.ORGANIZATION.getEnName().equals(orgType)) {
            Y9Organization y9Organization = findOrganizationById(syncId);
            dataMap.put(syncId, ModelConvertUtil.convert(y9Organization, Organization.class));
            if (needRecursion == 1) {
                syncRecursion(y9Organization.getId(), dataMap);
            }
        } else if (OrgTypeEnum.DEPARTMENT.getEnName().equals(orgType)) {
            Y9Department y9Department = findDepartmentById(syncId);
            dataMap.put(syncId, ModelConvertUtil.convert(y9Department, Department.class));
            if (needRecursion == 1) {
                syncRecursion(y9Department.getId(), dataMap);
            }
        } else if (OrgTypeEnum.GROUP.getEnName().equals(orgType)) {
            Y9Group y9Group = findGroupById(syncId);
            dataMap.put(syncId, ModelConvertUtil.convert(y9Group, Group.class));
            if (needRecursion == 1) {
                syncOrgGroupSub(y9Group, dataMap);
            }
        } else if (OrgTypeEnum.POSITION.getEnName().equals(orgType)) {
            Y9Position y9Position = findPositionById(syncId);
            dataMap.put(syncId, ModelConvertUtil.convert(y9Position, Position.class));
            if (needRecursion == 1) {
                syncOrgPositionSub(y9Position, dataMap);
            }
        } else if (OrgTypeEnum.PERSON.getEnName().equals(orgType)) {
            Y9Person y9Person = findPersonById(syncId);
            y9Person.setPassword(null);
            dataMap.put(syncId, ModelConvertUtil.convert(y9Person, Person.class));
        }
        return dataMap;
    }

    /**
     * 默认类型排序：部门->用户组->角色->岗位->人员
     */
    @Override
    public List<Y9OrgBase> getTree(String id, String treeType, boolean disabled) {
        return getTree(id, treeType, true, disabled);
    }

    @Override
    public List<Y9OrgBase> getTree(String id, String treeType, boolean isPersonIncluded, boolean disabled) {
        List<Y9OrgBase> childrenList = new CopyOnWriteArrayList<>();
        if (treeType.equals(TreeTypeConsts.TREE_TYPE_BUREAU)) {
            Y9OrgBase org = this.findOrganizationById(id);
            List<Y9Department> bureauList = y9DepartmentRepository.findByBureauAndDnContainingOrderByTabIndexAsc(true, org.getDn());
            childrenList.addAll(bureauList);
            for (Y9Department bureau : bureauList) {
                getOrgUnitListByUpwardRecursion(bureau.getParentId(), childrenList);
            }
            childrenList.remove(org);
        } else {
            childrenList.addAll(findDepartmentByParentId(id));
            if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG)
                    || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_PERSON)
                    || treeType.equals(TreeTypeConsts.TREE_TYPE_GROUP)) {
                childrenList.addAll(findGroupByParentId(id));
            }
            if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG) || treeType.equals(TreeTypeConsts.TREE_TYPE_POSITION)) {
                childrenList.addAll(findPositionByParentId(id));
            }
            if (isPersonIncluded) {
                if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG)
                        || treeType.equals(TreeTypeConsts.TREE_TYPE_PERSON)
                        || TreeTypeConsts.TREE_TYPE_ORG_PERSON.equals(treeType)) {
                    if (disabled) {
                        childrenList.addAll(findPersonByParentId(id));
                    } else {
                        childrenList.addAll(findPersonByParentIdAndDisabled(id, false));
                    }
                }
            }
        }

        try {
            // ORGBase实现了comparable接口，按照tabIndex字段升序排列
            Collections.sort(childrenList);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        return childrenList;
    }

    @Override
    public List<Y9OrgBase> getTree(String id, String treeType, String personId) {
        List<Y9OrgBase> childrenList = new ArrayList<>();
        Y9Department managerDept = findDepartmentById(Y9LoginUserHolder.getUserInfo().getParentId());
        String managerDeptGuidPath = managerDept.getGuidPath();
        String managerDeptId = managerDept.getId();

        if (treeType.equals(TreeTypeConsts.TREE_TYPE_BUREAU)) {
            Y9OrgBase org = this.findOrganizationById(id);
            List<Y9Department> bureauList = y9DepartmentRepository.findByBureauAndDnContainingOrderByTabIndexAsc(true, org.getDn());
            childrenList.addAll(bureauList);
            for (Y9Department bureau : bureauList) {
                getOrgUnitListByUpwardRecursion(bureau.getParentId(), childrenList);
            }
            childrenList.remove(org);
        } else {
            List<Y9Department> deptList = findDepartmentByParentId(id);
            for (Y9Department dept : deptList) {
                String checkPath = dept.getGuidPath();
                if (managerDeptGuidPath.contains(checkPath)) {
                    childrenList.add(dept);
                    break;
                }
            }
            Y9Department y9Department = findDepartmentById(id);
            if (y9Department != null) {
                String checkPath = y9Department.getGuidPath();
                if (managerDeptId.equals(id) || checkPath.contains(managerDeptGuidPath)) {
                    childrenList.addAll(findDepartmentByParentId(id));
                    if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG)
                            || treeType.equals(TreeTypeConsts.TREE_TYPE_GROUP) 
                            || TreeTypeConsts.TREE_TYPE_ORG_PERSON.equals(treeType)) {
                        childrenList.addAll(findGroupByParentId(id));
                    }
                    if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG)
                            || treeType.equals(TreeTypeConsts.TREE_TYPE_POSITION)) {
                        childrenList.addAll(findPositionByParentId(id));
                    }
                    if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG)
                            || treeType.equals(TreeTypeConsts.TREE_TYPE_PERSON)
                            || TreeTypeConsts.TREE_TYPE_ORG_PERSON.equals(treeType)) {
                        childrenList.addAll(findPersonByParentId(id));
                    }

                } else if (managerDeptGuidPath.contains(checkPath)) {
                    if (managerDept.getParentId().equals(id)) {
                        // childrenList.add(y9Department);
                    } else {
                        deptIdUpToOrg(managerDept.getParentId(), childrenList, id);
                    }
                }
            }
        }
        try {
            Collections.sort(childrenList);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return childrenList;
    }

    @Override
    public List<Y9OrgBase> listAllOrgUnits(String orgId) {
        List<Y9OrgBase> childrenList = new ArrayList<>();
        List<Y9Department> depts = findDepartmentByParentId(orgId);
        List<Y9Person> persons = findPersonByParentId(orgId);
        if (!depts.isEmpty()) {
            childrenList.addAll(depts);
        }
        if (!persons.isEmpty()) {
            childrenList.addAll(persons);
        }
        for (Y9Department y9Department : depts) {
            getOrgUnitListByDownwardRecursion(childrenList, y9Department.getId());
        }
        List<Y9Group> groups = findGroupByParentId(orgId);
        if (!groups.isEmpty()) {
            childrenList.addAll(groups);
        }
        List<Y9Position> positions = findPositionByParentId(orgId);
        if (!positions.isEmpty()) {
            childrenList.addAll(positions);
        }
        return childrenList;
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
            dept.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.DEPARTMENT) + dept.getName() + OrgLevelConsts.SEPARATOR + parent.getDn());
            dept.setGuidPath(parent.getGuidPath() + OrgLevelConsts.SEPARATOR + dept.getId());
            y9DepartmentRepository.save(dept);
            recursivelyUpdateProperties(dept);
        }

        // 用户组
        List<Y9Group> groupList = findGroupByParentId(parent.getId());
        for (Y9Group group : groupList) {
            group.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.GROUP) + group.getName() + OrgLevelConsts.SEPARATOR + parent.getDn());
            group.setGuidPath(parent.getGuidPath() + OrgLevelConsts.SEPARATOR + group.getId());
            y9GroupRepository.save(group);
        }

        // 岗位
        List<Y9Position> positionList = findPositionByParentId(parent.getId());
        for (Y9Position position : positionList) {
            position.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.POSITION) + position.getName() + OrgLevelConsts.SEPARATOR + parent.getDn());
            position.setGuidPath(parent.getGuidPath() + OrgLevelConsts.SEPARATOR + position.getId());
            y9PositionRepository.save(position);
        }

        // 人员
        List<Y9Person> personList = findPersonByParentId(parent.getId());
        for (Y9Person person : personList) {
            person.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.PERSON) + person.getName() + OrgLevelConsts.SEPARATOR + parent.getDn());
            person.setGuidPath(parent.getGuidPath() + OrgLevelConsts.SEPARATOR + person.getId());
            y9PersonRepository.save(person);
        }

    }

    @Override
    public List<Y9Person> searchAllPersonsRecursionDownward(String parentId, Boolean disabled, String name) {
        List<Y9Person> personList = new ArrayList<>();
        getPersonListByDownwardRecursion(parentId, personList, disabled, name);
        return personList;
    }

    @Override
    public void sync(String syncId, String orgType, Integer needRecursion) {
        HashMap<String, Serializable> dataMap = this.getSyncMap(syncId, orgType, needRecursion);

        Y9MessageOrg event = new Y9MessageOrg(dataMap, Y9OrgEventConst.RISEORGEVENT_TYPE_SYNC, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.publishMessageOrg(event);
    }

    private void syncOrgGroupSub(Y9Group y9Group, HashMap<String, Serializable> dateMap) {

        List<Y9Person> orgPersonList = findPersonByGroupId(y9Group.getId());
        ArrayList<Person> personList = new ArrayList<>();
        for (Y9Person y9Person : orgPersonList) {
            personList.add(ModelConvertUtil.convert(y9Person, Person.class));
        }
        dateMap.put(y9Group.getId() + OrgTypeEnum.PERSON.getEnName(), personList);
        List<Y9PersonsToGroups> orgPersonsGroupsList = y9PersonsToGroupsRepository.findByGroupIdOrderByPersonOrder(y9Group.getId());
        ArrayList<PersonsGroups> personsGroupsList = new ArrayList<>();
        for (Y9PersonsToGroups y9PersonsToGroups : orgPersonsGroupsList) {
            personsGroupsList.add(ModelConvertUtil.convert(y9PersonsToGroups, PersonsGroups.class));
        }
        dateMap.put(y9Group.getId() + Y9OrgEventConst.ORG_MAPPING_PERSONSGROUPS, personsGroupsList);
    }

    private void syncOrgPositionSub(Y9Position y9Position, HashMap<String, Serializable> dateMap) {

        List<Y9Person> orgPersonList = findPersonByPositionId(y9Position.getId());
        ArrayList<Person> personList = new ArrayList<>();
        for (Y9Person y9Person : orgPersonList) {
            personList.add(ModelConvertUtil.convert(y9Person, Person.class));
        }
        dateMap.put(y9Position.getId() + OrgTypeEnum.PERSON.getEnName(), personList);

        List<Y9PersonsToPositions> orgPositionsPersonsList = y9PersonsToPositionsRepository.findByPositionId(y9Position.getId());
        ArrayList<PersonsPositions> positionsPersonsList = new ArrayList<>();
        for (Y9PersonsToPositions y9PersonsToPositions : orgPositionsPersonsList) {
            positionsPersonsList.add(ModelConvertUtil.convert(y9PersonsToPositions, PersonsPositions.class));
        }
        dateMap.put(y9Position.getId() + Y9OrgEventConst.ORG_MAPPING_PERSONSPOSITIONS, positionsPersonsList);
    }

    private void syncRecursion(String parentId, HashMap<String, Serializable> dateMap) {

        List<Y9Department> orgDeptList = findDepartmentByParentId(parentId);
        ArrayList<Department> deptList = new ArrayList<>();
        for (Y9Department orgDept : orgDeptList) {
            deptList.add(ModelConvertUtil.convert(orgDept, Department.class));
            syncRecursion(orgDept.getId(), dateMap);
        }
        dateMap.put(parentId + OrgTypeEnum.DEPARTMENT.getEnName(), deptList);

        List<Y9Group> orgGroupList = findGroupByParentId(parentId);
        ArrayList<Group> groupList = new ArrayList<>();
        for (Y9Group y9Group : orgGroupList) {
            groupList.add(ModelConvertUtil.convert(y9Group, Group.class));
            syncOrgGroupSub(y9Group, dateMap);
        }
        dateMap.put(parentId + OrgTypeEnum.GROUP.getEnName(), groupList);

        List<Y9Position> orgPositionList = findPositionByParentId(parentId);
        ArrayList<Position> positionList = new ArrayList<>();
        for (Y9Position y9Position : orgPositionList) {
            positionList.add(ModelConvertUtil.convert(y9Position, Position.class));
            syncOrgPositionSub(y9Position, dateMap);
        }
        dateMap.put(parentId + OrgTypeEnum.POSITION.getEnName(), positionList);

        List<Y9Person> orgPersonList = findPersonByParentId(parentId);
        ArrayList<Person> personList = new ArrayList<>();
        for (Y9Person y9Person : orgPersonList) {
            personList.add(ModelConvertUtil.convert(y9Person, Person.class));
        }
        dateMap.put(parentId + OrgTypeEnum.PERSON.getEnName(), personList);
    }

    /**
     * 查询顺序:机构->部门->用户组->岗位->人员->三员
     */
    @Override
    public List<Y9OrgBase> treeSearch(String name, String treeType) {
        return this.treeSearch(name, treeType, true);
    }

    @Override
    public List<Y9OrgBase> treeSearch(String name, String treeType, String dnName) {
        List<Y9OrgBase> baseList = new ArrayList<>();
        if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_POSITION)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_PERSON)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_MANAGER)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_DEPT)) {
            baseList.addAll(y9OrganizationRepository.findByNameContainingAndDnContainingOrderByTabIndexAsc(name, dnName));
            List<Y9Department> deptList = findDepartmentByNameLike(name, dnName);
            baseList.addAll(deptList);
            for (Y9Department dept : deptList) {
                getOrgUnitListByUpwardRecursion(dept.getParentId(), baseList);
            }
        }
        if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_POSITION)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_PERSON)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_GROUP)) {
            List<Y9Group> groupList = findGroupByNameLike(name, dnName);
            baseList.addAll(groupList);
            for (Y9Group group : groupList) {
                getOrgUnitListByUpwardRecursion(group.getParentId(), baseList);
            }
        }
        if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_POSITION)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_POSITION)) {
            List<Y9Position> positionList = findPositionByNameLike(name, dnName);
            baseList.addAll(positionList);
            for (Y9Position position : positionList) {
                getOrgUnitListByUpwardRecursion(position.getParentId(), baseList);
            }
        }
        if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_PERSON)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_PERSON)) {
            List<Y9Person> personList = findPersonByNameLike(name, dnName);
            baseList.addAll(personList);
            for (Y9Person person : personList) {
                getOrgUnitListByUpwardRecursion(person.getParentId(), baseList);
            }
        }
        if (TreeTypeConsts.TREE_TYPE_ORG_MANAGER.equals(treeType)) {
            List<Y9Manager> y9ManagerList = findManagerByNameLike(name, dnName);
            baseList.addAll(y9ManagerList);
            for (Y9Manager y9Manager : y9ManagerList) {
                getOrgUnitListByUpwardRecursion(y9Manager.getParentId(), baseList);
            }
        }
        try {
            Collections.sort(baseList);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return baseList;
    }

    @Override
    public List<Y9OrgBase> treeSearch(String name, String treeType, boolean isDisabledIncluded) {
        List<Y9OrgBase> baseList = new ArrayList<>();

        if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_POSITION)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_PERSON)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_MANAGER)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_DEPT)) {
            baseList.addAll(y9OrganizationRepository.findByNameContainingOrderByTabIndexAsc(name));
            List<Y9Department> deptList = findDepartmentByNameLike(name);
            baseList.addAll(deptList);
            for (Y9Department dept : deptList) {
                getOrgUnitListByUpwardRecursion(dept.getParentId(), baseList);
            }
        }
        if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_POSITION)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_PERSON)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_GROUP)) {
            List<Y9Group> groupList = findGroupByNameLike(name);
            baseList.addAll(groupList);
            for (Y9Group group : groupList) {
                getOrgUnitListByUpwardRecursion(group.getParentId(), baseList);
            }
        }
        if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_POSITION)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_POSITION)) {
            List<Y9Position> positionList = findPositionByNameLike(name);
            baseList.addAll(positionList);
            for (Y9Position position : positionList) {
                getOrgUnitListByUpwardRecursion(position.getParentId(), baseList);
            }
        }
        if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_PERSON)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_PERSON)) {
            List<Y9Person> personList;
            if (isDisabledIncluded) {
                personList = findPersonByNameLike(name);
            } else {
                personList = findByNameContainingAndDisabled(name);
            }
            baseList.addAll(personList);
            for (Y9Person person : personList) {
                getOrgUnitListByUpwardRecursion(person.getParentId(), baseList);
            }
        }
        if (TreeTypeConsts.TREE_TYPE_ORG_MANAGER.equals(treeType)) {
            List<Y9Manager> y9ManagerList = findManagerByNameLike(name);
            baseList.addAll(y9ManagerList);
            for (Y9Manager y9Manager : y9ManagerList) {
                getOrgUnitListByUpwardRecursion(y9Manager.getParentId(), baseList);
            }
        }
        try {
            Collections.sort(baseList);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return baseList;
    }

    @Override
    public Y9OrgBase getOrgUnitOrganization(String orgUnitId) {
        Y9OrgBase y9OrgBase = this.getOrgBase(orgUnitId);
        if (y9OrgBase != null) {
            if (OrgTypeEnum.ORGANIZATION.getEnName().equals(y9OrgBase.getOrgType())) {
                return y9OrgBase;
            } else {
                return getOrgUnitOrganization(y9OrgBase.getParentId());
            }
        }
        return null;
    }
}