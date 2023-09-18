package net.risesoft.service.org.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.OrgLevelConsts;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.relation.Y9PersonsToGroups;
import net.risesoft.enums.AuthorizationPrincipalTypeEnum;
import net.risesoft.enums.OrgTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9GroupManager;
import net.risesoft.manager.relation.Y9PersonsToGroupsManager;
import net.risesoft.model.Group;
import net.risesoft.repository.Y9GroupRepository;
import net.risesoft.repository.permission.Y9AuthorizationRepository;
import net.risesoft.repository.relation.Y9OrgBasesToRolesRepository;
import net.risesoft.repository.relation.Y9PersonsToGroupsRepository;
import net.risesoft.service.org.Y9GroupService;
import net.risesoft.util.Y9PublishServiceUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;
import net.risesoft.y9.util.Y9BeanUtil;
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
public class Y9GroupServiceImpl implements Y9GroupService {

    private final Y9GroupRepository y9GroupRepository;
    private final Y9PersonsToGroupsRepository y9PersonsToGroupsRepository;
    private final Y9OrgBasesToRolesRepository y9OrgBasesToRolesRepository;
    private final Y9AuthorizationRepository y9AuthorizationRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9GroupManager y9GroupManager;
    private final Y9PersonsToGroupsManager y9PersonsToGroupsManager;

    @Override
    @Transactional(readOnly = false)
    public Y9Group createGroup(Y9Group y9Group) {
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(y9Group.getParentId());
        if (y9Group == null || parent == null) {
            return null;
        }
        if (StringUtils.isBlank(y9Group.getId())) {
            y9Group.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        if (null == y9Group.getTabIndex()) {
            y9Group.setTabIndex(Integer.MAX_VALUE);
        }
        y9Group.setOrgType(OrgTypeEnum.GROUP.getEnName());
        y9Group.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.GROUP) + y9Group.getName() + OrgLevelConsts.SEPARATOR
            + parent.getDn());
        y9Group.setDisabled(false);
        y9Group.setParentId(parent.getId());

        return y9GroupManager.save(y9Group);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String groupId) {
        Y9Group y9Group = this.getById(groupId);

        y9GroupManager.delete(y9Group);

        // 删除用户组关联数据
        y9OrgBasesToRolesRepository.deleteByOrgId(groupId);
        y9PersonsToGroupsManager.deleteByGroupId(groupId);
        y9AuthorizationRepository.deleteByPrincipalIdAndPrincipalType(groupId,
            AuthorizationPrincipalTypeEnum.GROUP.getValue());

        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Group));

        Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(y9Group, Group.class),
            Y9OrgEventConst.RISEORGEVENT_TYPE_DELETE_GROUP, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "删除用户组", "删除" + y9Group.getName());

    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByParentId(String parentId) {
        List<Y9Group> groupList = listByParentId(parentId);
        for (Y9Group group : groupList) {
            delete(group.getId());
        }
    }

    @Override
    public boolean existsById(String id) {
        return y9GroupRepository.existsById(id);
    }

    @Override
    public Optional<Y9Group> findById(String id) {
        return y9GroupManager.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Y9Group getById(String id) {
        return y9GroupManager.getById(id);
    }

    @Override
    public List<Y9Group> listAll() {
        return y9GroupRepository.findAll();
    }

    @Override
    public List<Y9Group> listByDn(String dn) {
        return y9GroupRepository.getByDn(dn);
    }

    @Override
    public List<Y9Group> listByNameLike(String name) {
        return y9GroupRepository.findByNameContainingOrderByTabIndexAsc(name);
    }

    @Override
    public List<Y9Group> listByNameLike(String name, String dn) {
        return y9GroupRepository.findByNameContainingAndDnContainingOrderByTabIndex(name, dn);
    }

    @Override
    public List<Y9Group> listByParentId(String parentId) {
        return y9GroupRepository.findByParentIdOrderByTabIndexAsc(parentId);
    }

    @Override
    public List<Y9Group> listByPersonId(String personId) {
        List<Y9PersonsToGroups> y9PersonsToGroupsList =
            y9PersonsToGroupsRepository.findByPersonIdOrderByGroupOrder(personId);
        List<Y9Group> groupList = new ArrayList<>();
        for (Y9PersonsToGroups y9PersonsToGroups : y9PersonsToGroupsList) {
            Optional<Y9Group> optionalY9Group = y9GroupManager.findById(y9PersonsToGroups.getGroupId());
            optionalY9Group.ifPresent(groupList::add);
        }
        return groupList;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Group move(String groupId, String parentId) {
        Y9Group originY9Group = this.getById(groupId);
        Y9Group updatedY9Group = new Y9Group();
        Y9BeanUtil.copyProperties(originY9Group, updatedY9Group);

        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(parentId);
        updatedY9Group.setParentId(parent.getId());
        updatedY9Group.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.GROUP) + updatedY9Group.getName()
            + OrgLevelConsts.SEPARATOR + parent.getDn());
        updatedY9Group.setGuidPath(parent.getGuidPath() + OrgLevelConsts.SEPARATOR + updatedY9Group.getId());
        updatedY9Group = y9GroupManager.save(updatedY9Group);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originY9Group, updatedY9Group));

        Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(updatedY9Group, Group.class),
            Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_GROUP, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "移动用户组",
            updatedY9Group.getName() + "移动到" + parent.getName());

        return updatedY9Group;
    }

    @EventListener
    public void onParentDepartmentDeleted(Y9EntityDeletedEvent<Y9Department> event) {
        Y9Department parentDepartment = event.getEntity();
        // 删除部门时其下岗位也要删除
        deleteByParentId(parentDepartment.getId());
    }

    @EventListener
    public void onParentOrganizationDeleted(Y9EntityDeletedEvent<Y9Organization> event) {
        Y9Organization y9Organization = event.getEntity();
        // 删除组织时其下岗位也要删除
        deleteByParentId(y9Organization.getId());
    }

    @Transactional(readOnly = false)
    public Y9Group saveOrder(String groupId, int tabIndex) {
        Y9Group group = this.getById(groupId);
        group.setTabIndex(tabIndex);
        group = y9GroupManager.save(group);

        Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(group, Group.class),
            Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_GROUP, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.publishMessageOrg(msg);

        return group;
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9Group> saveOrder(List<String> groupIds) {
        List<Y9Group> groupList = new ArrayList<>();

        int tabIndex = 0;
        for (String groupId : groupIds) {
            groupList.add(saveOrder(groupId, tabIndex++));

        }

        return groupList;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Group saveOrUpdate(Y9Group group) {
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(group.getParentId());

        if (StringUtils.isNotEmpty(group.getId())) {
            Optional<Y9Group> optionalY9Group = y9GroupManager.findById(group.getId());
            if (optionalY9Group.isPresent()) {
                Y9Group y9Group = optionalY9Group.get();
                y9Group.setName(group.getName());
                y9Group.setDescription(group.getDescription());
                y9Group.setParentId(parent.getId());
                y9Group.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.GROUP) + group.getName() + OrgLevelConsts.SEPARATOR
                    + parent.getDn());
                y9Group.setGuidPath(parent.getGuidPath() + OrgLevelConsts.SEPARATOR + group.getId());
                if (group.getTabIndex() != null) {
                    y9Group.setTabIndex(group.getTabIndex());
                }
                y9Group = y9GroupManager.save(y9Group);

                Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(y9Group, Group.class),
                    Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_GROUP, Y9LoginUserHolder.getTenantId());
                Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "更新用户组信息", "更新" + group.getName());

                return y9Group;
            } else {
                if (null == group.getTabIndex()) {
                    group.setTabIndex(Integer.MAX_VALUE);
                }
                group.setOrgType(OrgTypeEnum.GROUP.getEnName());
                group.setParentId(parent.getId());
                group.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.GROUP) + group.getName() + OrgLevelConsts.SEPARATOR
                    + parent.getDn());
                group.setGuidPath(parent.getGuidPath() + OrgLevelConsts.SEPARATOR + group.getId());
                group = y9GroupManager.save(group);

                Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(group, Group.class),
                    Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_GROUP, Y9LoginUserHolder.getTenantId());
                Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "新增用户组信息", "新增" + group.getName());

                return group;
            }
        }

        group.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        group.setTenantId(Y9LoginUserHolder.getTenantId());
        group.setOrgType(OrgTypeEnum.GROUP.getEnName());
        group.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.GROUP) + group.getName() + OrgLevelConsts.SEPARATOR
            + parent.getDn());
        group.setDisabled(false);
        group.setParentId(parent.getId());
        group.setTabIndex(compositeOrgBaseManager.getMaxSubTabIndex(parent.getId(), OrgTypeEnum.GROUP));
        group.setGuidPath(parent.getGuidPath() + OrgLevelConsts.SEPARATOR + group.getId());
        group = y9GroupManager.save(group);

        Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(group, Group.class),
            Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_GROUP, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "新增用户组信息", "新增" + group.getName());

        return group;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Group saveProperties(String groupId, String properties) {
        Y9Group group = this.getById(groupId);
        group.setProperties(properties);
        group = y9GroupManager.save(group);

        Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(group, Group.class),
            Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_GROUP, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.publishMessageOrg(msg);

        return group;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Group updateTabIndex(String id, int tabIndex) {
        Y9Group group = this.getById(id);
        group.setTabIndex(tabIndex);
        group = y9GroupManager.save(group);

        Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(group, Group.class),
            Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_GROUP_TABINDEX, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "更新用户组排序号", group.getName() + "的排序号更新为" + tabIndex);

        return group;
    }
}
