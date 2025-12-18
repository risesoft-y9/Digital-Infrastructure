package net.risesoft.listener;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.org.Y9Group;
import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToGroups;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.manager.org.Y9GroupManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.manager.org.Y9PositionManager;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.PersonsGroups;
import net.risesoft.model.platform.org.PersonsPositions;
import net.risesoft.service.setting.Y9SettingService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.util.Y9PublishServiceUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.constant.Y9OrgEventTypeConst;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;
import net.risesoft.y9.util.Y9StringUtil;
import net.risesoft.y9.util.signing.Y9MessageDigestUtil;

/**
 * 组织事件监听器
 *
 * @author shidaobang
 * @date 2024/05/27
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrgEventListener {

    private final Y9GroupManager y9GroupManager;
    private final Y9PersonManager y9PersonManager;
    private final Y9PositionManager y9PositionManager;

    private final Y9SettingService y9SettingService;

    @TransactionalEventListener
    public void onOrgUnitCreated(Y9EntityCreatedEvent<? extends Y9OrgBase> event) {
        Y9OrgBase y9OrgBase = event.getEntity();
        OrgTypeEnum orgType = y9OrgBase.getOrgType();
        String eventName = Y9StringUtil.format("新增{}", orgType.getName());
        String eventDescription = Y9StringUtil.format("新增{}[{}]", orgType.getName(), y9OrgBase.getName());
        String eventType = null;
        switch (orgType) {
            case ORGANIZATION:
                eventType = Y9OrgEventTypeConst.ORGANIZATION_ADD;
                break;
            case GROUP:
                eventType = Y9OrgEventTypeConst.GROUP_ADD;
                break;
            case POSITION:
                eventType = Y9OrgEventTypeConst.POSITION_ADD;
                break;
            case DEPARTMENT:
                eventType = Y9OrgEventTypeConst.DEPARTMENT_ADD;
                break;
            case PERSON:
                eventType = Y9OrgEventTypeConst.PERSON_ADD;
                break;
        }

        if (!OrgTypeEnum.MANAGER.equals(orgType)) {
            Y9MessageOrg<OrgUnit> msg = new Y9MessageOrg<>(PlatformModelConvertUtil.orgBaseToOrgUnit(y9OrgBase),
                eventType, Y9LoginUserHolder.getTenantId());
            Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, eventName, eventDescription);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("新增组织节点后保存及发送消息到中间件完成");
            }
        }
    }

    @TransactionalEventListener
    public void onOrgUnitDeleted(Y9EntityDeletedEvent<? extends Y9OrgBase> event) {
        Y9OrgBase y9OrgBase = event.getEntity();
        OrgTypeEnum orgType = y9OrgBase.getOrgType();
        String eventName = Y9StringUtil.format("删除{}", orgType.getName());
        String eventDescription = Y9StringUtil.format("删除{}[{}]", orgType.getName(), y9OrgBase.getName());
        String eventType = null;
        switch (orgType) {
            case ORGANIZATION:
                eventType = Y9OrgEventTypeConst.ORGANIZATION_DELETE;
                break;
            case GROUP:
                eventType = Y9OrgEventTypeConst.GROUP_DELETE;
                break;
            case POSITION:
                eventType = Y9OrgEventTypeConst.POSITION_DELETE;
                break;
            case DEPARTMENT:
                eventType = Y9OrgEventTypeConst.DEPARTMENT_DELETE;
                break;
            case PERSON:
                eventType = Y9OrgEventTypeConst.PERSON_DELETE;
                break;
        }
        if (!OrgTypeEnum.MANAGER.equals(orgType)) {
            Y9MessageOrg<OrgUnit> msg = new Y9MessageOrg<>(PlatformModelConvertUtil.orgBaseToOrgUnit(y9OrgBase),
                eventType, Y9LoginUserHolder.getTenantId());
            Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, eventName, eventDescription);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("删除组织节点后保存及发送消息到中间件完成");
            }
        }
    }

    @TransactionalEventListener
    public void onOrgUnitUpdated(Y9EntityUpdatedEvent<? extends Y9OrgBase> event) {
        Y9OrgBase originOrgUnit = event.getOriginEntity();
        Y9OrgBase updatedOrgUnit = event.getUpdatedEntity();
        OrgTypeEnum orgType = updatedOrgUnit.getOrgType();

        String eventName = Y9StringUtil.format("更新{}", orgType.getName());
        String eventDescription = Y9StringUtil.format("更新{}[{}]", orgType.getName(), originOrgUnit.getName());

        if (!Objects.equals(originOrgUnit.getParentId(), updatedOrgUnit.getParentId())) {
            eventName = Y9StringUtil.format("移动{}", orgType.getName());
            eventDescription = Y9StringUtil.format("移动{}[{}]", orgType.getName(), originOrgUnit.getName());
        } else if (!Objects.equals(originOrgUnit.getDisabled(), updatedOrgUnit.getDisabled())) {
            eventName = Y9StringUtil.format("{}{}", updatedOrgUnit.getDisabled() ? "禁用" : "启用", orgType.getName());
            eventDescription = Y9StringUtil.format("{}{}[{}]", updatedOrgUnit.getDisabled() ? "禁用" : "启用",
                orgType.getName(), originOrgUnit.getName());
        } else if (!Objects.equals(originOrgUnit.getTabIndex(), updatedOrgUnit.getTabIndex())) {
            eventName = Y9StringUtil.format("更新{}排序号", orgType.getName());
            eventDescription = Y9StringUtil.format("更新{}[{}]排序号为[{}]", orgType.getName(), originOrgUnit.getName(),
                updatedOrgUnit.getTabIndex());
        }
        String eventType = null;
        switch (orgType) {
            case ORGANIZATION:
                eventType = Y9OrgEventTypeConst.ORGANIZATION_UPDATE;
                break;
            case GROUP:
                eventType = Y9OrgEventTypeConst.GROUP_UPDATE;
                break;
            case POSITION:
                eventType = Y9OrgEventTypeConst.POSITION_UPDATE;
                break;
            case DEPARTMENT:
                eventType = Y9OrgEventTypeConst.DEPARTMENT_UPDATE;
                break;
            case PERSON:
                eventType = Y9OrgEventTypeConst.PERSON_UPDATE;
                Y9Person originPerson = (Y9Person)originOrgUnit;
                Y9Person updatedPerson = (Y9Person)updatedOrgUnit;
                if (!Objects.equals(originPerson.getPassword(), updatedPerson.getPassword())) {
                    eventName = "修改密码";
                    eventDescription = Y9StringUtil.format("修改[{}]密码", updatedOrgUnit.getName());
                    String defaultPassword = y9SettingService.getTenantSetting().getUserDefaultPassword();
                    if (Y9MessageDigestUtil.bcryptMatch(defaultPassword, updatedPerson.getPassword())) {
                        eventName = "重置密码";
                        eventDescription = Y9StringUtil.format("重置[{}]密码", updatedOrgUnit.getName());
                    }
                }
                break;
        }
        if (!OrgTypeEnum.MANAGER.equals(orgType)) {
            Y9MessageOrg<OrgUnit> msg = new Y9MessageOrg<>(PlatformModelConvertUtil.orgBaseToOrgUnit(updatedOrgUnit),
                eventType, Y9LoginUserHolder.getTenantId());
            Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, eventName, eventDescription);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("更新组织节点后保存及发送消息到中间件完成");
            }
        }
    }

    @TransactionalEventListener
    public void onY9PersonsToGroupsCreated(Y9EntityCreatedEvent<Y9PersonsToGroups> event) {
        Y9PersonsToGroups y9PersonsToGroups = event.getEntity();
        Y9Person person = y9PersonManager.getByIdFromCache(y9PersonsToGroups.getPersonId());
        Y9Group group = y9GroupManager.getByIdFromCache(y9PersonsToGroups.getGroupId());
        String eventName = "用户组添加人员";
        String eventDescription = Y9StringUtil.format("用户组[{}]添加人员[{}]", group.getName(), person.getName());
        String eventType = Y9OrgEventTypeConst.GROUP_ADD_PERSON;

        Y9MessageOrg<PersonsGroups> msg =
            new Y9MessageOrg<>(PlatformModelConvertUtil.convert(y9PersonsToGroups, PersonsGroups.class), eventType,
                Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, eventName, eventDescription);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("新增Y9PersonsToGroups后保存及发送消息到中间件完成");
        }
    }

    @TransactionalEventListener
    public void onY9PersonsToGroupsDeleted(Y9EntityDeletedEvent<Y9PersonsToGroups> event) {
        Y9PersonsToGroups y9PersonsToGroups = event.getEntity();

        Optional<Y9Person> y9PersonOptional = y9PersonManager.findByIdFromCache(y9PersonsToGroups.getPersonId());
        Optional<Y9Group> y9GroupOptional = y9GroupManager.findByIdFromCache(y9PersonsToGroups.getGroupId());
        if (y9GroupOptional.isPresent() && y9PersonOptional.isPresent()) {
            Y9Person person = y9PersonOptional.get();
            Y9Group group = y9GroupOptional.get();
            String eventName = "用户组移除人员";
            String eventDescription = Y9StringUtil.format("用户组[{}]移除人员[{}]", group.getName(), person.getName());
            String eventType = Y9OrgEventTypeConst.GROUP_REMOVE_PERSON;
            Y9MessageOrg<PersonsGroups> msg =
                new Y9MessageOrg<>(PlatformModelConvertUtil.convert(y9PersonsToGroups, PersonsGroups.class), eventType,
                    Y9LoginUserHolder.getTenantId());
            Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, eventName, eventDescription);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("移除Y9PersonsToGroups后保存及发送消息到中间件完成");
            }
        }
    }

    @TransactionalEventListener
    public void onY9PersonsToGroupsUpdated(Y9EntityUpdatedEvent<Y9PersonsToGroups> event) {
        Y9PersonsToGroups originPersonToGroups = event.getOriginEntity();
        Y9PersonsToGroups updatedPersonToGroups = event.getUpdatedEntity();

        Y9Person person = y9PersonManager.getByIdFromCache(updatedPersonToGroups.getPersonId());
        Y9Group group = y9GroupManager.getByIdFromCache(updatedPersonToGroups.getGroupId());

        String eventName;
        String eventDescription;
        String eventType = Y9OrgEventTypeConst.GROUP_ORDER;
        if (!Objects.equals(originPersonToGroups.getGroupOrder(), updatedPersonToGroups.getGroupOrder())) {
            eventName = "更新人员的用户组排序";
            eventDescription = Y9StringUtil.format("更新人员[{}]的用户组[{}]排序为[{}]", person.getName(), group.getName(),
                updatedPersonToGroups.getGroupOrder());
        } else {
            eventName = "更新用户组的人员排序";
            eventDescription = Y9StringUtil.format("更新用户组[{}]的成员[{}]排序为[{}]", group.getName(), person.getName(),
                updatedPersonToGroups.getPersonOrder());
        }
        Y9MessageOrg<PersonsGroups> msg =
            new Y9MessageOrg<>(PlatformModelConvertUtil.convert(updatedPersonToGroups, PersonsGroups.class), eventType,
                Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, eventName, eventDescription);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("更新Y9PersonsToGroups后保存及发送消息到中间件完成");
        }
    }

    @TransactionalEventListener
    public void onY9PersonsToPositionsCreated(Y9EntityCreatedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions y9PersonsToPositions = event.getEntity();

        Y9Person person = y9PersonManager.getByIdFromCache(y9PersonsToPositions.getPersonId());
        Y9Position position = y9PositionManager.getByIdFromCache(y9PersonsToPositions.getPositionId());
        String eventName = "岗位添加人员";
        String eventDescription = Y9StringUtil.format("岗位[{}]添加人员[{}]", position.getName(), person.getName());
        String eventType = Y9OrgEventTypeConst.POSITION_ADD_PERSON;

        Y9MessageOrg<PersonsPositions> msg =
            new Y9MessageOrg<>(PlatformModelConvertUtil.convert(y9PersonsToPositions, PersonsPositions.class),
                eventType, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, eventName, eventDescription);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("新增Y9PersonsToPositions后保存及发送消息到中间件完成");
        }
    }

    @TransactionalEventListener
    public void onY9PersonsToPositionsDeleted(Y9EntityDeletedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions y9PersonsToPositions = event.getEntity();

        Optional<Y9Person> y9PersonOptional = y9PersonManager.findByIdFromCache(y9PersonsToPositions.getPersonId());
        Optional<Y9Position> y9PositionOptional =
            y9PositionManager.findByIdFromCache(y9PersonsToPositions.getPositionId());
        if (y9PersonOptional.isPresent() && y9PositionOptional.isPresent()) {
            Y9Person person = y9PersonOptional.get();
            Y9Position position = y9PositionOptional.get();
            String eventName = "岗位移除人员";
            String eventDescription = Y9StringUtil.format("岗位[{}]移除人员[{}]", position.getName(), person.getName());
            String eventType = Y9OrgEventTypeConst.POSITION_REMOVE_PERSON;
            Y9MessageOrg<PersonsPositions> msg =
                new Y9MessageOrg<>(PlatformModelConvertUtil.convert(y9PersonsToPositions, PersonsPositions.class),
                    eventType, Y9LoginUserHolder.getTenantId());
            Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, eventName, eventDescription);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("移除Y9PersonsToPositions后保存及发送消息到中间件完成");
            }
        }
    }

    @TransactionalEventListener
    public void onY9PersonsToPositionsUpdated(Y9EntityUpdatedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions originPersonsToPositions = event.getOriginEntity();
        Y9PersonsToPositions updatedPersonsToPositions = event.getUpdatedEntity();

        Y9Person person = y9PersonManager.getByIdFromCache(updatedPersonsToPositions.getPersonId());
        Y9Position position = y9PositionManager.getByIdFromCache(updatedPersonsToPositions.getPositionId());

        String eventName;
        String eventDescription;
        String eventType = Y9OrgEventTypeConst.POSITION_ORDER;
        if (!Objects.equals(originPersonsToPositions.getPersonOrder(), updatedPersonsToPositions.getPersonOrder())) {
            eventName = "更新岗位的人员排序";
            eventDescription = Y9StringUtil.format("更新岗位[{}]的人员[{}]排序为[{}]", position.getName(), person.getName(),
                updatedPersonsToPositions.getPersonOrder());
        } else {
            eventName = "更新人员的岗位排序";
            eventDescription = Y9StringUtil.format("更新人员[{}]的岗位[{}]排序为[{}]", person.getName(), position.getName(),
                updatedPersonsToPositions.getPositionOrder());
        }
        Y9MessageOrg<PersonsPositions> msg =
            new Y9MessageOrg<>(PlatformModelConvertUtil.convert(updatedPersonsToPositions, PersonsPositions.class),
                eventType, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, eventName, eventDescription);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("更新Y9PersonsToPositions后保存及发送消息到中间件完成");
        }
    }
}
