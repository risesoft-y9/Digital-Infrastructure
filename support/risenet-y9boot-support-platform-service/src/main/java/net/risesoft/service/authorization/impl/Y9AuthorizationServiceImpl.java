package net.risesoft.service.authorization.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.enums.AuthorityEnum;
import net.risesoft.enums.AuthorizationPrincipalTypeEnum;
import net.risesoft.enums.OrgTypeEnum;
import net.risesoft.exception.AuthorizationErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.authorization.Y9PersonToResourceAndAuthorityManager;
import net.risesoft.manager.authorization.Y9PositionToResourceAndAuthorityManager;
import net.risesoft.manager.org.Y9OrgBaseManager;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.repository.permission.Y9AuthorizationRepository;
import net.risesoft.repository.relation.Y9OrgBasesToRolesRepository;
import net.risesoft.service.authorization.Y9AuthorizationService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.manager.resource.Y9ResourceBaseManager;
import net.risesoft.y9public.manager.role.Y9RoleManager;

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
    private final Y9OrgBasesToRolesRepository y9OrgBasesToRolesRepository;

    private final Y9OrgBaseManager y9OrgBaseManager;
    private final Y9ResourceBaseManager y9ResourceBaseManager;
    private final Y9RoleManager y9RoleManager;
    private final Y9PersonToResourceAndAuthorityManager y9PersonToResourceAndAuthorityManager;
    private final Y9PositionToResourceAndAuthorityManager y9PositionToResourceAndAuthorityManager;

    /**
     * 授权记录中是否有隐藏授权
     *
     * @param resourceRelatedY9AuthorizationList
     * @return
     */
    private boolean anyHidden(List<Y9Authorization> resourceRelatedY9AuthorizationList) {
        return resourceRelatedY9AuthorizationList.stream().anyMatch(authorization -> AuthorityEnum.HIDDEN.getValue().equals(authorization.getAuthority()));
    }

    /**
     * 是否有对某种 PrincipalType 的授权记录
     *
     * @param y9AuthorizationList
     * @param authorizationPrincipalType
     * @return
     */
    private boolean anyPrincipleType(List<Y9Authorization> y9AuthorizationList, AuthorizationPrincipalTypeEnum authorizationPrincipalType) {
        return y9AuthorizationList.stream().anyMatch(authorization -> authorizationPrincipalType.getValue().equals(authorization.getPrincipalType()));
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        Y9Authorization y9Authorization = this.getById(id);
        y9AuthorizationRepository.delete(y9Authorization);

        y9PersonToResourceAndAuthorityManager.deleteByAuthorizationId(id);
        y9PositionToResourceAndAuthorityManager.deleteByAuthorizationId(id);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String principalId, String[] resourceIds) {
        for (String resourceId : resourceIds) {
            List<Y9Authorization> permissionList = getAuthorization(principalId, resourceId);
            y9AuthorizationRepository.deleteAll(permissionList);
        }
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
    @Transactional(readOnly = false)
    public void delete(Y9Authorization acRolePermission) {
        y9AuthorizationRepository.delete(acRolePermission);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByResourceId(String resourceId) {
        y9AuthorizationRepository.deleteByResourceId(resourceId);
    }

    private List<Y9Authorization> filter(List<Y9Authorization> resourceRelatedY9AuthorizationList, AuthorizationPrincipalTypeEnum authorizationPrincipalTypeFilter) {
        return resourceRelatedY9AuthorizationList.stream().filter(authorization -> authorizationPrincipalTypeFilter.getValue().equals(authorization.getPrincipalType())).collect(Collectors.toList());
    }

    @Override
    public Y9Authorization findById(String id) {
        return y9AuthorizationRepository.findById(id).orElse(null);
    }

    private List<Y9Authorization> getAuthorization(String principalId, String resourceId) {
        return y9AuthorizationRepository.findByPrincipalIdAndResourceIdAndAuthorityIsNot(principalId, resourceId, AuthorityEnum.HIDDEN.getValue(), Sort.by(Sort.Direction.ASC, "createTime"));
    }

    private Y9Authorization getById(String id) {
        return y9AuthorizationRepository.findById(id).orElseThrow(() -> Y9ExceptionUtil.notFoundException(AuthorizationErrorCodeEnum.AUTHORIZATION_NOT_FOUND, id));
    }

    /**
     * 拿到人员或岗位所有关联的组织和角色id
     *
     * @param identityId
     * @return
     */
    private List<String> getIdentityRelatedPrincipalId(String identityId) {
        List<String> principalIdList = new ArrayList<>();

        List<Y9Role> y9RoleList = y9RoleManager.listOrgUnitRelatedWithoutNegative(identityId);
        principalIdList.addAll(y9RoleList.stream().map(Y9Role::getId).collect(Collectors.toList()));

        List<String> orgUnitIdList = y9RoleManager.listOrgUnitIdRecursively(identityId);
        principalIdList.addAll(orgUnitIdList);

        return principalIdList.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<Y9Authorization> listByPrincipalId(String principalId) {
        return y9AuthorizationRepository.findByPrincipalIdOrderByCreateTime(principalId);
    }

    @Override
    public List<Y9Authorization> listByPrincipalIdAndPrincipalType(String principalId, AuthorizationPrincipalTypeEnum principalTypeEnum) {
        return y9AuthorizationRepository.findByPrincipalIdAndPrincipalType(principalId, principalTypeEnum.getValue());
    }

    @Override
    public List<Y9Authorization> listByPrincipalIdAndResourceId(String principalId, String resourceId) {
        return y9AuthorizationRepository.findByPrincipalIdAndResourceId(principalId, resourceId, Sort.by(Sort.Direction.ASC, "createTime"));
    }

    @Override
    public List<Y9Authorization> listByPrincipalTypeAndResourceId(Integer principalType, String resourceId) {
        return y9AuthorizationRepository.findByPrincipalTypeAndResourceId(principalType, resourceId);
    }

    @Override
    public List<Y9Authorization> listByPrincipalTypeNotAndResourceId(Integer principalType, String resourceId) {
        return y9AuthorizationRepository.findByPrincipalTypeNotAndResourceId(principalType, resourceId);
    }

    @Override
    public List<Y9Authorization> listByResourceId(String resourceId) {
        return y9AuthorizationRepository.findByResourceId(resourceId);
    }

    private void listByResourceIdRelated(List<Y9Authorization> authorizationList, String resourceId) {
        if (StringUtils.isNotBlank(resourceId)) {
            authorizationList.addAll(y9AuthorizationRepository.findByResourceId(resourceId));
            listByResourceIdRelated(authorizationList, y9ResourceBaseManager.findById(resourceId).getParentId());
        }
    }

    @Override
    public List<Y9Authorization> listByResourceIdRelated(String resourceId) {
        List<Y9Authorization> authorizationList = new ArrayList<>();
        listByResourceIdRelated(authorizationList, resourceId);
        return authorizationList;
    }

    @Override
    public List<Y9Authorization> listByResourceIds(List<String> resourceIds, String principalId, Integer authority) {
        List<Y9Authorization> list = new ArrayList<>();
        for (String resourceId : resourceIds) {
            List<Y9Authorization> rolePermissions = y9AuthorizationRepository.findByResourceIdAndAuthorityAndPrincipalId(resourceId, authority, principalId);
            if (rolePermissions != null && !rolePermissions.isEmpty()) {
                list.addAll(rolePermissions);
            }
        }
        return list;
    }

    @Override
    public List<Y9Authorization> listByRoleIds(List<String> principalIds, String resourceId, Integer authority) {
        return y9AuthorizationRepository.findByResourceIdAndAuthorityAndPrincipalIdIn(resourceId, authority, principalIds);
    }

    @Override
    public List<String> listResourceIdByPrincipalIdsAndAuthority(List<String> principalIds, Integer authority) {
        return y9AuthorizationRepository.getResourceIdList(authority, principalIds);
    }

    @Override
    public Page<Y9Authorization> page(Y9PageQuery pageQuery, String resourceId, Integer principalType) {
        PageRequest pageRequest = PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by("createTime"));
        return y9AuthorizationRepository.findByResourceIdAndPrincipalType(resourceId, principalType, pageRequest);
    }

    @Override
    public Page<Y9Authorization> pageByPrincipalId(String principalId, Integer rows, Integer page) {
        if (page < 1) {
            page = 1;
        }
        PageRequest pageable = PageRequest.of(page - 1, rows);
        if (principalId != null) {
            return y9AuthorizationRepository.findByPrincipalId(principalId, pageable);
        } else {
            return y9AuthorizationRepository.findAll(pageable);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void save(Integer authority, String principalId, Integer principalType, String[] resourceIds) {
        for (String id : resourceIds) {
            Y9Authorization y9Authorization = new Y9Authorization();
            y9Authorization.setAuthority(authority);
            y9Authorization.setPrincipalId(principalId);
            y9Authorization.setResourceId(id);
            if (AuthorizationPrincipalTypeEnum.ROLE.getValue().equals(principalType)) {
                this.saveOrUpdateRole(y9Authorization);
            } else {
                this.saveOrUpdateOrg(y9Authorization);
            } 
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void saveByOrg(Integer authority, String resourceId, String[] principleIds) {
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
    public void saveByRoles(Integer authority, String resourceId, String[] roleIds) {
        for (String roleId : roleIds) {
            Y9Authorization y9Authorization = new Y9Authorization();
            y9Authorization.setPrincipalId(roleId);
            y9Authorization.setAuthority(authority);
            y9Authorization.setResourceId(resourceId);
            saveOrUpdateRole(y9Authorization);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Authorization saveOrUpdate(Y9Authorization y9Authorization) {
        // 如已存在则修改
        Y9Authorization old = y9AuthorizationRepository.findByPrincipalIdAndResourceIdAndAuthority(y9Authorization.getPrincipalId(), y9Authorization.getResourceId(), y9Authorization.getAuthority());
        if (old != null) {
            Y9BeanUtil.copyProperties(y9Authorization, old, "id");
            return y9AuthorizationRepository.save(old);
        }

        // 新增
        if (StringUtils.isBlank(y9Authorization.getId())) {
            y9Authorization.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }

        Y9ResourceBase y9ResourceBase = y9ResourceBaseManager.findById(y9Authorization.getResourceId());
        y9Authorization.setResourceName(y9ResourceBase.getName());
        y9Authorization.setResourceType(y9ResourceBase.getResourceType());
        y9Authorization.setTenantId(Y9LoginUserHolder.getTenantId());
        y9Authorization.setAuthorizer(Y9LoginUserHolder.getUserInfo().getName());
        Y9Authorization savedY9Authorization = y9AuthorizationRepository.save(y9Authorization);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedY9Authorization));

        return savedY9Authorization;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Authorization saveOrUpdateOrg(Y9Authorization y9Authorization) {
        Y9OrgBase y9OrgBase = y9OrgBaseManager.getOrgBase(y9Authorization.getPrincipalId());
        y9Authorization.setPrincipalId(y9OrgBase.getId());
        y9Authorization.setPrincipalName(y9OrgBase.getName());
        y9Authorization.setPrincipalType(AuthorizationPrincipalTypeEnum.getValueByName(OrgTypeEnum.getByEnName(y9OrgBase.getOrgType()).getName()));
        return this.saveOrUpdate(y9Authorization);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Authorization saveOrUpdateRole(Y9Authorization y9Authorization) {
        Y9Role role = y9RoleManager.getById(y9Authorization.getPrincipalId());
        y9Authorization.setPrincipalId(role.getId());
        y9Authorization.setPrincipalName(role.getName());
        y9Authorization.setPrincipalType(AuthorizationPrincipalTypeEnum.ROLE.getValue());
        return this.saveOrUpdate(y9Authorization);
    }

    @Transactional(readOnly = false)
    public void syncToIdentityResourceAndAuthority(Map<String, List<Y9Authorization>> resourceIdAuthorizationListMap, final List<String> solvedResourceIdList, Y9ResourceBase y9ResourceBase, Y9Person person, Y9Authorization inheritY9Authorization) {
        String resourceId = y9ResourceBase.getId();
        if (!solvedResourceIdList.contains(resourceId)) {
            // 对于已经处理过的资源及其子资源直接跳过
            solvedResourceIdList.add(resourceId);

            List<Y9Authorization> resourceRelatedY9AuthorizationList = resourceIdAuthorizationListMap.get(resourceId);
            if (resourceRelatedY9AuthorizationList != null && !resourceRelatedY9AuthorizationList.isEmpty()) {
                // 存在对资源的直接授权 不继承权限

                List<Y9Authorization> y9AuthorizationList = resourceRelatedY9AuthorizationList;

                if (anyPrincipleType(resourceRelatedY9AuthorizationList, AuthorizationPrincipalTypeEnum.PERSON)) {
                    // 对人直接授权 优先计算
                    y9AuthorizationList = filter(resourceRelatedY9AuthorizationList, AuthorizationPrincipalTypeEnum.PERSON);
                }
                if (anyHidden(y9AuthorizationList)) {
                    return;
                }

                for (Y9Authorization y9Authorization : y9AuthorizationList) {
                    y9PersonToResourceAndAuthorityManager.saveOrUpdate(y9ResourceBase, person, y9Authorization, Boolean.FALSE);

                    // 递归处理子资源
                    List<Y9ResourceBase> subResourceList = y9ResourceBaseManager.listChildrenById(y9ResourceBase.getId());
                    for (Y9ResourceBase resource : subResourceList) {
                        syncToIdentityResourceAndAuthority(resourceIdAuthorizationListMap, solvedResourceIdList, resource, person, y9Authorization);
                    }
                }

            } else if (Boolean.TRUE.equals(y9ResourceBase.getInherit()) && !AuthorityEnum.HIDDEN.getValue().equals(inheritY9Authorization.getAuthority())) {
                // 不存在对资源的直接授权 继承权限
                y9PersonToResourceAndAuthorityManager.saveOrUpdate(y9ResourceBase, person, inheritY9Authorization, Boolean.TRUE);

                // 递归处理子资源
                List<Y9ResourceBase> subResourceList = y9ResourceBaseManager.listChildrenById(y9ResourceBase.getId());
                for (Y9ResourceBase resource : subResourceList) {
                    syncToIdentityResourceAndAuthority(resourceIdAuthorizationListMap, solvedResourceIdList, resource, person, inheritY9Authorization);
                }
            }
        }
    }

    @Transactional(readOnly = false)
    public void syncToIdentityResourceAndAuthority(final Map<String, List<Y9Authorization>> resourceIdAuthorizationListMap, final List<String> solvedResourceIdList, Y9ResourceBase y9ResourceBase, Y9Position position, Y9Authorization inheritY9Authorization) {
        String resourceId = y9ResourceBase.getId();
        if (!solvedResourceIdList.contains(resourceId)) {
            // 对于已经处理过的资源及其子资源直接跳过
            solvedResourceIdList.add(resourceId);

            List<Y9Authorization> resourceRelatedY9AuthorizationList = resourceIdAuthorizationListMap.get(y9ResourceBase.getId());
            if (resourceRelatedY9AuthorizationList != null && !resourceRelatedY9AuthorizationList.isEmpty()) {
                // 存在对资源的直接授权 不继承权限

                List<Y9Authorization> y9AuthorizationList = resourceRelatedY9AuthorizationList;

                if (anyPrincipleType(resourceRelatedY9AuthorizationList, AuthorizationPrincipalTypeEnum.POSITION)) {
                    // 对岗直接授权 优先计算
                    y9AuthorizationList = filter(resourceRelatedY9AuthorizationList, AuthorizationPrincipalTypeEnum.POSITION);
                }

                if (anyHidden(y9AuthorizationList)) {
                    return;
                }

                for (Y9Authorization y9Authorization : y9AuthorizationList) {
                    y9PositionToResourceAndAuthorityManager.saveOrUpdate(y9ResourceBase, position, y9Authorization, Boolean.FALSE);
                    // 递归处理子资源
                    List<Y9ResourceBase> subResourceList = y9ResourceBaseManager.listChildrenById(y9ResourceBase.getId());
                    for (Y9ResourceBase resource : subResourceList) {
                        syncToIdentityResourceAndAuthority(resourceIdAuthorizationListMap, solvedResourceIdList, resource, position, y9Authorization);
                    }
                }

            } else if (Boolean.TRUE.equals(y9ResourceBase.getInherit()) && !AuthorityEnum.HIDDEN.getValue().equals(inheritY9Authorization.getAuthority())) {
                // 不存在对资源的直接授权 继承权限

                y9PositionToResourceAndAuthorityManager.saveOrUpdate(y9ResourceBase, position, inheritY9Authorization, Boolean.TRUE);

                // 递归处理子资源
                List<Y9ResourceBase> subResourceList = y9ResourceBaseManager.listChildrenById(y9ResourceBase.getId());
                for (Y9ResourceBase resource : subResourceList) {
                    syncToIdentityResourceAndAuthority(resourceIdAuthorizationListMap, solvedResourceIdList, resource, position, inheritY9Authorization);
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void syncToIdentityResourceAndAuthority(String orgUnitId) {
        Y9OrgBase y9OrgBase = y9OrgBaseManager.getOrgBase(orgUnitId);
        if (OrgTypeEnum.PERSON.getEnName().equals(y9OrgBase.getOrgType())) {
            syncToIdentityResourceAndAuthority((Y9Person)y9OrgBase);
            return;
        }

        if (OrgTypeEnum.POSITION.getEnName().equals(y9OrgBase.getOrgType())) {
            syncToIdentityResourceAndAuthority((Y9Position)y9OrgBase);
            return;
        }

        List<Y9Person> personList = y9OrgBaseManager.listAllPersonsRecursionDownward(orgUnitId);
        for (Y9Person person : personList) {
            syncToIdentityResourceAndAuthority(person);
        }
        List<Y9Position> positionList = y9OrgBaseManager.listAllPositionsRecursionDownward(orgUnitId);
        for (Y9Position position : positionList) {
            syncToIdentityResourceAndAuthority(position);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void syncToIdentityResourceAndAuthority(Y9Person person) {
        List<String> principalIdList = getIdentityRelatedPrincipalId(person.getId());
        List<Y9Authorization> principalRelatedY9AuthorizationList = y9AuthorizationRepository.getByPrincipalIdIn(principalIdList);
        Map<String, List<Y9Authorization>> resourceIdAuthorizationListMap = principalRelatedY9AuthorizationList.stream().collect(Collectors.groupingBy(Y9Authorization::getResourceId));
        for (String resourceId : resourceIdAuthorizationListMap.keySet()) {
            Y9ResourceBase y9ResourceBase = y9ResourceBaseManager.findById(resourceId);
            syncToIdentityResourceAndAuthority(resourceIdAuthorizationListMap, new ArrayList<>(), y9ResourceBase, person, new Y9Authorization());
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void syncToIdentityResourceAndAuthority(Y9Position position) {
        List<String> principalIdList = getIdentityRelatedPrincipalId(position.getId());
        List<Y9Authorization> principalRelatedY9AuthorizationList = y9AuthorizationRepository.getByPrincipalIdIn(principalIdList);
        Map<String, List<Y9Authorization>> resourceIdAuthorizationListMap = principalRelatedY9AuthorizationList.stream().collect(Collectors.groupingBy(Y9Authorization::getResourceId));
        for (String resourceId : resourceIdAuthorizationListMap.keySet()) {
            Y9ResourceBase y9ResourceBase = y9ResourceBaseManager.findById(resourceId);
            syncToIdentityResourceAndAuthority(resourceIdAuthorizationListMap, new ArrayList<>(), y9ResourceBase, position, new Y9Authorization());
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void syncToIdentityResourceAndAuthorityByResourceId(String resourceId) {
        List<Y9Authorization> authorizationList = listByResourceIdRelated(resourceId);
        for (Y9Authorization y9Authorization : authorizationList) {
            if (AuthorizationPrincipalTypeEnum.ROLE.getValue().equals(y9Authorization.getPrincipalType())) {
                List<String> orgUnitIdList = y9OrgBasesToRolesRepository.listOrgIdsByRoleId(y9Authorization.getPrincipalId());
                for (String orgUnitId : orgUnitIdList) {
                    syncToIdentityResourceAndAuthority(orgUnitId);
                }
            } else {
                syncToIdentityResourceAndAuthority(y9Authorization.getPrincipalId());
            }
        }
    }

}
