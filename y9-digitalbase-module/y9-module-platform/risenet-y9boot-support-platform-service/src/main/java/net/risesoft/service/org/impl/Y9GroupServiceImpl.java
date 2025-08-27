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
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.repository.org.Y9GroupRepository;
import net.risesoft.repository.relation.Y9PersonsToGroupsRepository;
import net.risesoft.service.org.Y9GroupService;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9.util.Y9StringUtil;

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

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9GroupManager y9GroupManager;
    private final Y9PersonsToGroupsManager y9PersonsToGroupsManager;

    @Override
    @Transactional(readOnly = false)
    public Y9Group changeDisabled(String id) {
        Y9Group currentGroup = this.getById(id);
        Y9Group originalGroup = Y9ModelConvertUtil.convert(currentGroup, Y9Group.class);
        boolean disableStatusToUpdate = !currentGroup.getDisabled();

        currentGroup.setDisabled(disableStatusToUpdate);
        Y9Group savedGroup = y9GroupManager.update(currentGroup);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.GROUP_UPDATE_DISABLED.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.GROUP_UPDATE_DISABLED.getDescription(), savedGroup.getName(),
                disableStatusToUpdate ? "禁用" : "启用"))
            .objectId(id)
            .oldObject(originalGroup)
            .currentObject(savedGroup)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedGroup;
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String groupId) {
        Y9Group y9Group = this.getById(groupId);

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
    @Transactional(readOnly = false)
    public void deleteByParentId(String parentId) {
        List<Y9Group> groupList = listByParentId(parentId, null);
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
    public Y9Group getById(String id) {
        return y9GroupManager.getById(id);
    }

    @Override
    public List<Y9Group> listAll() {
        return y9GroupRepository.findAll();
    }

    @Override
    public List<Y9Group> listByDn(String dn, Boolean disabled) {
        if (disabled == null) {
            return y9GroupRepository.findByDn(dn);
        } else {
            return y9GroupRepository.findByDnAndDisabled(dn, disabled);
        }
    }

    @Override
    public List<Y9Group> listByNameLike(String name) {
        return y9GroupRepository.findByNameContainingOrderByTabIndexAsc(name);
    }

    @Override
    public List<Y9Group> listByParentId(String parentId, Boolean disabled) {
        if (disabled == null) {
            return y9GroupRepository.findByParentIdOrderByTabIndexAsc(parentId);
        } else {
            return y9GroupRepository.findByParentIdAndDisabledOrderByTabIndexAsc(parentId, disabled);
        }
    }

    @Override
    public List<Y9Group> listByPersonId(String personId, Boolean disabled) {
        List<Y9PersonsToGroups> y9PersonsToGroupsList =
            y9PersonsToGroupsRepository.findByPersonIdOrderByGroupOrder(personId);
        List<Y9Group> groupList = new ArrayList<>();
        for (Y9PersonsToGroups y9PersonsToGroups : y9PersonsToGroupsList) {
            Optional<Y9Group> optionalY9Group = y9GroupManager.findById(y9PersonsToGroups.getGroupId());
            if (optionalY9Group.isPresent()) {
                Y9Group y9Group = optionalY9Group.get();
                if (disabled == null || disabled.equals(y9Group.getDisabled())) {
                    groupList.add(y9Group);
                }
            }
        }
        return groupList;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Group move(String id, String parentId) {
        Y9Group currentGroup = this.getById(id);
        Y9OrgBase parentToMove = compositeOrgBaseManager.getOrgUnitAsParent(parentId);
        Y9Group originalGroup = Y9ModelConvertUtil.convert(currentGroup, Y9Group.class);

        currentGroup.setParentId(parentId);
        currentGroup.setTabIndex(compositeOrgBaseManager.getNextSubTabIndex(parentId));
        Y9Group savedGroup = y9GroupManager.update(currentGroup);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.GROUP_UPDATE_PARENTID.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.GROUP_UPDATE_PARENTID.getDescription(), savedGroup.getName(),
                parentToMove.getName()))
            .objectId(id)
            .oldObject(originalGroup)
            .currentObject(savedGroup)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedGroup;
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9Group> saveOrder(List<String> groupIds) {
        List<Y9Group> groupList = new ArrayList<>();
        int tabIndex = 0;
        for (String groupId : groupIds) {
            groupList.add(y9GroupManager.updateTabIndex(groupId, tabIndex++));
        }
        return groupList;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Group saveOrUpdate(Y9Group group) {
        if (StringUtils.isNotBlank(group.getId())) {
            Optional<Y9Group> groupOptional = y9GroupManager.findByIdNotCache(group.getId());
            if (groupOptional.isPresent()) {
                Y9Group originalGroup = Y9ModelConvertUtil.convert(groupOptional.get(), Y9Group.class);
                Y9Group savedGroup = y9GroupManager.update(group);

                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(AuditLogEnum.GROUP_UPDATE.getAction())
                    .description(Y9StringUtil.format(AuditLogEnum.GROUP_UPDATE.getDescription(), savedGroup.getName()))
                    .objectId(savedGroup.getId())
                    .oldObject(originalGroup)
                    .currentObject(savedGroup)
                    .build();
                Y9Context.publishEvent(auditLogEvent);

                return savedGroup;
            }
        }

        Y9Group savedGroup = y9GroupManager.insert(group);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.GROUP_CREATE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.GROUP_CREATE.getDescription(), savedGroup.getName()))
            .objectId(savedGroup.getId())
            .oldObject(null)
            .currentObject(savedGroup)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedGroup;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Group saveProperties(String id, String properties) {
        Y9Group currentGroup = this.getById(id);
        Y9Group originalGroup = Y9ModelConvertUtil.convert(currentGroup, Y9Group.class);

        currentGroup.setProperties(properties);
        Y9Group savedGroup = y9GroupManager.update(currentGroup);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.GROUP_UPDATE_PROPERTIES.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.GROUP_UPDATE_PROPERTIES.getDescription(),
                savedGroup.getName(), savedGroup.getProperties()))
            .objectId(id)
            .oldObject(originalGroup)
            .currentObject(savedGroup)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedGroup;
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onParentDepartmentDeleted(Y9EntityDeletedEvent<Y9Department> event) {
        Y9Department parentDepartment = event.getEntity();
        // 删除部门时其下岗位也要删除
        deleteByParentId(parentDepartment.getId());
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onParentOrganizationDeleted(Y9EntityDeletedEvent<Y9Organization> event) {
        Y9Organization y9Organization = event.getEntity();
        // 删除组织时其下岗位也要删除
        deleteByParentId(y9Organization.getId());
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onParentUpdated(Y9EntityUpdatedEvent<? extends Y9OrgBase> event) {
        Y9OrgBase originOrgBase = event.getOriginEntity();
        Y9OrgBase updatedOrgBase = event.getUpdatedEntity();

        if (Y9OrgUtil.isCurrentOrAncestorChanged(originOrgBase, updatedOrgBase)) {
            List<Y9Group> groupList = y9GroupRepository.findByParentIdOrderByTabIndexAsc(updatedOrgBase.getId());
            for (Y9Group group : groupList) {
                y9GroupManager.update(group);
            }
        }
    }
}
