package net.risesoft.service.relation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.org.Y9Group;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.relation.Y9PersonsToGroups;
import net.risesoft.enums.AuditLogEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.Y9GroupManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.manager.relation.Y9PersonsToGroupsManager;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.repository.relation.Y9PersonsToGroupsRepository;
import net.risesoft.service.relation.Y9PersonsToGroupsService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
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
public class Y9PersonsToGroupsServiceImpl implements Y9PersonsToGroupsService {

    private final Y9PersonsToGroupsManager y9PersonsToGroupsManager;
    private final Y9PersonManager y9PersonManager;
    private final Y9GroupManager y9GroupManager;

    private final Y9PersonsToGroupsRepository y9PersonsToGroupsRepository;

    @Override
    @Transactional(readOnly = false)
    public void addGroups(String personId, String[] groupIds) {
        for (int i = 0; i < groupIds.length; i++) {
            String groupId = groupIds[i];
            if (y9PersonsToGroupsRepository.findByGroupIdAndPersonId(groupId, personId).isPresent()) {
                continue;
            }
            Integer nextGroupsOrder = getNextGroupOrderByPersonId(personId);
            Integer nextPersonsOrder = getNextPersonOrderByGroupId(groupId);

            this.addY9PersonsToGroups(personId, groupId, nextGroupsOrder + i, nextPersonsOrder);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void addPersons(String groupId, String[] personIds) {
        for (int i = 0; i < personIds.length; i++) {
            String personId = personIds[i];
            if (y9PersonsToGroupsRepository.findByGroupIdAndPersonId(groupId, personId).isPresent()) {
                continue;
            }
            Integer nextPersonsOrder = getNextPersonOrderByGroupId(groupId);
            Integer nextGroupsOrder = getNextGroupOrderByPersonId(personId);

            this.addY9PersonsToGroups(personId, groupId, nextGroupsOrder, nextPersonsOrder + i);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByPersonId(String personId) {
        y9PersonsToGroupsManager.deleteByPersonId(personId);
    }

    @Override
    public List<Y9PersonsToGroups> findByGroupId(String groupId) {
        return y9PersonsToGroupsRepository.findByGroupId(groupId);
    }

    @Override
    public Integer getNextGroupOrderByPersonId(String personId) {
        return y9PersonsToGroupsRepository.findTopByPersonIdOrderByGroupOrderDesc(personId)
            .map(Y9PersonsToGroups::getGroupOrder)
            .orElse(-1) + 1;
    }

    @Override
    public Integer getNextPersonOrderByGroupId(String groupId) {
        return y9PersonsToGroupsRepository.findTopByGroupIdOrderByPersonOrderDesc(groupId)
            .map(Y9PersonsToGroups::getPersonOrder)
            .orElse(-1) + 1;
    }

    @Override
    public List<Y9PersonsToGroups> listByGroupId(String groupId) {
        return y9PersonsToGroupsRepository.findByGroupIdOrderByPersonOrder(groupId);
    }

    @Override
    public List<String> listGroupIdsByPersonId(String personId) {
        return y9PersonsToGroupsRepository.findByPersonIdOrderByGroupOrder(personId)
            .stream()
            .map(Y9PersonsToGroups::getGroupId)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9PersonsToGroups> orderGroups(String personId, String[] groupIds) {
        List<Y9PersonsToGroups> personsGroupsList = new ArrayList<>();
        for (int i = 0; i < groupIds.length; i++) {

            Optional<Y9PersonsToGroups> optionalY9PersonsToGroups =
                y9PersonsToGroupsRepository.findByGroupIdAndPersonId(groupIds[i], personId);

            if (optionalY9PersonsToGroups.isPresent()) {
                Y9PersonsToGroups updatedPersonToGroups = optionalY9PersonsToGroups.get();
                Y9PersonsToGroups originalPersonToGroups = new Y9PersonsToGroups();
                Y9BeanUtil.copyProperties(updatedPersonToGroups, originalPersonToGroups);
                updatedPersonToGroups.setGroupOrder(i);
                final Y9PersonsToGroups savedPersonToGroups = y9PersonsToGroupsRepository.save(updatedPersonToGroups);

                Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originalPersonToGroups, savedPersonToGroups));

                personsGroupsList.add(savedPersonToGroups);
            }
        }
        return personsGroupsList;
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9PersonsToGroups> orderPersons(String groupId, String[] personIds) {
        List<Y9PersonsToGroups> personsGroupsList = new ArrayList<>();
        for (int i = 0; i < personIds.length; i++) {

            Optional<Y9PersonsToGroups> optionalY9PersonsToGroups =
                y9PersonsToGroupsRepository.findByGroupIdAndPersonId(groupId, personIds[i]);

            if (optionalY9PersonsToGroups.isPresent()) {
                Y9PersonsToGroups updatedPersonToGroups = optionalY9PersonsToGroups.get();
                Y9PersonsToGroups originalPersonToGroups = new Y9PersonsToGroups();
                Y9BeanUtil.copyProperties(updatedPersonToGroups, originalPersonToGroups);
                updatedPersonToGroups.setPersonOrder(i);
                final Y9PersonsToGroups savedPersonToGroups = y9PersonsToGroupsRepository.save(updatedPersonToGroups);

                Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originalPersonToGroups, savedPersonToGroups));

                personsGroupsList.add(savedPersonToGroups);
            }
        }
        return personsGroupsList;
    }

    @Override
    @Transactional(readOnly = false)
    public void removeGroups(String personId, String[] groupIds) {
        for (String groupId : groupIds) {
            remove(personId, groupId);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void removePersons(String groupId, String[] personIds) {
        for (String personId : personIds) {
            remove(personId, groupId);
        }
    }

    @Override
    public List<Y9PersonsToGroups> listByPersonId(String personId) {
        return y9PersonsToGroupsRepository.findByPersonIdOrderByGroupOrder(personId);
    }

    @Transactional(readOnly = false)
    @Override
    public Y9PersonsToGroups saveOrUpdate(Y9PersonsToGroups y9PersonsToGroups) {
        checkPersonExists(y9PersonsToGroups.getPersonId());
        checkGroupExists(y9PersonsToGroups.getGroupId());

        if (StringUtils.isNotBlank(y9PersonsToGroups.getId())) {
            Optional<Y9PersonsToGroups> y9PersonsToGroupsOptional =
                y9PersonsToGroupsRepository.findById(y9PersonsToGroups.getId());
            if (y9PersonsToGroupsOptional.isPresent()) {
                Y9PersonsToGroups originY9PersonsToGroups = y9PersonsToGroupsOptional.get();
                Y9BeanUtil.copyProperties(y9PersonsToGroups, originY9PersonsToGroups);
                y9PersonsToGroupsRepository.save(originY9PersonsToGroups);
            }
        } else {
            y9PersonsToGroups.setId(Y9IdGenerator.genId());
        }

        y9PersonsToGroups = y9PersonsToGroupsRepository.save(y9PersonsToGroups);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(y9PersonsToGroups));

        return y9PersonsToGroups;
    }

    /**
     * 检查用户组是否存在
     *
     * @param groupId 用户组 id
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    private void checkGroupExists(String groupId) {
        y9GroupManager.getById(groupId);
    }

    /**
     * 检查人员是否存在
     *
     * @param personId 人员 id
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    private void checkPersonExists(String personId) {
        y9PersonManager.getById(personId);
    }

    @Transactional(readOnly = false)
    public Y9PersonsToGroups addY9PersonsToGroups(String personId, String groupId, Integer maxGroupsOrder,
        Integer maxPersonsOrder) {
        Y9Person person = y9PersonManager.getById(personId);
        Y9Group group = y9GroupManager.getById(groupId);

        Y9PersonsToGroups y9PersonsToGroups = new Y9PersonsToGroups();
        y9PersonsToGroups.setGroupId(groupId);
        y9PersonsToGroups.setPersonId(personId);
        y9PersonsToGroups.setGroupOrder(maxGroupsOrder);
        y9PersonsToGroups.setPersonOrder(maxPersonsOrder);
        Y9PersonsToGroups savedPersonsToGroups = this.saveOrUpdate(y9PersonsToGroups);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.GROUP_ADD_PERSON.getAction())
            .description(
                Y9StringUtil.format(AuditLogEnum.GROUP_ADD_PERSON.getDescription(), group.getName(), person.getName()))
            .objectId(y9PersonsToGroups.getId())
            .oldObject(null)
            .currentObject(y9PersonsToGroups)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedPersonsToGroups;
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onPersonDeleted(Y9EntityDeletedEvent<Y9Person> event) {
        Y9Person person = event.getEntity();
        y9PersonsToGroupsRepository.deleteByPersonId(person.getId());
    }

    @Transactional(readOnly = false)
    public void remove(String personId, String groupId) {
        Y9Person person = y9PersonManager.getById(personId);
        Y9Group group = y9GroupManager.getById(groupId);

        Optional<Y9PersonsToGroups> optionalY9PersonsToGroups =
            y9PersonsToGroupsRepository.findByGroupIdAndPersonId(groupId, personId);
        if (optionalY9PersonsToGroups.isPresent()) {
            Y9PersonsToGroups y9PersonsToGroups = optionalY9PersonsToGroups.get();
            y9PersonsToGroupsManager.delete(y9PersonsToGroups);

            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(AuditLogEnum.GROUP_REMOVE_PERSON.getAction())
                .description(Y9StringUtil.format(AuditLogEnum.GROUP_REMOVE_PERSON.getDescription(), group.getName(),
                    person.getName()))
                .objectId(y9PersonsToGroups.getId())
                .oldObject(y9PersonsToGroups)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        }
    }
}
