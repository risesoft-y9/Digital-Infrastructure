package net.risesoft.listener;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9OrgBase;
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

    // TODO 岗位删除 和 用户组删除 需更新关联的人员角色

    @TransactionalEventListener
    @Async
    public void onOrgUnitCreated(Y9EntityCreatedEvent<? extends Y9OrgBase> event) {
        Y9OrgBase y9OrgBase = event.getEntity();
        String orgType = y9OrgBase.getOrgType();

        if (OrgTypeEnum.PERSON.getEnName().equals(orgType)) {
            this.updateIdentityRolesByOrgId(y9OrgBase.getId());

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("新增人员触发的重新计算角色执行完成");
            }
        }

        if (OrgTypeEnum.POSITION.getEnName().equals(orgType)) {
            this.updateIdentityRolesByOrgId(y9OrgBase.getId());

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("新增岗位触发的重新计算角色执行完成");
            }
        }
    }

    @TransactionalEventListener
    @Async
    public void onOrgUnitUpdated(Y9EntityUpdatedEvent<? extends Y9OrgBase> event) {
        Y9OrgBase originY9OrgBase = event.getOriginEntity();
        Y9OrgBase updatedY9OrgBase = event.getUpdatedEntity();
        String orgType = originY9OrgBase.getOrgType();

        if (Y9OrgUtil.isMoved(originY9OrgBase, updatedY9OrgBase)) {
            if (OrgTypeEnum.POSITION.getEnName().equals(orgType)) {
                // 岗位移动需特殊处理：人员的角色还包括关联岗位的角色
                List<Y9PersonsToPositions> y9PersonsToPositionsList =
                    y9PersonsToPositionsRepository.findByPositionId(updatedY9OrgBase.getId());
                for (Y9PersonsToPositions y9PersonsToPositions : y9PersonsToPositionsList) {
                    y9PersonToRoleService.recalculate(y9PersonsToPositions.getPersonId());
                }
            }

            if (OrgTypeEnum.DEPARTMENT.getEnName().equals(orgType) || OrgTypeEnum.GROUP.getEnName().equals(orgType)
                || OrgTypeEnum.PERSON.getEnName().equals(orgType) || OrgTypeEnum.POSITION.getEnName().equals(orgType)) {
                this.updateIdentityRolesByOrgId(updatedY9OrgBase.getId());
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("组织节点移动触发的更新人员/岗位角色执行完成");
            }
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9OrgBasesToRolesCreated(Y9EntityCreatedEvent<Y9OrgBasesToRoles> event) {
        Y9OrgBasesToRoles y9OrgBasesToRoles = event.getEntity();

        this.updateIdentityRolesByOrgId(y9OrgBasesToRoles.getOrgId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("新建组织和角色的映射触发的重新计算角色执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9OrgBasesToRolesDeleted(Y9EntityDeletedEvent<Y9OrgBasesToRoles> event) {
        Y9OrgBasesToRoles y9OrgBasesToRoles = event.getEntity();

        this.updateIdentityRolesByOrgId(y9OrgBasesToRoles.getOrgId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("删除组织和角色的映射触发的重新计算角色执行完成");
        }
    }

    @TransactionalEventListener()
    @Async
    public void onY9PersonsToPositionsCreated(Y9EntityCreatedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions y9PersonsToPositions = event.getEntity();

        y9PersonToRoleService.recalculate(y9PersonsToPositions.getPersonId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("新建人员和岗位的映射触发的重新计算角色执行完成");
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

    private void updateIdentityRolesByOrgId(final String orgId) {
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
                case GROUP:
                    List<Y9PersonsToGroups> y9PersonsToGroups = y9PersonsToGroupsRepository.findByGroupId(orgId);
                    for (Y9PersonsToGroups orgPersonsGroup : y9PersonsToGroups) {
                        String orgPersonId = orgPersonsGroup.getPersonId();
                        y9PersonToRoleService.recalculate(orgPersonId);
                    }
                    break;
                case POSITION:
                    y9PositionToRoleService.recalculate(orgId);
                    break;
                case PERSON:
                    y9PersonToRoleService.recalculate(orgId);
                    break;
                default:
            }
        }
    }
}
