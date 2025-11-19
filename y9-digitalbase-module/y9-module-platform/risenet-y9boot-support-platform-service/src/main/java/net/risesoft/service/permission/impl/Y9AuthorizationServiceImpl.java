package net.risesoft.service.permission.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.org.Y9Department;
import net.risesoft.entity.org.Y9Group;
import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.entity.org.Y9Organization;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.enums.AuditLogEnum;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.permission.AuthorizationPrincipalTypeEnum;
import net.risesoft.exception.AuthorizationErrorCodeEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.permission.Y9AuthorizationManager;
import net.risesoft.model.platform.permission.Authorization;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.repository.permission.Y9AuthorizationRepository;
import net.risesoft.service.permission.Y9AuthorizationService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.util.Y9PlatformUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9StringUtil;
import net.risesoft.y9public.entity.Y9Role;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.entity.tenant.Y9TenantApp;
import net.risesoft.y9public.manager.resource.CompositeResourceManager;
import net.risesoft.y9public.manager.role.Y9RoleManager;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class Y9AuthorizationServiceImpl implements Y9AuthorizationService {

    private final Y9AuthorizationRepository y9AuthorizationRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final CompositeResourceManager compositeResourceManager;

    private final Y9AuthorizationManager y9AuthorizationManager;
    private final Y9RoleManager y9RoleManager;

    private static List<Authorization> entityToModel(List<Y9Authorization> y9AuthorizationList) {
        return PlatformModelConvertUtil.convert(y9AuthorizationList, Authorization.class);
    }

    private static Authorization entityToModel(Y9Authorization y9Authorization) {
        return PlatformModelConvertUtil.convert(y9Authorization, Authorization.class);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Y9Authorization y9Authorization = this.getById(id);

        y9AuthorizationRepository.delete(y9Authorization);

        String resourceName = compositeResourceManager
            .findByIdAndResourceType(y9Authorization.getResourceId(), y9Authorization.getResourceType())
            .getName();
        String principalName = Objects.equals(AuthorizationPrincipalTypeEnum.ROLE, y9Authorization.getPrincipalType())
            ? y9RoleManager.getByIdFromCache(y9Authorization.getPrincipalId()).getName()
            : compositeOrgBaseManager.getOrgUnit(y9Authorization.getPrincipalId()).getName();
        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.AUTHORIZATION_DELETE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.AUTHORIZATION_DELETE.getDescription(),
                y9Authorization.getPrincipalType().getName(), principalName, y9Authorization.getAuthority().getName(),
                y9Authorization.getResourceType().getName(), resourceName))
            .objectId(id)
            .oldObject(y9Authorization)
            .currentObject(null)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Authorization));
    }

    @Override
    @Transactional
    public void delete(String[] ids) {
        if (ids != null) {
            for (String id : ids) {
                this.delete(id);
            }
        }
    }

    @Override
    public Optional<Authorization> findById(String id) {
        return y9AuthorizationRepository.findById(id).map(Y9AuthorizationServiceImpl::entityToModel);
    }

    @Override
    public List<Authorization> listByPrincipalIdAndPrincipalType(String principalId,
        AuthorizationPrincipalTypeEnum principalTypeEnum) {
        List<Y9Authorization> y9AuthorizationList =
            y9AuthorizationRepository.findByPrincipalIdAndPrincipalType(principalId, principalTypeEnum);
        return entityToModel(y9AuthorizationList);
    }

    @Override
    public List<Authorization> listByPrincipalTypeAndResourceId(AuthorizationPrincipalTypeEnum principalType,
        String resourceId) {
        List<Y9Authorization> y9AuthorizationList =
            y9AuthorizationRepository.findByResourceIdAndPrincipalType(resourceId, principalType);
        return entityToModel(y9AuthorizationList);
    }

    @Override
    public List<Authorization> listByResourceIdAndPrincipalTypeNot(String resourceId,
        AuthorizationPrincipalTypeEnum principalType) {
        List<Y9Authorization> y9AuthorizationList =
            y9AuthorizationRepository.findByResourceIdAndPrincipalTypeNot(resourceId, principalType);
        return entityToModel(y9AuthorizationList);
    }

    @Override
    public List<Authorization> listByRoleIds(List<String> principalIds, String resourceId, AuthorityEnum authority) {
        List<Y9Authorization> y9AuthorizationList =
            y9AuthorizationRepository.findByResourceIdAndAuthorityAndPrincipalIdIn(resourceId, authority, principalIds);
        return entityToModel(y9AuthorizationList);
    }

    @Override
    public Y9Page<Authorization> pageByPrincipalId(String principalId, Y9PageQuery pageQuery) {
        PageRequest pageable = PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by("createTime"));
        Page<Y9Authorization> y9AuthorizationPage = y9AuthorizationRepository.findByPrincipalId(principalId, pageable);
        return Y9Page.success(pageQuery.getPage(), y9AuthorizationPage.getTotalPages(),
            y9AuthorizationPage.getTotalElements(), entityToModel(y9AuthorizationPage.getContent()));
    }

    @Override
    @Transactional
    public void save(AuthorityEnum authority, String principalId, AuthorizationPrincipalTypeEnum principalType,
        String[] resourceIds) {
        for (String id : resourceIds) {
            Authorization y9Authorization = new Authorization();
            y9Authorization.setAuthority(authority);
            y9Authorization.setPrincipalId(principalId);
            y9Authorization.setResourceId(id);
            if (AuthorizationPrincipalTypeEnum.ROLE.equals(principalType)) {
                this.saveOrUpdateRole(y9Authorization);
            } else {
                this.saveOrUpdateOrg(y9Authorization);
            }
        }
    }

    @Override
    @Transactional
    public void saveByOrg(AuthorityEnum authority, String resourceId, String[] principleIds) {
        for (String principleId : principleIds) {
            Authorization y9Authorization = new Authorization();
            y9Authorization.setPrincipalId(principleId);
            y9Authorization.setAuthority(authority);
            y9Authorization.setResourceId(resourceId);
            this.saveOrUpdateOrg(y9Authorization);
        }
    }

    @Override
    @Transactional
    public void saveByRoles(AuthorityEnum authority, String resourceId, String[] roleIds) {
        for (String roleId : roleIds) {
            Authorization y9Authorization = new Authorization();
            y9Authorization.setPrincipalId(roleId);
            y9Authorization.setAuthority(authority);
            y9Authorization.setResourceId(resourceId);
            this.saveOrUpdateRole(y9Authorization);
        }
    }

    @Transactional
    public Authorization saveOrUpdate(Authorization authorization) {
        Y9Authorization y9Authorization = PlatformModelConvertUtil.convert(authorization, Y9Authorization.class);

        // 如已存在则修改
        Optional<Y9Authorization> optionalY9Authorization =
            y9AuthorizationRepository.findByPrincipalIdAndResourceIdAndAuthority(y9Authorization.getPrincipalId(),
                y9Authorization.getResourceId(), y9Authorization.getAuthority());
        if (optionalY9Authorization.isPresent()) {
            Y9Authorization originY9Authorization = optionalY9Authorization.get();
            Y9BeanUtil.copyProperties(y9Authorization, originY9Authorization, "id");
            return entityToModel(y9AuthorizationRepository.save(originY9Authorization));
        }

        Y9Authorization savedAuthorization = y9AuthorizationManager.insert(y9Authorization);

        String resourceName = compositeResourceManager
            .findByIdAndResourceType(y9Authorization.getResourceId(), y9Authorization.getResourceType())
            .getName();
        String principalName = Objects.equals(AuthorizationPrincipalTypeEnum.ROLE, y9Authorization.getPrincipalType())
            ? y9RoleManager.getByIdFromCache(y9Authorization.getPrincipalId()).getName()
            : compositeOrgBaseManager.getOrgUnit(y9Authorization.getPrincipalId()).getName();
        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.AUTHORIZATION_CREATE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.AUTHORIZATION_CREATE.getDescription(),
                savedAuthorization.getPrincipalType().getName(), principalName,
                y9Authorization.getAuthority().getName(), savedAuthorization.getResourceType().getName(), resourceName))
            .objectId(savedAuthorization.getId())
            .oldObject(null)
            .currentObject(savedAuthorization)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return entityToModel(savedAuthorization);
    }

    @Override
    @Transactional
    public Authorization saveOrUpdateOrg(Authorization y9Authorization) {
        Y9OrgBase y9OrgBase = compositeOrgBaseManager.getOrgUnit(y9Authorization.getPrincipalId());

        checkIfOrgUnitIncludedInResourceRelatedAuthorities(y9Authorization.getResourceId(),
            y9Authorization.getAuthority(), y9OrgBase);

        y9Authorization.setPrincipalId(y9OrgBase.getId());
        y9Authorization.setPrincipalName(y9OrgBase.getName());
        y9Authorization.setPrincipalType(AuthorizationPrincipalTypeEnum.getByName(y9OrgBase.getOrgType().getName()));
        return this.saveOrUpdate(y9Authorization);
    }

    /**
     * 检查资源的授权列表中是否已包含要添加的组织节点
     *
     * @param resourceId 资源id
     * @param authority 权限类型
     * @param y9OrgBase 组织节点
     */
    private void checkIfOrgUnitIncludedInResourceRelatedAuthorities(String resourceId, AuthorityEnum authority,
        Y9OrgBase y9OrgBase) {
        List<Y9Authorization> y9AuthorizationList =
            y9AuthorizationRepository.findByResourceIdAndAuthority(resourceId, authority);
        boolean included = y9AuthorizationList.stream()
            .map(Y9Authorization::getPrincipalId)
            .anyMatch(principalId -> y9OrgBase.getGuidPath().contains(principalId));
        if (included) {
            throw Y9ExceptionUtil.businessException(AuthorizationErrorCodeEnum.ORG_UNIT_INCLUDED, y9OrgBase.getId());
        }
    }

    @Override
    @Transactional
    public Authorization saveOrUpdateRole(Authorization authorization) {
        Y9Role role = y9RoleManager.getByIdFromCache(authorization.getPrincipalId());
        authorization.setPrincipalId(role.getId());
        authorization.setPrincipalName(role.getName());
        authorization.setPrincipalType(AuthorizationPrincipalTypeEnum.ROLE);
        return this.saveOrUpdate(authorization);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Authorization> listInheritByPrincipalTypeAndResourceId(
        AuthorizationPrincipalTypeEnum authorizationPrincipalType, String resourceId) {
        List<Y9Authorization> y9AuthorizationList = new ArrayList<>();

        String currentResourceId = resourceId;
        while (true) {
            Y9ResourceBase y9ResourceBase = compositeResourceManager.getById(currentResourceId);
            String parentResourceId = y9ResourceBase.getParentId();
            if (Boolean.FALSE.equals(y9ResourceBase.getInherit()) || StringUtils.isBlank(parentResourceId)) {
                break;
            }
            List<Y9Authorization> inheritY9AuthorizationList = y9AuthorizationRepository
                .findByResourceIdAndPrincipalType(parentResourceId, authorizationPrincipalType);
            y9AuthorizationList.addAll(inheritY9AuthorizationList);
            currentResourceId = parentResourceId;
        }
        return entityToModel(y9AuthorizationList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Authorization> listInheritByPrincipalTypeIsOrgUnitAndResourceId(String resourceId) {
        List<Y9Authorization> y9AuthorizationList = new ArrayList<>();

        String currentResourceId = resourceId;
        while (true) {
            Y9ResourceBase y9ResourceBase = compositeResourceManager.getById(currentResourceId);
            String parentResourceId = y9ResourceBase.getParentId();
            if (Boolean.FALSE.equals(y9ResourceBase.getInherit()) || StringUtils.isBlank(parentResourceId)) {
                break;
            }
            List<Y9Authorization> inheritY9AuthorizationList = y9AuthorizationRepository
                .findByResourceIdAndPrincipalTypeNot(parentResourceId, AuthorizationPrincipalTypeEnum.ROLE);
            y9AuthorizationList.addAll(inheritY9AuthorizationList);
            currentResourceId = parentResourceId;
        }
        return entityToModel(y9AuthorizationList);
    }

    @Override
    public List<String> listResourceIdByPrincipleId(String roleId, AuthorityEnum authority) {
        return y9AuthorizationRepository.listResourceIdByPrincipleId(roleId, authority);
    }

    @Override
    public List<String> listPrincipalIdByResourceId(String resourceId, AuthorityEnum authority) {
        return y9AuthorizationRepository.listRoleIdByResourceId(resourceId, authority);
    }

    private Y9Authorization getById(String id) {
        return y9AuthorizationRepository.findById(id)
            .orElseThrow(
                () -> Y9ExceptionUtil.notFoundException(AuthorizationErrorCodeEnum.AUTHORIZATION_NOT_FOUND, id));
    }

    @EventListener
    @Transactional
    public void onOrganizationDeleted(Y9EntityDeletedEvent<Y9Organization> event) {
        Y9Organization organization = event.getEntity();
        y9AuthorizationRepository.deleteByPrincipalIdAndPrincipalType(organization.getId(),
            AuthorizationPrincipalTypeEnum.ORGANIZATION);
    }

    @EventListener
    @Transactional
    public void onDepartmentDeleted(Y9EntityDeletedEvent<Y9Department> event) {
        Y9Department department = event.getEntity();
        y9AuthorizationRepository.deleteByPrincipalIdAndPrincipalType(department.getId(),
            AuthorizationPrincipalTypeEnum.DEPARTMENT);
    }

    @EventListener
    @Transactional
    public void onGroupDeleted(Y9EntityDeletedEvent<Y9Group> event) {
        Y9Group group = event.getEntity();
        y9AuthorizationRepository.deleteByPrincipalIdAndPrincipalType(group.getId(),
            AuthorizationPrincipalTypeEnum.GROUP);
    }

    @EventListener
    @Transactional
    public void onPersonDeleted(Y9EntityDeletedEvent<Y9Person> event) {
        Y9Person person = event.getEntity();
        y9AuthorizationRepository.deleteByPrincipalIdAndPrincipalType(person.getId(),
            AuthorizationPrincipalTypeEnum.PERSON);
    }

    @EventListener
    @Transactional
    public void onPositionDeleted(Y9EntityDeletedEvent<Y9Position> event) {
        Y9Position position = event.getEntity();
        y9AuthorizationRepository.deleteByPrincipalIdAndPrincipalType(position.getId(),
            AuthorizationPrincipalTypeEnum.POSITION);
    }

    @TransactionalEventListener
    public void onResourceDeleted(Y9EntityDeletedEvent<? extends Y9ResourceBase> event) {
        Y9ResourceBase entity = event.getEntity();
        for (String tenantId : Y9PlatformUtil.getTenantIds()) {
            deleteByResource(tenantId, entity);
        }
    }

    @Async
    protected void deleteByResource(String tenantId, Y9ResourceBase entity) {
        Y9LoginUserHolder.setTenantId(tenantId);
        y9AuthorizationRepository.deleteByResourceId(entity.getId());
        LOGGER.debug("{}资源[{}]删除时同步删除租户[{}]的授权数据", entity.getResourceType().getName(), entity.getId(), tenantId);
    }

    @EventListener
    public void onTenantAppDeleted(Y9EntityDeletedEvent<Y9TenantApp> event) {
        Y9TenantApp entity = event.getEntity();
        deleteByTenantApp(entity);
    }

    @Async
    protected void deleteByTenantApp(Y9TenantApp entity) {
        Y9LoginUserHolder.setTenantId(entity.getTenantId());
        List<Y9ResourceBase> y9ResourceList = compositeResourceManager.findByAppId(entity.getAppId());
        for (Y9ResourceBase y9ResourceBase : y9ResourceList) {
            y9AuthorizationRepository.deleteByResourceId(y9ResourceBase.getId());
        }
        LOGGER.debug("应用[{}]取消租用时同步删除租户[{}]的授权数据", entity.getAppId(), entity.getTenantId());
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
        List<Y9Authorization> authorizationList = y9AuthorizationRepository
            .findByPrincipalIdAndPrincipalType(entity.getId(), AuthorizationPrincipalTypeEnum.ROLE);
        for (Y9Authorization y9Authorization : authorizationList) {
            delete(y9Authorization.getId());
        }
        LOGGER.debug("角色[{}]删除时同步删除租户[{}]的角色授权数据", entity.getId(), tenantId);
    }

}
