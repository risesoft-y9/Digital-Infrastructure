package net.risesoft.service.org.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.relation.Y9PersonsToGroups;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9GroupManager;
import net.risesoft.manager.relation.Y9PersonsToGroupsManager;
import net.risesoft.repository.Y9GroupRepository;
import net.risesoft.repository.relation.Y9PersonsToGroupsRepository;
import net.risesoft.service.org.Y9GroupService;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
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
public class Y9GroupServiceImpl implements Y9GroupService {

    private final Y9GroupRepository y9GroupRepository;
    private final Y9PersonsToGroupsRepository y9PersonsToGroupsRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9GroupManager y9GroupManager;
    private final Y9PersonsToGroupsManager y9PersonsToGroupsManager;

    @Override
    @Transactional(readOnly = false)
    public Y9Group changeDisabled(String id) {
        final Y9Group y9Group = this.getById(id);

        Y9Group updatedGroup = Y9ModelConvertUtil.convert(y9Group, Y9Group.class);
        updatedGroup.setDisabled(!y9Group.getDisabled());
        return y9GroupManager.saveOrUpdate(updatedGroup);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Group createGroup(Y9Group y9Group) {
        if (y9Group == null) {
            return null;
        }
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(y9Group.getParentId());
        if (parent == null) {
            return null;
        }
        if (StringUtils.isBlank(y9Group.getId())) {
            y9Group.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        if (null == y9Group.getTabIndex()) {
            y9Group.setTabIndex(Integer.MAX_VALUE);
        }
        y9Group.setDn(Y9OrgUtil.buildDn(OrgTypeEnum.GROUP, y9Group.getName(), parent.getDn()));
        y9Group.setDisabled(false);
        y9Group.setParentId(parent.getId());

        return y9GroupManager.save(y9Group);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String groupId) {
        Y9Group y9Group = this.getById(groupId);

        // 删除用户组关联数据
        y9PersonsToGroupsManager.deleteByGroupId(groupId);
        y9GroupManager.delete(y9Group);

        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Group));
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
    public List<Y9Group> listByNameLikeAndDn(String name, String dn) {
        return y9GroupRepository.findByNameContainingAndDnContainingOrderByTabIndex(name, dn);
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
    public Y9Group move(String groupId, String parentId) {
        final Y9Group y9Group = this.getById(groupId);

        Y9Group updatedGroup = Y9ModelConvertUtil.convert(y9Group, Y9Group.class);
        updatedGroup.setParentId(parentId);
        updatedGroup.setTabIndex(compositeOrgBaseManager.getNextSubTabIndex(parentId));
        return y9GroupManager.saveOrUpdate(updatedGroup);
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
        return y9GroupManager.saveOrUpdate(group);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Group saveProperties(String groupId, String properties) {

        return y9GroupManager.saveProperties(groupId, properties);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Group updateTabIndex(String id, int tabIndex) {
        return y9GroupManager.updateTabIndex(id, tabIndex);
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
                this.saveOrUpdate(group);
            }
        }
    }
}
