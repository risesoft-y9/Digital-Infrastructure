package net.risesoft.service.relation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.relation.Y9PersonsToGroups;
import net.risesoft.manager.org.Y9GroupManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.model.PersonsGroups;
import net.risesoft.repository.relation.Y9PersonsToGroupsRepository;
import net.risesoft.service.relation.Y9PersonsToGroupsService;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.util.Y9PublishServiceUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class Y9PersonsToGroupsServiceImpl implements Y9PersonsToGroupsService {

    private final Y9PersonManager y9PersonManager;
    private final Y9GroupManager y9GroupManager;

    private final Y9PersonsToGroupsRepository y9PersonsToGroupsRepository;

    @Override
    @Transactional(readOnly = false)
    public List<Y9Group> addGroups(String personId, String[] groupIds) {
        List<Y9Group> groupList = new ArrayList<>();
        Integer maxGroupsOrder = getMaxGroupOrderByPersonId(personId);
        for (int i = 0; i < groupIds.length; i++) {
            if (y9PersonsToGroupsRepository.findByGroupIdAndPersonId(groupIds[i], personId) != null) {
                continue;
            }

            Integer maxPersonsOrder = getMaxPersonOrderByGroupId(groupIds[i]);
            Y9PersonsToGroups y9PersonsToGroups = new Y9PersonsToGroups();
            y9PersonsToGroups.setGroupId(groupIds[i]);
            y9PersonsToGroups.setPersonId(personId);
            y9PersonsToGroups.setGroupOrder(maxGroupsOrder != null ? maxGroupsOrder + i + 1 : i);
            y9PersonsToGroups.setPersonOrder(maxPersonsOrder != null ? maxPersonsOrder + 1 : 0);
            y9PersonsToGroups = y9PersonsToGroupsRepository.save(y9PersonsToGroups);

            Y9Context.publishEvent(new Y9EntityCreatedEvent<>(y9PersonsToGroups));

            Y9Person person = y9PersonManager.getById(personId);
            Y9Group group = y9GroupManager.getById(groupIds[i]);
            Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.convert(y9PersonsToGroups, PersonsGroups.class), Y9OrgEventConst.RISEORGEVENT_TYPE_GROUP_ADDPERSON, Y9LoginUserHolder.getTenantId());
            Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "添加用户组人员", group.getName() + "添加用户组成员" + person.getName());

            groupList.add(group);
        }
        return groupList;
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9Person> addPersons(String groupId, String[] personIds) {
        List<Y9Person> personList = new ArrayList<>();
        Integer maxPersonsOrder = getMaxPersonOrderByGroupId(groupId);
        for (int i = 0; i < personIds.length; i++) {
            Y9Person person = y9PersonManager.getById(personIds[i]);
            if ((null == person) || (y9PersonsToGroupsRepository.findByGroupIdAndPersonId(groupId, person.getId()) != null)) {
                continue;
            }
            Integer maxGroupsOrder = getMaxGroupOrderByPersonId(personIds[i]);
            Y9PersonsToGroups y9PersonsToGroups = new Y9PersonsToGroups();
            y9PersonsToGroups.setGroupId(groupId);
            y9PersonsToGroups.setPersonId(person.getId());
            y9PersonsToGroups.setPersonOrder(maxPersonsOrder != null ? maxPersonsOrder + i + 1 : i);
            y9PersonsToGroups.setGroupOrder(maxGroupsOrder != null ? maxGroupsOrder + 1 : 0);
            y9PersonsToGroups = y9PersonsToGroupsRepository.save(y9PersonsToGroups);

            Y9Context.publishEvent(new Y9EntityCreatedEvent<>(y9PersonsToGroups));

            Y9Group group = y9GroupManager.getById(groupId);
            Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.convert(y9PersonsToGroups, PersonsGroups.class), Y9OrgEventConst.RISEORGEVENT_TYPE_GROUP_ADDPERSON, Y9LoginUserHolder.getTenantId());
            Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "添加用户组人员", group.getName() + "添加用户组成员" + person.getName());

            personList.add(person);
        }
        return personList;
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByGroupId(String groupId) {
        y9PersonsToGroupsRepository.deleteByGroupId(groupId);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByPersonId(String personId) {
        y9PersonsToGroupsRepository.deleteByPersonId(personId);
    }

    @Override
    public Y9PersonsToGroups getByGroupIdAndPersonId(String groupId, String personId) {
        return y9PersonsToGroupsRepository.findByGroupIdAndPersonId(groupId, personId);
    }

    @Override
    public Integer getMaxGroupOrderByPersonId(String personId) {
        Y9PersonsToGroups gp = y9PersonsToGroupsRepository.findTopByPersonIdOrderByGroupOrderDesc(personId);
        if (gp != null) {
            return gp.getGroupOrder();
        }
        return 0;
    }

    @Override
    public Integer getMaxPersonOrderByGroupId(String groupId) {
        Y9PersonsToGroups gp = y9PersonsToGroupsRepository.findTopByGroupIdOrderByPersonOrderDesc(groupId);
        if (gp != null) {
            return gp.getPersonOrder();
        }
        return 0;
    }

    @Override
    public List<Y9PersonsToGroups> listByGroupId(String groupId) {
        return y9PersonsToGroupsRepository.findByGroupIdOrderByPersonOrder(groupId);
    }

    @Override
    public List<String> listGroupIdsByPersonId(String personId) {
        List<Y9PersonsToGroups> pgList = y9PersonsToGroupsRepository.findByPersonIdOrderByGroupOrder(personId);
        return pgList.stream().map(Y9PersonsToGroups::getGroupId).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9PersonsToGroups> orderGroups(String personId, String[] groupIds) {
        Y9Person person = y9PersonManager.getById(personId);

        List<Y9PersonsToGroups> personsGroupsList = new ArrayList<>();
        for (int i = 0; i < groupIds.length; i++) {
            Y9PersonsToGroups y9PersonsToGroups = y9PersonsToGroupsRepository.findByGroupIdAndPersonId(groupIds[i], personId);
            y9PersonsToGroups.setGroupOrder(i);
            Y9PersonsToGroups save = y9PersonsToGroupsRepository.save(y9PersonsToGroups);

            Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.convert(y9PersonsToGroups, PersonsGroups.class), Y9OrgEventConst.RISEORGEVENT_TYPE_GROUP_ORDER, Y9LoginUserHolder.getTenantId());
            Y9Group group = y9GroupManager.getById(y9PersonsToGroups.getGroupId());
            Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "人员用户组排序", person.getName() + "的用户组：" + group.getName() + "排序更新为" + y9PersonsToGroups.getGroupOrder());

            personsGroupsList.add(save);
        }
        return personsGroupsList;
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9PersonsToGroups> orderPersons(String groupId, String[] personIds) {
        Y9Group group = y9GroupManager.getById(groupId);

        List<Y9PersonsToGroups> personsGroupsList = new ArrayList<>();
        for (int i = 0; i < personIds.length; i++) {
            Y9PersonsToGroups y9PersonsToGroups = y9PersonsToGroupsRepository.findByGroupIdAndPersonId(groupId, personIds[i]);
            y9PersonsToGroups.setPersonOrder(i);
            y9PersonsToGroups = y9PersonsToGroupsRepository.save(y9PersonsToGroups);

            Y9Person person = y9PersonManager.getById(y9PersonsToGroups.getPersonId());
            Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.convert(y9PersonsToGroups, PersonsGroups.class), Y9OrgEventConst.RISEORGEVENT_TYPE_GROUP_ORDER, Y9LoginUserHolder.getTenantId());
            Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "用户组人员排序", group.getName() + "的成员" + person.getName() + "排序更新为" + y9PersonsToGroups.getPersonOrder());

            personsGroupsList.add(y9PersonsToGroups);
        }
        return personsGroupsList;
    }

    @Override
    @Transactional(readOnly = false)
    public void removeGroups(String personId, String[] groupIds) {
        for (String groupId : groupIds) {
            Y9PersonsToGroups y9PersonsToGroups = this.getByGroupIdAndPersonId(personId, groupId);

            Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9PersonsToGroups));

            Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.convert(y9PersonsToGroups, PersonsGroups.class), Y9OrgEventConst.RISEORGEVENT_TYPE_GROUP_REMOVEPERSON, Y9LoginUserHolder.getTenantId());
            Y9Group group = y9GroupManager.getById(y9PersonsToGroups.getGroupId());
            Y9Person person = y9PersonManager.getById(personId);
            Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "移除用户组人员", group.getName() + "移除用户组成员" + person.getName());

            y9PersonsToGroupsRepository.deleteByGroupIdAndPersonId(groupId, personId);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void removePersons(String groupId, String[] personIds) {
        for (String personId : personIds) {
            Y9PersonsToGroups y9PersonsToGroups = this.getByGroupIdAndPersonId(groupId, personId);

            Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9PersonsToGroups));

            Y9Person person = y9PersonManager.getById(y9PersonsToGroups.getPersonId());
            Y9Group group = y9GroupManager.getById(groupId);
            Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.convert(y9PersonsToGroups, PersonsGroups.class), Y9OrgEventConst.RISEORGEVENT_TYPE_GROUP_REMOVEPERSON, Y9LoginUserHolder.getTenantId());
            Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "移除用户组人员", group.getName() + "移除用户组成员" + person.getName());

            y9PersonsToGroupsRepository.deleteByGroupIdAndPersonId(groupId, personId);
        }
    }
}
