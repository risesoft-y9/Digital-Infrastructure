package net.risesoft.listener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.entity.relation.Y9OrgBasesToRoles;
import net.risesoft.entity.relation.Y9PersonsToGroups;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.enums.platform.AuthorizationPrincipalTypeEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.enums.platform.ResourceTypeEnum;
import net.risesoft.service.authorization.Y9AuthorizationService;
import net.risesoft.service.identity.IdentityResourceCalculator;
import net.risesoft.service.identity.Y9PersonToResourceAndAuthorityService;
import net.risesoft.service.identity.Y9PositionToResourceAndAuthorityService;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.service.relation.Y9OrgBasesToRolesService;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.util.Y9ResourceUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;

/**
 * 监听各种需要重新计算权限缓存的事件并执行相应操作
 *
 * @author shidaobang
 * @date 2022/09/13
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateIdentityResourceAndAuthorityListener {

    private final Y9PersonToResourceAndAuthorityService y9PersonToResourceAndAuthorityService;
    private final Y9PositionToResourceAndAuthorityService y9PositionToResourceAndAuthorityService;
    private final Y9AuthorizationService y9AuthorizationService;
    private final Y9OrgBasesToRolesService y9OrgBasesToRolesService;
    private final Y9PersonService y9PersonService;
    private final Y9PositionService y9PositionService;
    private final IdentityResourceCalculator identityResourceCalculator;

    private final CompositeOrgBaseService compositeOrgBaseService;

    @TransactionalEventListener
    @Async
    public void onOrgUnitCreated(Y9EntityCreatedEvent<? extends Y9OrgBase> event) {
        Y9OrgBase y9OrgBase = event.getEntity();
        OrgTypeEnum orgType = y9OrgBase.getOrgType();

        if (OrgTypeEnum.PERSON.equals(orgType)) {
            identityResourceCalculator.recalculateByOrgUnitId(y9OrgBase.getId());

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("新增人员触发的重新计算权限缓存执行完成");
            }
            return;
        }

        if (OrgTypeEnum.POSITION.equals(orgType)) {
            identityResourceCalculator.recalculateByOrgUnitId(y9OrgBase.getId());

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("新增岗位触发的重新计算权限缓存执行完成");
            }
            return;
        }
    }

    @TransactionalEventListener
    @Async
    public void onOrgUnitUpdated(Y9EntityUpdatedEvent<? extends Y9OrgBase> event) {
        Y9OrgBase originOrgUnit = event.getOriginEntity();
        Y9OrgBase updatedOrgUnit = event.getUpdatedEntity();
        OrgTypeEnum orgType = updatedOrgUnit.getOrgType();

        if (OrgTypeEnum.DEPARTMENT.equals(orgType)) {
            if (Y9OrgUtil.isMoved(originOrgUnit, updatedOrgUnit)) {
                // 只需要针对移动部门的情况需要删除重新计算
                String departmentId = updatedOrgUnit.getId();
                y9PositionToResourceAndAuthorityService.deleteByOrgUnitId(departmentId);
                y9PersonToResourceAndAuthorityService.deleteByOrgUnitId(departmentId);
                identityResourceCalculator.recalculateByOrgUnitId(departmentId);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("修改部门触发的重新计算权限缓存执行完成");
                }
            }
            return;
        }

        if (OrgTypeEnum.PERSON.equals(orgType)) {
            if (Y9OrgUtil.isMoved(originOrgUnit, updatedOrgUnit)) {
                // 只需要针对移动人员的情况需要删除重新计算
                String personId = updatedOrgUnit.getId();
                y9PersonToResourceAndAuthorityService.deleteByPersonId(personId);
                identityResourceCalculator.recalculateByOrgUnitId(personId);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("修改人员触发的重新计算权限缓存执行完成");
                }
            }
            return;
        }

        if (OrgTypeEnum.POSITION.equals(orgType)) {
            if (Y9OrgUtil.isMoved(originOrgUnit, updatedOrgUnit)) {
                // 只需要针对移动岗位的情况需要删除重新计算
                String positionId = updatedOrgUnit.getId();
                y9PositionToResourceAndAuthorityService.deleteByPositionId(positionId);
                identityResourceCalculator.recalculateByOrgUnitId(positionId);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("修改岗位触发的重新计算权限缓存执行完成");
                }
            }
            return;
        }
    }

    @TransactionalEventListener
    @Async
    public void onResourceCreated(Y9EntityCreatedEvent<? extends Y9ResourceBase> event) {
        Y9ResourceBase resource = event.getEntity();
        ResourceTypeEnum resourceType = resource.getResourceType();

        if (ResourceTypeEnum.APP.equals(resourceType)) {
            // 新建的 APP 肯定没有对该资源授权，不用计算权限
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("新建的 APP 肯定没有对该资源授权，不用计算权限");
            }
            return;
        }

        // 如果菜单或按钮继承权限，则需计算
        if (Boolean.TRUE.equals(resource.getInherit())) {
            identityResourceCalculator.recalculateByResourceId(resource.getId());
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("新增资源触发的重新计算权限缓存执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onResourceUpdated(Y9EntityUpdatedEvent<? extends Y9ResourceBase> event) {
        Y9ResourceBase originResource = event.getOriginEntity();
        Y9ResourceBase updatedResource = event.getUpdatedEntity();
        ResourceTypeEnum resourceType = updatedResource.getResourceType();

        if (ResourceTypeEnum.APP.equals(resourceType)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("应用修改不用重新计算权限缓存");
            }
            return;
        }

        if (Y9ResourceUtil.isInheritanceChanged(originResource, updatedResource)) {
            // 菜单和按钮如果修改继承属性，则需计算
            identityResourceCalculator.recalculateByResourceId(updatedResource.getId());
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("更新资源触发的重新计算权限缓存执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9AuthorizationCreated(Y9EntityCreatedEvent<Y9Authorization> event) {
        Y9Authorization y9Authorization = event.getEntity();

        if (AuthorizationPrincipalTypeEnum.ROLE.equals(y9Authorization.getPrincipalType())) {

            recalculateByRoleId(y9Authorization.getPrincipalId());

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("新增对角色的授权配置触发的重新计算权限缓存执行完成");
            }
            return;
        }

        identityResourceCalculator.recalculateByOrgUnitId(y9Authorization.getPrincipalId());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("新增或更新对组织的直接授权配置触发的重新计算权限缓存执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9AuthorizationDeleted(Y9EntityDeletedEvent<Y9Authorization> event) {
        Y9Authorization y9Authorization = event.getEntity();
        if (AuthorityEnum.HIDDEN.equals(y9Authorization.getAuthority())) {
            if (AuthorizationPrincipalTypeEnum.ROLE.equals(y9Authorization.getPrincipalType())) {

                recalculateByRoleId(y9Authorization.getPrincipalId());

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("删除对组织的直接隐藏授权配置触发的重新计算权限缓存执行完成");
                }
                return;
            }

            identityResourceCalculator.recalculateByOrgUnitId(y9Authorization.getPrincipalId());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("删除对角色的隐藏授权配置触发的重新计算权限缓存执行完成");
            }
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9OrgBasesToRolesCreated(Y9EntityCreatedEvent<Y9OrgBasesToRoles> event) {
        Y9OrgBasesToRoles y9OrgBasesToRoles = event.getEntity();
        if (Boolean.TRUE.equals(y9OrgBasesToRoles.getNegative())) {
            List<Y9Authorization> authorizationList = y9AuthorizationService
                .listByPrincipalIdAndPrincipalType(y9OrgBasesToRoles.getRoleId(), AuthorizationPrincipalTypeEnum.ROLE);
            for (Y9Authorization y9Authorization : authorizationList) {
                if (OrgTypeEnum.PERSON.equals(y9OrgBasesToRoles.getOrgType())) {
                    y9PersonToResourceAndAuthorityService.deleteByAuthorizationIdAndPersonId(y9Authorization.getId(),
                        y9OrgBasesToRoles.getOrgId());
                } else if (OrgTypeEnum.POSITION.equals(y9OrgBasesToRoles.getOrgType())) {
                    y9PositionToResourceAndAuthorityService
                        .deleteByAuthorizationIdAndPositionId(y9Authorization.getId(), y9OrgBasesToRoles.getOrgId());

                    List<Y9Person> y9PersonList =
                        y9PersonService.listByPositionId(y9OrgBasesToRoles.getOrgId(), Boolean.FALSE);
                    for (Y9Person y9Person : y9PersonList) {
                        y9PersonToResourceAndAuthorityService
                            .deleteByAuthorizationIdAndPersonId(y9Authorization.getId(), y9Person.getId());
                    }
                } else {
                    y9PersonToResourceAndAuthorityService.deleteByAuthorizationIdAndOrgUnitId(y9Authorization.getId(),
                        y9OrgBasesToRoles.getOrgId());
                    y9PositionToResourceAndAuthorityService.deleteByAuthorizationIdAndOrgUnitId(y9Authorization.getId(),
                        y9OrgBasesToRoles.getOrgId());
                }
            }
            return;
        } else {
            identityResourceCalculator.recalculateByOrgUnitId(y9OrgBasesToRoles.getOrgId());
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("新建组织和角色的映射触发的重新计算权限缓存执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9OrgBasesToRolesDeleted(Y9EntityDeletedEvent<Y9OrgBasesToRoles> event) {
        Y9OrgBasesToRoles orgBasesToRoles = event.getEntity();
        if (Boolean.TRUE.equals(orgBasesToRoles.getNegative())) {
            identityResourceCalculator.recalculateByOrgUnitId(orgBasesToRoles.getOrgId());
        } else {
            List<Y9Authorization> authorizationList = y9AuthorizationService
                .listByPrincipalIdAndPrincipalType(orgBasesToRoles.getRoleId(), AuthorizationPrincipalTypeEnum.ROLE);
            for (Y9Authorization y9Authorization : authorizationList) {
                if (OrgTypeEnum.PERSON.equals(orgBasesToRoles.getOrgType())) {
                    y9PersonToResourceAndAuthorityService.deleteByAuthorizationIdAndPersonId(y9Authorization.getId(),
                        orgBasesToRoles.getOrgId());
                } else if (OrgTypeEnum.POSITION.equals(orgBasesToRoles.getOrgType())) {
                    y9PositionToResourceAndAuthorityService
                        .deleteByAuthorizationIdAndPositionId(y9Authorization.getId(), orgBasesToRoles.getOrgId());

                    List<Y9Person> y9PersonList =
                        y9PersonService.listByPositionId(orgBasesToRoles.getOrgId(), Boolean.FALSE);
                    for (Y9Person y9Person : y9PersonList) {
                        y9PersonToResourceAndAuthorityService
                            .deleteByAuthorizationIdAndPersonId(y9Authorization.getId(), y9Person.getId());
                    }
                } else {
                    y9PersonToResourceAndAuthorityService.deleteByAuthorizationIdAndOrgUnitId(y9Authorization.getId(),
                        orgBasesToRoles.getOrgId());
                    y9PositionToResourceAndAuthorityService.deleteByAuthorizationIdAndOrgUnitId(y9Authorization.getId(),
                        orgBasesToRoles.getOrgId());
                }
            }
            identityResourceCalculator.recalculateByOrgUnitId(orgBasesToRoles.getOrgId());
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("删除组织和角色的映射触发的重新计算权限缓存执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9PersonsToGroupsCreated(Y9EntityCreatedEvent<Y9PersonsToGroups> event) {
        Y9PersonsToGroups y9PersonsToGroups = event.getEntity();

        identityResourceCalculator.recalculateByOrgUnitId(y9PersonsToGroups.getPersonId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("用户组加人触发的重新计算权限缓存执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9PersonsToGroupsDeleted(Y9EntityDeletedEvent<Y9PersonsToGroups> event) {
        Y9PersonsToGroups y9PersonsToGroups = event.getEntity();

        identityResourceCalculator.recalculateByOrgUnitId(y9PersonsToGroups.getPersonId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("用户组删人触发的重新计算权限缓存执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9PersonsToPositionsCreated(Y9EntityCreatedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions y9PersonsToPositions = event.getEntity();

        identityResourceCalculator.recalculateByOrgUnitId(y9PersonsToPositions.getPersonId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("新建人员和岗位的映射触发的重新计算权限缓存执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9PersonsToPositionsDeleted(Y9EntityDeletedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions y9PersonsToPositions = event.getEntity();

        identityResourceCalculator.recalculateByOrgUnitId(y9PersonsToPositions.getPersonId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("岗位删人触发的重新计算权限缓存执行完成");
        }
    }

    private void recalculateByRoleId(String roleId) {
        Set<Y9Person> y9PersonSet = new HashSet<>();
        Set<Y9Position> y9PositionSet = new HashSet<>();

        List<Y9OrgBasesToRoles> y9OrgBasesToRolesList = y9OrgBasesToRolesService.listByRoleId(roleId);

        for (Y9OrgBasesToRoles y9OrgBasesToRoles : y9OrgBasesToRolesList) {
            if (OrgTypeEnum.PERSON.equals(y9OrgBasesToRoles.getOrgType())) {
                y9PersonSet.add(y9PersonService.getById(y9OrgBasesToRoles.getOrgId()));
            } else if (OrgTypeEnum.POSITION.equals(y9OrgBasesToRoles.getOrgType())) {
                y9PositionSet.add(y9PositionService.getById(y9OrgBasesToRoles.getOrgId()));
                y9PersonSet.addAll(y9PersonService.listByPositionId(y9OrgBasesToRoles.getOrgId(), Boolean.FALSE));
            } else {
                y9PersonSet.addAll(compositeOrgBaseService.listAllDescendantPersons(y9OrgBasesToRoles.getOrgId()));
                y9PositionSet
                    .addAll(compositeOrgBaseService.listAllPositionsRecursionDownward(y9OrgBasesToRoles.getOrgId()));
            }
        }
        for (Y9Person y9Person : y9PersonSet) {
            identityResourceCalculator.recalculateByPerson(y9Person);
        }
        for (Y9Position y9Position : y9PositionSet) {
            identityResourceCalculator.recalculateByPosition(y9Position);
        }
    }
}
