package net.risesoft.listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.kafka.core.KafkaTemplate;
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
import net.risesoft.enums.AuthorizationPrincipalTypeEnum;
import net.risesoft.enums.OrgTypeEnum;
import net.risesoft.enums.ResourceTypeEnum;
import net.risesoft.service.authorization.Y9AuthorizationService;
import net.risesoft.service.identity.Y9PersonToResourceAndAuthorityService;
import net.risesoft.service.identity.Y9PositionToResourceAndAuthorityService;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.relation.Y9OrgBasesToRolesService;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.util.Y9ResourceUtil;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9TopicConst;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.repository.resource.Y9SystemRepository;

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

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9SystemRepository y9SystemRepository;

    private final KafkaTemplate<String, Object> y9KafkaTemplate;

    @TransactionalEventListener
    @Async
    public void onOrgUnitCreated(Y9EntityCreatedEvent<? extends Y9OrgBase> event) {
        Y9OrgBase y9OrgBase = event.getEntity();
        String orgType = y9OrgBase.getOrgType();

        if (OrgTypeEnum.PERSON.getEnName().equals(orgType)) {
            y9AuthorizationService.syncToIdentityResourceAndAuthority(y9OrgBase.getId());

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("新增人员触发的重新计算权限缓存执行完成");
            }
        }

        if (OrgTypeEnum.POSITION.getEnName().equals(orgType)) {
            y9AuthorizationService.syncToIdentityResourceAndAuthority(y9OrgBase.getId());

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("新增岗位触发的重新计算权限缓存执行完成");
            }
        }
    }

    @TransactionalEventListener
    @Async
    public void onOrgUnitUpdated(Y9EntityUpdatedEvent<? extends Y9OrgBase> event) {
        Y9OrgBase originOrgUnit = event.getOriginEntity();
        Y9OrgBase updatedOrgUnit = event.getUpdatedEntity();
        String orgType = updatedOrgUnit.getOrgType();

        if (OrgTypeEnum.DEPARTMENT.getEnName().equals(orgType)) {
            if (Y9OrgUtil.isMoved(originOrgUnit, updatedOrgUnit)) {
                // 只需要针对移动部门的情况需要删除重新计算
                String departmentId = updatedOrgUnit.getId();
                y9PositionToResourceAndAuthorityService.deleteByOrgUnitId(departmentId);
                y9PersonToResourceAndAuthorityService.deleteByOrgUnitId(departmentId);
                y9AuthorizationService.syncToIdentityResourceAndAuthority(departmentId);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("修改部门触发的重新计算权限缓存执行完成");
                }
            }
        }

        if (OrgTypeEnum.PERSON.getEnName().equals(orgType)) {
            if (Y9OrgUtil.isMoved(originOrgUnit, updatedOrgUnit)) {
                // 只需要针对移动人员的情况需要删除重新计算
                String personId = updatedOrgUnit.getId();
                y9PersonToResourceAndAuthorityService.deleteByPersonId(personId);
                y9AuthorizationService.syncToIdentityResourceAndAuthority(personId);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("修改人员触发的重新计算权限缓存执行完成");
                }
            }
        }

        if (OrgTypeEnum.POSITION.getEnName().equals(orgType)) {
            if (Y9OrgUtil.isMoved(originOrgUnit, updatedOrgUnit)) {
                // 只需要针对移动岗位的情况需要删除重新计算
                String positionId = updatedOrgUnit.getId();
                y9PositionToResourceAndAuthorityService.deleteByPositionId(positionId);
                y9AuthorizationService.syncToIdentityResourceAndAuthority(positionId);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("修改岗位触发的重新计算权限缓存执行完成");
                }
            }
        }
    }

    @TransactionalEventListener
    @Async
    public void onResourceCreated(Y9EntityCreatedEvent<? extends Y9ResourceBase> event) {
        Y9ResourceBase resource = event.getEntity();
        Integer resourceType = resource.getResourceType();

        if (ResourceTypeEnum.APP.getValue().equals(resourceType)) {
            // 新建的 APP 肯定没有对该资源授权，不用计算权限
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("新建的 APP 肯定没有对该资源授权，不用计算权限");
            }
            return;
        }

        // 如果菜单或按钮继承权限，则需计算
        if (Boolean.TRUE.equals(resource.getInherit())) {
            y9AuthorizationService.syncToIdentityResourceAndAuthorityByResourceId(resource.getId());
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
        Integer resourceType = updatedResource.getResourceType();

        if (ResourceTypeEnum.APP.getValue().equals(resourceType)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("应用修改不用重新计算权限缓存");
            }
            return;
        }

        if (Y9ResourceUtil.isInheritanceChanged(originResource, updatedResource)) {
            // 菜单和按钮如果修改继承属性，则需计算
            y9AuthorizationService.syncToIdentityResourceAndAuthorityByResourceId(updatedResource.getId());
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("更新资源触发的重新计算权限缓存执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9AppCreated(Y9EntityCreatedEvent<Y9App> event) {
        Y9App y9App = event.getEntity();
        Y9System system = y9SystemRepository.findByName("risecms7");
        // 同步内容管理栏目
        if (system != null && system.getId().equals(y9App.getSystemId())) {
            HashMap<String, Object> map = new HashMap<>(8);
            map.put("appId", y9App.getAppId());
            map.put("url", y9App.getUrl());
            map.put("appName", y9App.getName());
            String jsonString = Y9JsonUtil.writeValueAsString(map);
            if (y9KafkaTemplate != null) {
                y9KafkaTemplate.send(Y9TopicConst.Y9_INSERT_PUBLIC_CHNL_MESSAGE, jsonString);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("同步内容管理新建栏目完成");
                }
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("新增应用触发事件执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9AuthorizationCreated(Y9EntityCreatedEvent<Y9Authorization> event) {
        Y9Authorization y9Authorization = event.getEntity();

        if (AuthorizationPrincipalTypeEnum.ROLE.getValue().equals(y9Authorization.getPrincipalType())) {
            Set<Y9Person> y9PersonSet = new HashSet<>();
            Set<Y9Position> y9PositionSet = new HashSet<>();

            List<Y9OrgBasesToRoles> y9OrgBasesToRolesList =
                y9OrgBasesToRolesService.listByRoleId(y9Authorization.getPrincipalId());

            for (Y9OrgBasesToRoles y9OrgBasesToRoles : y9OrgBasesToRolesList) {
                if (OrgTypeEnum.PERSON.getEnName().equals(y9OrgBasesToRoles.getOrgType())) {
                    y9PersonSet.add((Y9Person)compositeOrgBaseService.getOrgBase(y9OrgBasesToRoles.getOrgId()));
                } else if (OrgTypeEnum.POSITION.getEnName().equals(y9OrgBasesToRoles.getOrgType())) {
                    y9PositionSet.add((Y9Position)compositeOrgBaseService.getOrgBase(y9OrgBasesToRoles.getOrgId()));
                } else {
                    y9PersonSet
                        .addAll(compositeOrgBaseService.listAllPersonsRecursionDownward(y9OrgBasesToRoles.getOrgId()));
                    y9PositionSet.addAll(
                        compositeOrgBaseService.listAllPositionsRecursionDownward(y9OrgBasesToRoles.getOrgId()));
                }
            }
            for (Y9Person y9Person : y9PersonSet) {
                y9AuthorizationService.syncToIdentityResourceAndAuthority(y9Person);
            }
            for (Y9Position y9Position : y9PositionSet) {
                y9AuthorizationService.syncToIdentityResourceAndAuthority(y9Position);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("新增对角色的授权配置触发的重新计算权限缓存执行完成");
            }
        } else {
            y9AuthorizationService.syncToIdentityResourceAndAuthority(y9Authorization.getPrincipalId());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("新增或更新对组织的直接授权配置触发的重新计算权限缓存执行完成");
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
                if (OrgTypeEnum.PERSON.getEnName().equals(y9OrgBasesToRoles.getOrgType())) {
                    y9PersonToResourceAndAuthorityService.deleteByAuthorizationIdAndPersonId(y9Authorization.getId(),
                        y9OrgBasesToRoles.getOrgId());
                } else if (OrgTypeEnum.POSITION.getEnName().equals(y9OrgBasesToRoles.getOrgType())) {
                    y9PositionToResourceAndAuthorityService
                        .deleteByAuthorizationIdAndPositionId(y9Authorization.getId(), y9OrgBasesToRoles.getOrgId());
                } else {
                    y9PersonToResourceAndAuthorityService.deleteByAuthorizationIdAndOrgUnitId(y9Authorization.getId(),
                        y9OrgBasesToRoles.getOrgId());
                    y9PositionToResourceAndAuthorityService.deleteByAuthorizationIdAndOrgUnitId(y9Authorization.getId(),
                        y9OrgBasesToRoles.getOrgId());
                }
            }
        } else {
            y9AuthorizationService.syncToIdentityResourceAndAuthority(y9OrgBasesToRoles.getOrgId());
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
            y9AuthorizationService.syncToIdentityResourceAndAuthority(orgBasesToRoles.getOrgId());
        } else {
            List<Y9Authorization> authorizationList = y9AuthorizationService
                .listByPrincipalIdAndPrincipalType(orgBasesToRoles.getRoleId(), AuthorizationPrincipalTypeEnum.ROLE);
            for (Y9Authorization y9Authorization : authorizationList) {
                if (OrgTypeEnum.PERSON.getEnName().equals(orgBasesToRoles.getOrgType())) {
                    y9PersonToResourceAndAuthorityService.deleteByAuthorizationIdAndPersonId(y9Authorization.getId(),
                        orgBasesToRoles.getOrgId());
                } else if (OrgTypeEnum.POSITION.getEnName().equals(orgBasesToRoles.getOrgType())) {
                    y9PositionToResourceAndAuthorityService
                        .deleteByAuthorizationIdAndPositionId(y9Authorization.getId(), orgBasesToRoles.getOrgId());
                } else {
                    y9PersonToResourceAndAuthorityService.deleteByAuthorizationIdAndOrgUnitId(y9Authorization.getId(),
                        orgBasesToRoles.getOrgId());
                    y9PositionToResourceAndAuthorityService.deleteByAuthorizationIdAndOrgUnitId(y9Authorization.getId(),
                        orgBasesToRoles.getOrgId());
                }
            }
            y9AuthorizationService.syncToIdentityResourceAndAuthority(orgBasesToRoles.getOrgId());
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("删除组织和角色的映射触发的重新计算权限缓存执行完成");
        }
    }
}
