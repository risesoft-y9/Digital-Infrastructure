package net.risesoft.service.authorization.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.enums.platform.AuthorizationPrincipalTypeEnum;
import net.risesoft.exception.AuthorizationErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.identity.Y9PersonToResourceAndAuthorityManager;
import net.risesoft.manager.identity.Y9PositionToResourceAndAuthorityManager;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.repository.permission.Y9AuthorizationRepository;
import net.risesoft.service.authorization.Y9AuthorizationService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.manager.role.Y9RoleManager;
import net.risesoft.y9public.service.resource.CompositeResourceService;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9AuthorizationServiceImpl implements Y9AuthorizationService {

    private final Y9AuthorizationRepository y9AuthorizationRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final CompositeResourceService compositeResourceService;
    private final Y9RoleManager y9RoleManager;
    private final Y9PersonToResourceAndAuthorityManager y9PersonToResourceAndAuthorityManager;
    private final Y9PositionToResourceAndAuthorityManager y9PositionToResourceAndAuthorityManager;

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        Y9Authorization y9Authorization = this.getById(id);

        y9AuthorizationRepository.delete(y9Authorization);
        y9PersonToResourceAndAuthorityManager.deleteByAuthorizationId(id);
        y9PositionToResourceAndAuthorityManager.deleteByAuthorizationId(id);

        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Authorization));
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String[] ids) {
        if (ids != null) {
            for (String id : ids) {
                this.delete(id);
            }
        }
    }

    @Override
    public Optional<Y9Authorization> findById(String id) {
        return y9AuthorizationRepository.findById(id);
    }

    @Override
    public List<Y9Authorization> listByPrincipalId(String principalId) {
        return y9AuthorizationRepository.findByPrincipalIdOrderByCreateTime(principalId);
    }

    @Override
    public List<Y9Authorization> listByPrincipalIdAndPrincipalType(String principalId,
        AuthorizationPrincipalTypeEnum principalTypeEnum) {
        return y9AuthorizationRepository.findByPrincipalIdAndPrincipalType(principalId, principalTypeEnum);
    }

    @Override
    public List<Y9Authorization> listByPrincipalIdAndResourceId(String principalId, String resourceId) {
        return y9AuthorizationRepository.findByPrincipalIdAndResourceId(principalId, resourceId,
            Sort.by(Sort.Direction.ASC, "createTime"));
    }

    @Override
    public List<Y9Authorization> listByPrincipalTypeAndResourceId(AuthorizationPrincipalTypeEnum principalType,
        String resourceId) {
        return y9AuthorizationRepository.findByResourceIdAndPrincipalType(resourceId, principalType);
    }

    @Override
    public List<Y9Authorization> listByPrincipalTypeNotAndResourceId(AuthorizationPrincipalTypeEnum principalType,
        String resourceId) {
        return y9AuthorizationRepository.findByResourceIdAndPrincipalTypeNot(resourceId, principalType);
    }

    @Override
    public List<Y9Authorization> listByResourceId(String resourceId) {
        return y9AuthorizationRepository.findByResourceId(resourceId);
    }

    @Override
    public List<Y9Authorization> listByRoleIds(List<String> principalIds, String resourceId, AuthorityEnum authority) {
        return y9AuthorizationRepository.findByResourceIdAndAuthorityAndPrincipalIdIn(resourceId, authority,
            principalIds);
    }

    @Override
    public Page<Y9Authorization> page(Y9PageQuery pageQuery, String resourceId,
        AuthorizationPrincipalTypeEnum principalType) {
        PageRequest pageRequest = PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by("createTime"));
        return y9AuthorizationRepository.findByResourceIdAndPrincipalType(resourceId, principalType, pageRequest);
    }

    @Override
    public Page<Y9Authorization> pageByPrincipalId(String principalId, Y9PageQuery pageQuery) {
        PageRequest pageable = PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by("createTime"));
        return y9AuthorizationRepository.findByPrincipalId(principalId, pageable);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(AuthorityEnum authority, String principalId, AuthorizationPrincipalTypeEnum principalType,
        String[] resourceIds) {
        for (String id : resourceIds) {
            Y9Authorization y9Authorization = new Y9Authorization();
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
    @Transactional(readOnly = false)
    public void saveByOrg(AuthorityEnum authority, String resourceId, String[] principleIds) {
        for (String principleId : principleIds) {
            Y9Authorization y9Authorization = new Y9Authorization();
            y9Authorization.setPrincipalId(principleId);
            y9Authorization.setAuthority(authority);
            y9Authorization.setResourceId(resourceId);
            this.saveOrUpdateOrg(y9Authorization);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void saveByRoles(AuthorityEnum authority, String resourceId, String[] roleIds) {
        for (String roleId : roleIds) {
            Y9Authorization y9Authorization = new Y9Authorization();
            y9Authorization.setPrincipalId(roleId);
            y9Authorization.setAuthority(authority);
            y9Authorization.setResourceId(resourceId);
            this.saveOrUpdateRole(y9Authorization);
        }
    }

    @Transactional(readOnly = false)
    public Y9Authorization saveOrUpdate(Y9Authorization y9Authorization) {
        // 如已存在则修改
        Optional<Y9Authorization> optionalY9Authorization =
            y9AuthorizationRepository.findByPrincipalIdAndResourceIdAndAuthority(y9Authorization.getPrincipalId(),
                y9Authorization.getResourceId(), y9Authorization.getAuthority());
        if (optionalY9Authorization.isPresent()) {
            Y9Authorization originY9Authorization = optionalY9Authorization.get();
            Y9BeanUtil.copyProperties(y9Authorization, originY9Authorization, "id");
            return y9AuthorizationRepository.save(originY9Authorization);
        }

        // 新增
        if (StringUtils.isBlank(y9Authorization.getId())) {
            y9Authorization.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }

        Y9ResourceBase y9ResourceBase = compositeResourceService.findById(y9Authorization.getResourceId());
        y9Authorization.setResourceName(y9ResourceBase.getName());
        y9Authorization.setResourceType(y9ResourceBase.getResourceType());
        y9Authorization.setTenantId(Y9LoginUserHolder.getTenantId());
        y9Authorization
            .setAuthorizer(Optional.ofNullable(Y9LoginUserHolder.getUserInfo()).map(UserInfo::getName).orElse(null));
        Y9Authorization savedY9Authorization = y9AuthorizationRepository.save(y9Authorization);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedY9Authorization));

        return savedY9Authorization;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Authorization saveOrUpdateOrg(Y9Authorization y9Authorization) {
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
        boolean included = y9AuthorizationList.stream().map(Y9Authorization::getPrincipalId)
            .anyMatch(principalId -> y9OrgBase.getGuidPath().contains(principalId));
        if (included) {
            throw Y9ExceptionUtil.businessException(AuthorizationErrorCodeEnum.ORG_UNIT_INCLUDED, y9OrgBase.getId());
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Authorization saveOrUpdateRole(Y9Authorization y9Authorization) {
        Y9Role role = y9RoleManager.getById(y9Authorization.getPrincipalId());
        y9Authorization.setPrincipalId(role.getId());
        y9Authorization.setPrincipalName(role.getName());
        y9Authorization.setPrincipalType(AuthorizationPrincipalTypeEnum.ROLE);
        return this.saveOrUpdate(y9Authorization);
    }

    @Override
    public List<Y9Authorization> listInheritByPrincipalTypeAndResourceId(
        AuthorizationPrincipalTypeEnum authorizationPrincipalType, String resourceId) {
        List<Y9Authorization> y9AuthorizationList = new ArrayList<>();

        String currentResourceId = resourceId;
        while (true) {
            Y9ResourceBase y9ResourceBase = compositeResourceService.getById(currentResourceId);
            String parentResourceId = y9ResourceBase.getParentId();
            if (Boolean.FALSE.equals(y9ResourceBase.getInherit()) || StringUtils.isBlank(parentResourceId)) {
                break;
            }
            List<Y9Authorization> inheritY9AuthorizationList = y9AuthorizationRepository
                .findByResourceIdAndPrincipalType(parentResourceId, authorizationPrincipalType);
            y9AuthorizationList.addAll(inheritY9AuthorizationList);
            currentResourceId = parentResourceId;
        }
        return y9AuthorizationList;
    }

    @Override
    public List<Y9Authorization> listInheritByPrincipalTypeIsOrgUnitAndResourceId(String resourceId) {
        List<Y9Authorization> y9AuthorizationList = new ArrayList<>();

        String currentResourceId = resourceId;
        while (true) {
            Y9ResourceBase y9ResourceBase = compositeResourceService.getById(currentResourceId);
            String parentResourceId = y9ResourceBase.getParentId();
            if (Boolean.FALSE.equals(y9ResourceBase.getInherit()) || StringUtils.isBlank(parentResourceId)) {
                break;
            }
            List<Y9Authorization> inheritY9AuthorizationList = y9AuthorizationRepository
                .findByResourceIdAndPrincipalTypeNot(parentResourceId, AuthorizationPrincipalTypeEnum.ROLE);
            y9AuthorizationList.addAll(inheritY9AuthorizationList);
            currentResourceId = parentResourceId;
        }
        return y9AuthorizationList;
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
        return y9AuthorizationRepository.findById(id).orElseThrow(
            () -> Y9ExceptionUtil.notFoundException(AuthorizationErrorCodeEnum.AUTHORIZATION_NOT_FOUND, id));
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onOrganizationDeleted(Y9EntityDeletedEvent<Y9Organization> event) {
        Y9Organization organization = event.getEntity();
        y9AuthorizationRepository.deleteByPrincipalIdAndPrincipalType(organization.getId(),
            AuthorizationPrincipalTypeEnum.ORGANIZATION);
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onDepartmentDeleted(Y9EntityDeletedEvent<Y9Department> event) {
        Y9Department department = event.getEntity();
        y9AuthorizationRepository.deleteByPrincipalIdAndPrincipalType(department.getId(),
            AuthorizationPrincipalTypeEnum.DEPARTMENT);
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onGroupDeleted(Y9EntityDeletedEvent<Y9Group> event) {
        Y9Group group = event.getEntity();
        y9AuthorizationRepository.deleteByPrincipalIdAndPrincipalType(group.getId(),
            AuthorizationPrincipalTypeEnum.GROUP);
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onPersonDeleted(Y9EntityDeletedEvent<Y9Person> event) {
        Y9Person person = event.getEntity();
        y9AuthorizationRepository.deleteByPrincipalIdAndPrincipalType(person.getId(),
            AuthorizationPrincipalTypeEnum.PERSON);
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onPositionDeleted(Y9EntityDeletedEvent<Y9Position> event) {
        Y9Position position = event.getEntity();
        y9AuthorizationRepository.deleteByPrincipalIdAndPrincipalType(position.getId(),
            AuthorizationPrincipalTypeEnum.POSITION);
    }
}
