package net.risesoft.service.org.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9DepartmentManager;
import net.risesoft.manager.org.Y9GroupManager;
import net.risesoft.manager.org.Y9OrganizationManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.manager.org.Y9PositionManager;
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
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.util.Y9PublishServiceUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;

/**
 * @author shidaobang
 * @date 2023/07/31
 * @since 9.6.3
 */
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CompositeOrgBaseServiceImpl implements CompositeOrgBaseService {

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9DepartmentManager y9DepartmentManager;
    private final Y9GroupManager y9GroupManager;
    private final Y9PositionManager y9PositionManager;
    private final Y9PersonManager y9PersonManager;
    private final Y9OrganizationManager y9OrganizationManager;

    private final Y9PersonsToGroupsRepository y9PersonsToGroupsRepository;
    private final Y9PersonsToPositionsRepository y9PersonsToPositionsRepository;
    private final Y9DepartmentRepository y9DepartmentRepository;
    private final Y9GroupRepository y9GroupRepository;
    private final Y9OrganizationRepository y9OrganizationRepository;
    private final Y9PersonRepository y9PersonRepository;
    private final Y9PositionRepository y9PositionRepository;
    private final Y9ManagerRepository y9ManagerRepository;
    private final Y9OrgBaseDeletedRepository y9OrgBaseDeletedRepository;

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

    @Override
    public Optional<Y9OrgBase> findOrgUnit(String orgUnitId) {
        return compositeOrgBaseManager.findOrgUnit(orgUnitId);
    }

    @Override
    public Optional<Y9OrgBase> findOrgUnitAsParent(String orgUnitId) {
        return compositeOrgBaseManager.findOrgUnitAsParent(orgUnitId);
    }

    @Override
    public Optional<Y9OrgBase> findOrgUnitBureau(String orgUnitId) {
        return compositeOrgBaseManager.findOrgUnitBureau(orgUnitId);
    }

    @Override
    public Optional<Y9OrgBase> findOrgUnitDeleted(String orgUnitId) {
        Optional<Y9OrgBaseDeleted> optionalY9OrgBaseDeleted = y9OrgBaseDeletedRepository.findByOrgId(orgUnitId);
        if (optionalY9OrgBaseDeleted.isPresent()) {
            Y9OrgBaseDeleted y9OrgBaseDeleted = optionalY9OrgBaseDeleted.get();
            String jsonContent = y9OrgBaseDeleted.getJsonContent();
            if (y9OrgBaseDeleted.getOrgType().equals(OrgTypeEnum.ORGANIZATION.getEnName())) {
                return Optional.ofNullable(Y9JsonUtil.readValue(jsonContent, Y9Organization.class));
            }
            if (y9OrgBaseDeleted.getOrgType().equals(OrgTypeEnum.DEPARTMENT.getEnName())) {
                return Optional.ofNullable(Y9JsonUtil.readValue(jsonContent, Y9Department.class));
            }
            if (y9OrgBaseDeleted.getOrgType().equals(OrgTypeEnum.GROUP.getEnName())) {
                return Optional.ofNullable(Y9JsonUtil.readValue(jsonContent, Y9Group.class));
            }
            if (y9OrgBaseDeleted.getOrgType().equals(OrgTypeEnum.POSITION.getEnName())) {
                return Optional.ofNullable(Y9JsonUtil.readValue(jsonContent, Y9Position.class));
            }
            if (y9OrgBaseDeleted.getOrgType().equals(OrgTypeEnum.PERSON.getEnName())) {
                return Optional.ofNullable(Y9JsonUtil.readValue(jsonContent, Y9Person.class));
            }
            if (y9OrgBaseDeleted.getOrgType().equals(OrgTypeEnum.MANAGER.getEnName())) {
                return Optional.ofNullable(Y9JsonUtil.readValue(jsonContent, Y9Manager.class));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Y9Organization> findOrgUnitOrganization(String orgUnitId) {
        Optional<Y9OrgBase> y9OrgBaseOptional = this.findOrgUnit(orgUnitId);
        if (y9OrgBaseOptional.isPresent()) {
            Y9OrgBase y9OrgBase = y9OrgBaseOptional.get();
            if (OrgTypeEnum.ORGANIZATION.getEnName().equals(y9OrgBase.getOrgType())) {
                return Optional.of((Y9Organization)y9OrgBase);
            } else {
                return findOrgUnitOrganization(y9OrgBase.getParentId());
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Y9OrgBase> findOrgUnitParent(String orgUnitId) {
        return compositeOrgBaseManager.findOrgUnitParent(orgUnitId);
    }

    @Override
    @Transactional(readOnly = true)
    public Y9OrgBase getOrgUnit(String orgUnitId) {
        return compositeOrgBaseManager.getOrgUnit(orgUnitId);
    }

    @Override
    public Y9OrgBase getOrgUnitAsParent(String orgUnitId) {
        return compositeOrgBaseManager.getOrgUnitAsParent(orgUnitId);
    }

    @Override
    public Y9OrgBase getOrgUnitBureau(String orgUnitId) {
        return compositeOrgBaseManager.getOrgUnitBureau(orgUnitId);
    }

    @Override
    public Y9OrgBase getOrgUnitDeleted(String orgUnitId) {
        return this.findOrgUnitDeleted(orgUnitId).orElse(null);
    }

    @Override
    public Y9Organization getOrgUnitOrganization(String orgUnitId) {
        return this.findOrgUnitOrganization(orgUnitId).orElse(null);
    }

    @Override
    public Y9OrgBase getOrgUnitParent(String orgUnitId) {
        return compositeOrgBaseManager.getOrgUnitParent(orgUnitId);
    }

    @Override
    public HashMap<String, Serializable> getSyncMap(String syncId, String orgType, Integer needRecursion) {
        HashMap<String, Serializable> dataMap = new HashMap<>(16);
        dataMap.put(Y9OrgEventConst.ORG_TYPE, orgType);
        dataMap.put(Y9OrgEventConst.SYNC_ID, syncId);
        dataMap.put(Y9OrgEventConst.SYNC_RECURSION, needRecursion);
        if (OrgTypeEnum.ORGANIZATION.getEnName().equals(orgType)) {
            Optional<Y9Organization> y9OrganizationOptional = y9OrganizationManager.findById(syncId);
            if (y9OrganizationOptional.isPresent()) {
                Y9Organization y9Organization = y9OrganizationOptional.get();
                dataMap.put(syncId, ModelConvertUtil.convert(y9Organization, Organization.class));
                if (needRecursion == 1) {
                    syncRecursion(y9Organization.getId(), dataMap);
                }
            }
        } else if (OrgTypeEnum.DEPARTMENT.getEnName().equals(orgType)) {
            Optional<Y9Department> y9DepartmentOptional = y9DepartmentManager.findById(syncId);
            if (y9DepartmentOptional.isPresent()) {
                Y9Department y9Department = y9DepartmentOptional.get();
                dataMap.put(syncId, ModelConvertUtil.convert(y9Department, Department.class));
                if (needRecursion == 1) {
                    syncRecursion(y9Department.getId(), dataMap);
                }
            }
        } else if (OrgTypeEnum.GROUP.getEnName().equals(orgType)) {
            Optional<Y9Group> y9GroupOptional = y9GroupManager.findById(syncId);
            if (y9GroupOptional.isPresent()) {
                Y9Group y9Group = y9GroupOptional.get();
                dataMap.put(syncId, ModelConvertUtil.convert(y9Group, Group.class));
                if (needRecursion == 1) {
                    syncOrgGroupSub(y9Group, dataMap);
                }
            }
        } else if (OrgTypeEnum.POSITION.getEnName().equals(orgType)) {
            Optional<Y9Position> y9PositionOptional = y9PositionManager.findById(syncId);
            if (y9PositionOptional.isPresent()) {
                Y9Position y9Position = y9PositionOptional.get();
                dataMap.put(syncId, ModelConvertUtil.convert(y9Position, Position.class));
                if (needRecursion == 1) {
                    syncOrgPositionSub(y9Position, dataMap);
                }
            }
        } else if (OrgTypeEnum.PERSON.getEnName().equals(orgType)) {
            Optional<Y9Person> y9PersonOptional = y9PersonManager.findById(syncId);
            if (y9PersonOptional.isPresent()) {
                Y9Person y9Person = y9PersonOptional.get();
                y9Person.setPassword(null);
                dataMap.put(syncId, ModelConvertUtil.convert(y9Person, Person.class));
            }
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
            Optional<Y9Organization> y9OrganizationOptional = y9OrganizationManager.findById(id);
            if (y9OrganizationOptional.isPresent()) {
                Y9OrgBase org = y9OrganizationOptional.get();
                List<Y9Department> bureauList =
                    y9DepartmentRepository.findByBureauAndDnContainingOrderByTabIndexAsc(true, org.getDn());
                childrenList.addAll(bureauList);
                for (Y9Department bureau : bureauList) {
                    getOrgUnitListByUpwardRecursion(bureau.getParentId(), childrenList);
                }
                childrenList.remove(org);
            }
        } else {
            childrenList.addAll(findDepartmentByParentId(id));
            if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG) || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_PERSON)
                || treeType.equals(TreeTypeConsts.TREE_TYPE_GROUP)) {
                childrenList.addAll(findGroupByParentId(id));
            }
            if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG) || treeType.equals(TreeTypeConsts.TREE_TYPE_POSITION)) {
                childrenList.addAll(findPositionByParentId(id));
            }
            if (isPersonIncluded) {
                if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG) || treeType.equals(TreeTypeConsts.TREE_TYPE_PERSON)
                    || TreeTypeConsts.TREE_TYPE_ORG_PERSON.equals(treeType)) {
                    if (disabled) {
                        childrenList.addAll(findPersonByParentId(id));
                    } else {
                        childrenList.addAll(findPersonByParentIdAndDisabled(id, false));
                    }
                }
            }
        }

        Collections.sort(childrenList);

        return childrenList;
    }

    @Override
    public List<Y9OrgBase> getTree4DeptManager(String id, String treeType) {
        List<Y9OrgBase> childrenList = new ArrayList<>();
        Optional<Y9Department> managerDeptOptional = y9DepartmentManager.findById(Y9LoginUserHolder.getDeptId());
        if (managerDeptOptional.isPresent()) {
            Y9Department managerDept = managerDeptOptional.get();

            if (treeType.equals(TreeTypeConsts.TREE_TYPE_BUREAU)) {
                Optional<Y9Organization> y9OrganizationOptional = y9OrganizationManager.findById(id);
                if (y9OrganizationOptional.isPresent()) {
                    Y9OrgBase org = y9OrganizationOptional.get();
                    List<Y9Department> bureauList =
                        y9DepartmentRepository.findByBureauAndDnContainingOrderByTabIndexAsc(true, org.getDn());
                    childrenList.addAll(bureauList);
                    for (Y9Department bureau : bureauList) {
                        getOrgUnitListByUpwardRecursion(bureau.getParentId(), childrenList);
                    }
                    childrenList.remove(org);
                }
            } else {
                List<Y9Department> deptList = findDepartmentByParentId(id);
                for (Y9Department dept : deptList) {
                    if (Y9OrgUtil.isSameOf(dept, managerDept) || Y9OrgUtil.isAncestorOf(dept, managerDept)) {
                        // 筛选当前管理员所在部门及祖先部门
                        childrenList.add(dept);
                        break;
                    }
                }
                Optional<Y9Department> targetY9DepartmentOptional = y9DepartmentManager.findById(id);
                if (targetY9DepartmentOptional.isPresent()) {
                    Y9Department y9Department = targetY9DepartmentOptional.get();
                    if (Y9OrgUtil.isSameOf(y9Department, managerDept)
                        || Y9OrgUtil.isDescendantOf(y9Department, managerDept)) {

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
                    }
                }
            }
            Collections.sort(childrenList);
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
        return compositeOrgBaseManager.listAllPersonsRecursionDownward(parentId);
    }

    @Override
    public List<Y9Person> listAllPersonsRecursionDownward(String parentId, Boolean disabled) {
        return compositeOrgBaseManager.listAllPersonsRecursionDownward(parentId, disabled);
    }

    @Override
    public List<Y9Position> listAllPositionsRecursionDownward(String parentId) {
        return compositeOrgBaseManager.listAllPositionsRecursionDownward(parentId);
    }

    @Override
    public List<Y9Person> searchAllPersonsRecursionDownward(String parentId, Boolean disabled, String name) {
        List<Y9Person> personList = new ArrayList<>();
        getPersonListByDownwardRecursion(parentId, personList, disabled, name);
        return personList;
    }

    @Override
    @Transactional(readOnly = false)
    public void sort(List<String> orgUnitIds) {
        int tabIndex = 0;
        for (String orgUnitId : orgUnitIds) {
            Y9OrgBase y9OrgBase = compositeOrgBaseManager.getOrgUnit(orgUnitId);
            String orgType = y9OrgBase.getOrgType();

            if (orgType.equals(OrgTypeEnum.DEPARTMENT.getEnName())) {

                y9DepartmentManager.updateTabIndex(y9OrgBase.getId(), tabIndex);

            } else if (orgType.equals(OrgTypeEnum.GROUP.getEnName())) {

                y9GroupManager.updateTabIndex(y9OrgBase.getId(), tabIndex);

            } else if (orgType.equals(OrgTypeEnum.POSITION.getEnName())) {

                y9PositionManager.updateTabIndex(y9OrgBase.getId(), tabIndex);

            } else if (orgType.equals(OrgTypeEnum.PERSON.getEnName())) {

                y9PersonManager.updateTabIndex(y9OrgBase.getId(), tabIndex);

            }
            tabIndex++;
        }
    }

    @Override
    public void sync(String syncId, String orgType, Integer needRecursion) {
        HashMap<String, Serializable> dataMap = this.getSyncMap(syncId, orgType, needRecursion);

        Y9MessageOrg event =
            new Y9MessageOrg(dataMap, Y9OrgEventConst.RISEORGEVENT_TYPE_SYNC, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.publishMessageOrg(event);
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
        if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG) || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_POSITION)
            || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_PERSON)
            || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_MANAGER)
            || treeType.equals(TreeTypeConsts.TREE_TYPE_DEPT)) {

            baseList.addAll(this.findOrganizationByNameLike(name, dnName));

            List<Y9Department> deptList = this.findDepartmentByNameLike(name, dnName);
            baseList.addAll(deptList);
            for (Y9Department dept : deptList) {
                this.getOrgUnitListByUpwardRecursion(dept.getParentId(), baseList);
            }
        }
        if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG) || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_POSITION)
            || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_PERSON)
            || treeType.equals(TreeTypeConsts.TREE_TYPE_GROUP)) {
            List<Y9Group> groupList = this.findGroupByNameLike(name, dnName);
            baseList.addAll(groupList);
            for (Y9Group group : groupList) {
                this.getOrgUnitListByUpwardRecursion(group.getParentId(), baseList);
            }
        }
        if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG) || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_POSITION)
            || treeType.equals(TreeTypeConsts.TREE_TYPE_POSITION)) {
            List<Y9Position> positionList = this.findPositionByNameLike(name, dnName);
            baseList.addAll(positionList);
            for (Y9Position position : positionList) {
                this.getOrgUnitListByUpwardRecursion(position.getParentId(), baseList);
            }
        }
        if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG) || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_PERSON)
            || treeType.equals(TreeTypeConsts.TREE_TYPE_PERSON)) {
            List<Y9Person> personList = this.findPersonByNameLike(name, dnName);
            baseList.addAll(personList);
            for (Y9Person person : personList) {
                this.getOrgUnitListByUpwardRecursion(person.getParentId(), baseList);
            }
        }
        if (TreeTypeConsts.TREE_TYPE_ORG_MANAGER.equals(treeType)) {
            List<Y9Manager> y9ManagerList = findManagerByNameLike(name, dnName);
            baseList.addAll(y9ManagerList);
            for (Y9Manager y9Manager : y9ManagerList) {
                this.getOrgUnitListByUpwardRecursion(y9Manager.getParentId(), baseList);
            }
        }

        Collections.sort(baseList);

        return baseList;
    }

    @Override
    public List<Y9OrgBase> treeSearch(String name, String treeType, boolean isDisabledIncluded) {
        List<Y9OrgBase> baseList = new ArrayList<>();

        if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG) || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_POSITION)
            || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_PERSON)
            || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_MANAGER)
            || treeType.equals(TreeTypeConsts.TREE_TYPE_DEPT)) {

            baseList.addAll(this.findOrganizationByNameLike(name));

            List<Y9Department> deptList = this.findDepartmentByNameLike(name);
            baseList.addAll(deptList);
            for (Y9Department dept : deptList) {
                this.getOrgUnitListByUpwardRecursion(dept.getParentId(), baseList);
            }
        }
        if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG) || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_POSITION)
            || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_PERSON)
            || treeType.equals(TreeTypeConsts.TREE_TYPE_GROUP)) {
            List<Y9Group> groupList = this.findGroupByNameLike(name);
            baseList.addAll(groupList);
            for (Y9Group group : groupList) {
                this.getOrgUnitListByUpwardRecursion(group.getParentId(), baseList);
            }
        }
        if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG) || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_POSITION)
            || treeType.equals(TreeTypeConsts.TREE_TYPE_POSITION)) {
            List<Y9Position> positionList = this.findPositionByNameLike(name);
            baseList.addAll(positionList);
            for (Y9Position position : positionList) {
                this.getOrgUnitListByUpwardRecursion(position.getParentId(), baseList);
            }
        }
        if (treeType.equals(TreeTypeConsts.TREE_TYPE_ORG) || treeType.equals(TreeTypeConsts.TREE_TYPE_ORG_PERSON)
            || treeType.equals(TreeTypeConsts.TREE_TYPE_PERSON)) {
            List<Y9Person> personList;
            if (isDisabledIncluded) {
                personList = this.findPersonByNameLike(name);
            } else {
                personList = this.findPersonByNameLikeAndDisabled(name);
            }
            baseList.addAll(personList);
            for (Y9Person person : personList) {
                this.getOrgUnitListByUpwardRecursion(person.getParentId(), baseList);
            }
        }
        if (TreeTypeConsts.TREE_TYPE_ORG_MANAGER.equals(treeType)) {
            List<Y9Manager> y9ManagerList = findManagerByNameLike(name);
            baseList.addAll(y9ManagerList);
            for (Y9Manager y9Manager : y9ManagerList) {
                this.getOrgUnitListByUpwardRecursion(y9Manager.getParentId(), baseList);
            }
        }

        Collections.sort(baseList);

        return baseList;
    }

    @Override
    public List<Y9OrgBase> treeSearch4DeptManager(String name, String treeType) {
        Y9Department y9Department = y9DepartmentManager.getById(Y9LoginUserHolder.getDeptId());
        return this.treeSearch(name, treeType, y9Department.getDn());
    }

    private List<Y9Organization> findOrganizationByNameLike(String name, String dnName) {
        return y9OrganizationRepository.findByNameContainingAndDnContainingOrderByTabIndexAsc(name, dnName);
    }

    private List<Y9Organization> findOrganizationByNameLike(String name) {
        return y9OrganizationRepository.findByNameContainingOrderByTabIndexAsc(name);
    }

    private List<Y9Person> findPersonByNameLike(String name) {
        return y9PersonRepository.findByNameContaining(name);
    }

    private List<Y9Person> findPersonByNameLike(String name, String dnName) {
        return y9PersonRepository.findByNameContainingAndDnContaining(name, dnName);
    }

    private List<Y9Person> findPersonByNameLikeAndDisabled(String name) {
        return y9PersonRepository.findByNameContainingAndDisabled(name, false);
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

    private List<Y9Position> findPositionByNameLike(String name) {
        return y9PositionRepository.findByNameContainingOrderByTabIndexAsc(name);
    }

    private List<Y9Position> findPositionByNameLike(String name, String dnName) {
        return y9PositionRepository.findByNameContainingAndDnContainingOrderByTabIndexAsc(name, dnName);
    }

    private List<Y9Position> findPositionByParentId(String parentId) {
        return y9PositionRepository.findByParentIdOrderByTabIndexAsc(parentId);
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
        Y9OrgBase parent = getOrgUnit(parentId);
        if (parent == null) {
            LOGGER.debug("parentId={}", parentId);
        } else {
            if (baseList.stream().noneMatch(base -> base.getId().equals(parent.getId()))) {
                baseList.add(parent);
            }
            if (parent.getOrgType().equals(OrgTypeEnum.DEPARTMENT.getEnName())) {
                Y9Department departmentParent = (Y9Department)parent;
                getOrgUnitListByUpwardRecursion(departmentParent.getParentId(), baseList);
            }
        }
    }

    private void getPersonListByDownwardRecursion(String parentId, List<Y9Person> personList, Boolean disabled,
        String name) {
        personList.addAll(findPersonByParentIdAndDisabledAndName(parentId, disabled, name));

        List<Y9Department> deptList = findDepartmentByParentId(parentId);
        for (Y9Department dept : deptList) {
            getPersonListByDownwardRecursion(dept.getId(), personList, disabled, name);
        }
    }

    private void syncOrgGroupSub(Y9Group y9Group, HashMap<String, Serializable> dateMap) {

        List<Y9Person> orgPersonList = y9PersonManager.listByGroupId(y9Group.getId());
        ArrayList<Person> personList = new ArrayList<>();
        for (Y9Person y9Person : orgPersonList) {
            personList.add(ModelConvertUtil.convert(y9Person, Person.class));
        }
        dateMap.put(y9Group.getId() + OrgTypeEnum.PERSON.getEnName(), personList);
        List<Y9PersonsToGroups> orgPersonsGroupsList =
            y9PersonsToGroupsRepository.findByGroupIdOrderByPersonOrder(y9Group.getId());
        ArrayList<PersonsGroups> personsGroupsList = new ArrayList<>();
        for (Y9PersonsToGroups y9PersonsToGroups : orgPersonsGroupsList) {
            personsGroupsList.add(ModelConvertUtil.convert(y9PersonsToGroups, PersonsGroups.class));
        }
        dateMap.put(y9Group.getId() + Y9OrgEventConst.ORG_MAPPING_PERSONSGROUPS, personsGroupsList);
    }

    private void syncOrgPositionSub(Y9Position y9Position, HashMap<String, Serializable> dateMap) {

        List<Y9Person> orgPersonList = y9PersonManager.listByPositionId(y9Position.getId());
        ArrayList<Person> personList = new ArrayList<>();
        for (Y9Person y9Person : orgPersonList) {
            personList.add(ModelConvertUtil.convert(y9Person, Person.class));
        }
        dateMap.put(y9Position.getId() + OrgTypeEnum.PERSON.getEnName(), personList);

        List<Y9PersonsToPositions> orgPositionsPersonsList =
            y9PersonsToPositionsRepository.findByPositionId(y9Position.getId());
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
}
