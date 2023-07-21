package net.risesoft.listener;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9OrgBasesToRoles;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;

/**
 * 监听需要更新人员角色的事件并执行相应操作
 *
 * @author shidaobang
 * @date 2022/09/26
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UpdatePersonRolesListener {
    
    private final Y9PersonService y9PersonService;
    
    @TransactionalEventListener
    @Async
    public void onY9DepartmentUpdated(Y9EntityUpdatedEvent<Y9Department> event) {
        Y9Department originY9Department = event.getOriginEntity();
        Y9Department updatedY9Department = event.getUpdatedEntity();

        if (Y9OrgUtil.isMoved(originY9Department, updatedY9Department)) {
            y9PersonService.updatePersonRolesByOrgId(updatedY9Department.getId());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("部门移动触发的更新人员角色执行完成");
            }
        }

    }

    @TransactionalEventListener
    @Async
    public void onY9GroupDeleted(Y9EntityDeletedEvent<Y9Group> event) {
        Y9Group y9Group = event.getEntity();

        y9PersonService.updatePersonRolesByOrgId(y9Group.getId());

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
            y9PersonService.updatePersonRolesByOrgId(updatedY9Group.getId());

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("用户组移动触发的更新人员角色执行完成");
            }
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9OrgBasesToRolesCreated(Y9EntityCreatedEvent<Y9OrgBasesToRoles> event) {
        Y9OrgBasesToRoles y9OrgBasesToRoles = event.getEntity();

        y9PersonService.updatePersonRolesByOrgId(y9OrgBasesToRoles.getOrgId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("新建组织和角色的映射触发的重新计算权限缓存执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9OrgBasesToRolesDeleted(Y9EntityDeletedEvent<Y9OrgBasesToRoles> event) {
        Y9OrgBasesToRoles y9OrgBasesToRoles = event.getEntity();

        y9PersonService.updatePersonRolesByOrgId(y9OrgBasesToRoles.getOrgId());

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
            y9PersonService.updatePersonRoles(updatedY9Person);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("修改人员触发的更新人员角色执行完成");
            }
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9PersonsToPositionsCreated(Y9EntityCreatedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions y9PersonsToPositions = event.getEntity();

        y9PersonService.updatePersonRoles(y9PersonsToPositions.getPersonId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("新建人员和岗位的映射触发的重新计算权限缓存执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9PersonsToPositionsDeleted(Y9EntityDeletedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions y9PersonsToPositions = event.getEntity();

        y9PersonService.updatePersonRoles(y9PersonsToPositions.getPersonId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("岗位删人触发的更新人员角色执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9PositionDeleted(Y9EntityDeletedEvent<Y9Position> event) {
        Y9Position y9Position = event.getEntity();

        y9PersonService.updatePersonRolesByOrgId(y9Position.getId());

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
            y9PersonService.updatePersonRolesByOrgId(updatedY9Position.getId());

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("修改岗位触发的更新人员角色执行完成");
            }
        }
    }
}
