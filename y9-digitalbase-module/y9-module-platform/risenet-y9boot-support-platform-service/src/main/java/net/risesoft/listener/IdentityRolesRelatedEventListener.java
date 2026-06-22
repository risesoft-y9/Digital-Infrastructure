package net.risesoft.listener;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.entity.permission.Y9OrgBasesToRoles;
import net.risesoft.entity.relation.Y9PersonsToGroups;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.repository.permission.cache.person.Y9PersonToRoleRepository;
import net.risesoft.repository.permission.cache.position.Y9PositionToRoleRepository;
import net.risesoft.service.permission.cache.IdentityRoleCalculator;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.util.Y9PlatformUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9public.entity.Y9Role;

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
public class IdentityRolesRelatedEventListener {

    private final IdentityRoleCalculator identityRoleCalculator;

    private final Y9PersonToRoleRepository y9PersonToRoleRepository;
    private final Y9PositionToRoleRepository y9PositionToRoleRepository;

    @TransactionalEventListener
    @Async
    public void onOrgUnitCreated(Y9EntityCreatedEvent<? extends Y9OrgBase> event) {
        Y9OrgBase y9OrgBase = event.getEntity();
        OrgTypeEnum orgType = y9OrgBase.getOrgType();

        if (OrgTypeEnum.PERSON.equals(orgType)) {
            identityRoleCalculator.recalculateByOrgUnitId(y9OrgBase.getId());

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("新增人员触发的重新计算角色执行完成");
            }
        }

        if (OrgTypeEnum.POSITION.equals(orgType)) {
            identityRoleCalculator.recalculateByOrgUnitId(y9OrgBase.getId());

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
        OrgTypeEnum orgType = originY9OrgBase.getOrgType();

        if (Y9OrgUtil.isMoved(originY9OrgBase, updatedY9OrgBase)) {

            if (OrgTypeEnum.DEPARTMENT.equals(orgType) || OrgTypeEnum.GROUP.equals(orgType)
                || OrgTypeEnum.PERSON.equals(orgType) || OrgTypeEnum.POSITION.equals(orgType)) {
                identityRoleCalculator.recalculateByOrgUnitId(updatedY9OrgBase.getId());
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

        identityRoleCalculator.recalculateByOrgUnitId(y9OrgBasesToRoles.getOrgId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("新建组织和角色的映射触发的重新计算角色执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9OrgBasesToRolesDeleted(Y9EntityDeletedEvent<Y9OrgBasesToRoles> event) {
        Y9OrgBasesToRoles y9OrgBasesToRoles = event.getEntity();

        identityRoleCalculator.recalculateByOrgUnitId(y9OrgBasesToRoles.getOrgId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("删除组织和角色的映射触发的重新计算角色执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9PersonsToGroupsDeleted(Y9EntityDeletedEvent<Y9PersonsToGroups> event) {
        Y9PersonsToGroups y9PersonsToGroups = event.getEntity();

        identityRoleCalculator.recalculateByOrgUnitId(y9PersonsToGroups.getPersonId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("用户组删人触发的更新人员角色执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9PersonsToPositionsCreated(Y9EntityCreatedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions y9PersonsToPositions = event.getEntity();

        identityRoleCalculator.recalculateByOrgUnitId(y9PersonsToPositions.getPersonId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("新建人员和岗位的映射触发的重新计算角色执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9PersonsToPositionsDeleted(Y9EntityDeletedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions y9PersonsToPositions = event.getEntity();

        identityRoleCalculator.recalculateByOrgUnitId(y9PersonsToPositions.getPersonId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("岗位删人触发的更新人员角色执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onPersonDeleted(Y9EntityDeletedEvent<Y9Person> event) {
        Y9Person person = event.getEntity();
        y9PersonToRoleRepository.deleteByPersonId(person.getId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("人员[{}]删除同时删除角色缓存执行完成", person.getId());
        }
    }

    @TransactionalEventListener
    @Async
    public void onPositionDeleted(Y9EntityDeletedEvent<Y9Position> event) {
        Y9Position position = event.getEntity();
        y9PositionToRoleRepository.deleteByPositionId(position.getId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("岗位[{}]删除同时删除角色缓存执行完成", position.getId());
        }
    }
    
    @TransactionalEventListener
    public void onRoleDeleted(Y9EntityDeletedEvent<Y9Role> event) {
        Y9Role entity = event.getEntity();
        for (String tenantId : Y9PlatformUtil.getTenantIds()) {
            deleteByRole(tenantId, entity);
        }
    }

    @Async
    protected void deleteByRole(String tenantId, Y9Role entity) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9PersonToRoleRepository.deleteByRoleId(entity.getId());
        LOGGER.debug("角色[{}]删除时同步删除租户[{}]的人员角色缓存数据", entity.getId(), tenantId);

        y9PositionToRoleRepository.deleteByRoleId(entity.getId());
        LOGGER.debug("角色[{}]删除时同步删除租户[{}]的岗位角色缓存数据", entity.getId(), tenantId);
    }

}
