package net.risesoft.listener;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9OrgBasesToRoles;
import net.risesoft.entity.relation.Y9PersonsToGroups;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.OrgTypeEnum;
import net.risesoft.repository.Y9PersonRepository;
import net.risesoft.repository.Y9PositionRepository;
import net.risesoft.repository.relation.Y9PersonsToGroupsRepository;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.service.identity.Y9PersonToRoleService;
import net.risesoft.service.identity.Y9PositionToRoleService;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;

/**
 * 监听需要更新人员/岗位角色的事件并执行相应操作
 *
 * @author shidaobang
 * @date 2023/07/24
 * @since 9.6.3
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateIdentityRolesListener {

    private final CompositeOrgBaseService compositeOrgBaseService;

    private final Y9PersonToRoleService y9PersonToRoleService;
    private final Y9PositionToRoleService y9PositionToRoleService;

    private final Y9PersonRepository y9PersonRepository;
    private final Y9PositionRepository y9PositionRepository;
    private final Y9PersonsToGroupsRepository y9PersonsToGroupsRepository;
    private final Y9PersonsToPositionsRepository y9PersonsToPositionsRepository;

    @TransactionalEventListener
    @Async
    public void onY9DepartmentUpdated(Y9EntityUpdatedEvent<Y9Department> event) {
        Y9Department originY9Department = event.getOriginEntity();
        Y9Department updatedY9Department = event.getUpdatedEntity();

        if (Y9OrgUtil.isMoved(originY9Department, updatedY9Department)) {
            this.updateIdentityRolesByOrgId(updatedY9Department.getId());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("部门移动触发的更新人员/岗位角色执行完成");
            }
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9GroupDeleted(Y9EntityDeletedEvent<Y9Group> event) {
        Y9Group y9Group = event.getEntity();

        this.updateIdentityRolesByOrgId(y9Group.getId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("用户组删除触发的更新人员角色执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9GroupUpdated(Y9EntityUpdatedEvent<Y9Group> event) {
        Y9Group updatedY9Group = event.getUpdatedEntity();
        Y9Group originY9Group = event.getOriginEntity();

        if (Y9OrgUtil.isMoved(originY9Group, updatedY9Group)) {
            this.updateIdentityRolesByOrgId(updatedY9Group.getId());

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("用户组移动触发的更新人员角色执行完成");
            }
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9OrgBasesToRolesCreated(Y9EntityCreatedEvent<Y9OrgBasesToRoles> event) {
        Y9OrgBasesToRoles y9OrgBasesToRoles = event.getEntity();

        this.updateIdentityRolesByOrgId(y9OrgBasesToRoles.getOrgId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("新建组织和角色的映射触发的重新计算权限缓存执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9OrgBasesToRolesDeleted(Y9EntityDeletedEvent<Y9OrgBasesToRoles> event) {
        Y9OrgBasesToRoles y9OrgBasesToRoles = event.getEntity();

        this.updateIdentityRolesByOrgId(y9OrgBasesToRoles.getOrgId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("删除组织和角色的映射触发的重新计算权限缓存执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9PersonUpdated(Y9EntityUpdatedEvent<Y9Person> event) {
        Y9Person originY9Person = event.getOriginEntity();
        Y9Person updatedY9Person = event.getUpdatedEntity();

        if (Y9OrgUtil.isMoved(originY9Person, updatedY9Person)) {
            y9PersonToRoleService.recalculate(updatedY9Person.getId());

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("修改人员触发的更新人员/岗位角色执行完成");
            }
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9PersonsToPositionsCreated(Y9EntityCreatedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions y9PersonsToPositions = event.getEntity();

        y9PersonToRoleService.recalculate(y9PersonsToPositions.getPersonId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("新建人员和岗位的映射触发的重新计算权限缓存执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9PersonsToPositionsDeleted(Y9EntityDeletedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions y9PersonsToPositions = event.getEntity();

        y9PersonToRoleService.recalculate(y9PersonsToPositions.getPersonId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("岗位删人触发的更新人员角色执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9PositionDeleted(Y9EntityDeletedEvent<Y9Position> event) {
        Y9Position y9Position = event.getEntity();

        this.updateIdentityRolesByOrgId(y9Position.getId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("删除岗位触发的重新计算权限缓存执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9PositionUpdated(Y9EntityUpdatedEvent<Y9Position> event) {
        Y9Position originY9Position = event.getOriginEntity();
        Y9Position updatedY9Position = event.getUpdatedEntity();

        if (Y9OrgUtil.isMoved(originY9Position, updatedY9Position)) {
            this.updateIdentityRolesByOrgId(updatedY9Position.getId());

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("修改岗位触发的更新岗位角色执行完成");
            }
        }
    }

    public void updateIdentityRolesByOrgId(final String orgId) {
        Y9OrgBase y9OrgBase = compositeOrgBaseService.getOrgUnit(orgId);
        if (y9OrgBase != null && y9OrgBase.getId() != null) {
            OrgTypeEnum orgType = OrgTypeEnum.getByEnName(y9OrgBase.getOrgType());
            switch (orgType) {
                case ORGANIZATION:
                    List<String> personIdList =
                        y9PersonRepository.findIdByGuidPathStartingWith(y9OrgBase.getGuidPath());
                    for (String personId : personIdList) {
                        y9PersonToRoleService.recalculate(personId);
                    }
                    List<String> positionIdList =
                        y9PositionRepository.findIdByGuidPathStartingWith(y9OrgBase.getGuidPath());
                    for (String positionId : positionIdList) {
                        y9PositionToRoleService.recalculate(positionId);
                    }
                    break;
                case DEPARTMENT:
                    List<String> personIds = y9PersonRepository.findIdByGuidPathStartingWith(y9OrgBase.getGuidPath());
                    for (String personId : personIds) {
                        y9PersonToRoleService.recalculate(personId);
                    }
                    List<String> positionIds =
                        y9PositionRepository.findIdByGuidPathStartingWith(y9OrgBase.getGuidPath());
                    for (String positionId : positionIds) {
                        y9PositionToRoleService.recalculate(positionId);
                    }
                    break;
                case POSITION:
                    List<Y9PersonsToPositions> orgPositionPersons =
                        y9PersonsToPositionsRepository.findByPositionId(orgId);
                    for (Y9PersonsToPositions orgPositionsPerson : orgPositionPersons) {
                        String orgPersonId = orgPositionsPerson.getPersonId();
                        y9PersonToRoleService.recalculate(orgPersonId);
                    }
                    y9PositionToRoleService.recalculate(orgId);
                    break;
                case GROUP:
                    List<Y9PersonsToGroups> y9PersonsToGroups = y9PersonsToGroupsRepository.findByGroupId(orgId);
                    for (Y9PersonsToGroups orgPersonsGroup : y9PersonsToGroups) {
                        String orgPersonId = orgPersonsGroup.getPersonId();
                        y9PersonToRoleService.recalculate(orgPersonId);
                    }
                    break;
                case PERSON:
                    y9PersonToRoleService.recalculate(orgId);
                    break;
                default:
            }
        }
    }
}
