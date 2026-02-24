package net.risesoft.service.org.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.org.Y9Department;
import net.risesoft.entity.org.Y9Group;
import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.entity.org.Y9Organization;
import net.risesoft.entity.relation.Y9PersonsToGroups;
import net.risesoft.enums.AuditLogEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9GroupManager;
import net.risesoft.manager.relation.Y9PersonsToGroupsManager;
import net.risesoft.model.platform.org.Group;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.repository.org.Y9GroupRepository;
import net.risesoft.repository.relation.Y9PersonsToGroupsRepository;
import net.risesoft.service.org.Y9GroupService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9StringUtil;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
public class Y9GroupServiceImpl implements Y9GroupService {

    private final Y9GroupRepository y9GroupRepository;
    private final Y9PersonsToGroupsRepository y9PersonsToGroupsRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9GroupManager y9GroupManager;
    private final Y9PersonsToGroupsManager y9PersonsToGroupsManager;

    @Override
    @Transactional
    public Group changeDisabled(String id) {
        Y9Group currentGroup = y9GroupManager.getById(id);
        Y9Group originalGroup = PlatformModelConvertUtil.convert(currentGroup, Y9Group.class);

        Boolean disableStatus = currentGroup.changeDisabled();
        Y9Group savedGroup = y9GroupManager.update(currentGroup, originalGroup);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.GROUP_UPDATE_DISABLED.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.GROUP_UPDATE_DISABLED.getDescription(), savedGroup.getName(),
                disableStatus ? "禁用" : "启用"))
            .objectId(id)
            .oldObject(originalGroup)
            .currentObject(savedGroup)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return PlatformModelConvertUtil.y9GroupToGroup(savedGroup);
    }

    @Override
    @Transactional
    public void delete(String groupId) {
        Y9Group y9Group = y9GroupManager.getById(groupId);

        // 删除用户组关联数据
        y9PersonsToGroupsManager.deleteByGroupId(groupId);
        y9GroupManager.delete(y9Group);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.GROUP_DELETE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.GROUP_DELETE.getDescription(), y9Group.getName()))
            .objectId(groupId)
            .oldObject(y9Group)
            .currentObject(null)
            .build();
        Y9Context.publishEvent(auditLogEvent);
    }

    @Override
    @Transactional
    public void deleteByParentId(String parentId) {
        List<Group> groupList = listByParentId(parentId, null);
        for (Group group : groupList) {
            delete(group.getId());
        }
    }

    @Override
    public Optional<Group> findById(String id) {
        return y9GroupManager.findByIdFromCache(id).map(PlatformModelConvertUtil::y9GroupToGroup);
    }

    @Override
    public Group getById(String id) {
        return PlatformModelConvertUtil.y9GroupToGroup(y9GroupManager.getByIdFromCache(id));
    }

    @Override
    public List<Group> listByIds(List<String> ids) {
        List<Group> groupList = new ArrayList<>();
        for (String groupId : ids) {
            Optional<Group> groupOptional = findById(groupId);
            groupOptional.ifPresent(groupList::add);
        }
        return groupList;
    }

    @Override
    public List<Group> listByDn(String dn, Boolean disabled) {
        List<Y9Group> y9GroupList;
        if (disabled == null) {
            y9GroupList = y9GroupRepository.findByDn(dn);
        } else {
            y9GroupList = y9GroupRepository.findByDnAndDisabled(dn, disabled);
        }
        return PlatformModelConvertUtil.y9GroupToGroup(y9GroupList);
    }

    @Override
    public List<Group> listByNameLike(String name) {
        List<Y9Group> y9GroupList = y9GroupRepository.findByNameContainingOrderByTabIndexAsc(name);
        return PlatformModelConvertUtil.y9GroupToGroup(y9GroupList);
    }

    @Override
    public List<Group> listByParentId(String parentId, Boolean disabled) {
        List<Y9Group> y9GroupList;
        if (disabled == null) {
            y9GroupList = y9GroupRepository.findByParentIdOrderByTabIndexAsc(parentId);
        } else {
            y9GroupList = y9GroupRepository.findByParentIdAndDisabledOrderByTabIndexAsc(parentId, disabled);
        }
        return PlatformModelConvertUtil.y9GroupToGroup(y9GroupList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> listByPersonId(String personId, Boolean disabled) {
        List<Y9PersonsToGroups> y9PersonsToGroupsList =
            y9PersonsToGroupsRepository.findByPersonIdOrderByGroupOrder(personId);
        List<Y9Group> groupList = new ArrayList<>();
        for (Y9PersonsToGroups y9PersonsToGroups : y9PersonsToGroupsList) {
            Optional<Y9Group> optionalY9Group = y9GroupManager.findByIdFromCache(y9PersonsToGroups.getGroupId());
            if (optionalY9Group.isPresent()) {
                Y9Group y9Group = optionalY9Group.get();
                if (disabled == null || disabled.equals(y9Group.getDisabled())) {
                    groupList.add(y9Group);
                }
            }
        }
        return PlatformModelConvertUtil.y9GroupToGroup(groupList);
    }

    @Override
    @Transactional
    public Group move(String id, String parentId) {
        Y9Group currentGroup = y9GroupManager.getById(id);
        Y9Group originalGroup = PlatformModelConvertUtil.convert(currentGroup, Y9Group.class);

        Y9OrgBase parentToMove = compositeOrgBaseManager.getOrgUnitAsParent(parentId);
        currentGroup.changeParent(parentToMove, compositeOrgBaseManager.getNextSubTabIndex(parentId));

        Y9Group savedGroup = y9GroupManager.update(currentGroup, originalGroup);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.GROUP_UPDATE_PARENTID.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.GROUP_UPDATE_PARENTID.getDescription(), savedGroup.getName(),
                parentToMove.getName()))
            .objectId(id)
            .oldObject(originalGroup)
            .currentObject(savedGroup)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return PlatformModelConvertUtil.y9GroupToGroup(savedGroup);
    }

    @Override
    @Transactional
    public Group saveOrUpdate(Group group) {
        if (StringUtils.isNotBlank(group.getId())) {
            Optional<Y9Group> groupOptional = y9GroupManager.findById(group.getId());
            if (groupOptional.isPresent()) {
                Y9Group y9Group = groupOptional.get();
                Y9Group originalGroup = PlatformModelConvertUtil.convert(y9Group, Y9Group.class);

                Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(group.getParentId());
                y9Group.update(group, parent);
                Y9Group savedGroup = y9GroupManager.update(y9Group, originalGroup);

                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(AuditLogEnum.GROUP_UPDATE.getAction())
                    .description(Y9StringUtil.format(AuditLogEnum.GROUP_UPDATE.getDescription(), savedGroup.getName()))
                    .objectId(savedGroup.getId())
                    .oldObject(originalGroup)
                    .currentObject(savedGroup)
                    .build();
                Y9Context.publishEvent(auditLogEvent);

                return PlatformModelConvertUtil.y9GroupToGroup(savedGroup);
            }
        }

        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(group.getParentId());
        Integer nextSubTabIndex = compositeOrgBaseManager.getNextSubTabIndex(parent.getId());
        Y9Group y9Group = new Y9Group(group, parent, nextSubTabIndex);
        Y9Group savedGroup = y9GroupManager.insert(y9Group);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.GROUP_CREATE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.GROUP_CREATE.getDescription(), savedGroup.getName()))
            .objectId(savedGroup.getId())
            .oldObject(null)
            .currentObject(savedGroup)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return PlatformModelConvertUtil.y9GroupToGroup(savedGroup);
    }

    @Override
    @Transactional
    public Group saveProperties(String id, String properties) {
        Y9Group currentGroup = y9GroupManager.getById(id);
        Y9Group originalGroup = PlatformModelConvertUtil.convert(currentGroup, Y9Group.class);

        currentGroup.changeProperties(properties);
        Y9Group savedGroup = y9GroupManager.update(currentGroup, originalGroup);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.GROUP_UPDATE_PROPERTIES.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.GROUP_UPDATE_PROPERTIES.getDescription(),
                savedGroup.getName(), savedGroup.getProperties()))
            .objectId(id)
            .oldObject(originalGroup)
            .currentObject(savedGroup)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return PlatformModelConvertUtil.y9GroupToGroup(savedGroup);
    }

    @EventListener
    @Transactional
    public void onParentDepartmentDeleted(Y9EntityDeletedEvent<Y9Department> event) {
        Y9Department parentDepartment = event.getEntity();
        // 删除部门时其下岗位也要删除
        deleteByParentId(parentDepartment.getId());
    }

    @EventListener
    @Transactional
    public void onParentOrganizationDeleted(Y9EntityDeletedEvent<Y9Organization> event) {
        Y9Organization y9Organization = event.getEntity();
        // 删除组织时其下岗位也要删除
        deleteByParentId(y9Organization.getId());
    }

    @EventListener
    @Transactional
    public void onParentUpdated(Y9EntityUpdatedEvent<? extends Y9OrgBase> event) {
        Y9OrgBase originOrgBase = event.getOriginEntity();
        Y9OrgBase updatedOrgBase = event.getUpdatedEntity();

        if (Y9OrgUtil.isCurrentOrAncestorChanged(originOrgBase, updatedOrgBase)) {
            List<Y9Group> groupList = y9GroupRepository.findByParentIdOrderByTabIndexAsc(updatedOrgBase.getId());
            for (Y9Group group : groupList) {
                Y9Group originalGroup = PlatformModelConvertUtil.convert(group, Y9Group.class);

                group.update(new Group(), updatedOrgBase);
                y9GroupManager.update(group, originalGroup);
            }
        }
    }
}
