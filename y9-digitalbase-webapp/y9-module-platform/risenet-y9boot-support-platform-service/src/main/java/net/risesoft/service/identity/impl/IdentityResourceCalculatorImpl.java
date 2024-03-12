package net.risesoft.service.identity.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.enums.platform.AuthorizationPrincipalTypeEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.manager.identity.Y9PersonToResourceAndAuthorityManager;
import net.risesoft.manager.identity.Y9PositionToResourceAndAuthorityManager;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.repository.permission.Y9AuthorizationRepository;
import net.risesoft.repository.relation.Y9OrgBasesToRolesRepository;
import net.risesoft.service.identity.IdentityResourceCalculator;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.manager.role.Y9RoleManager;
import net.risesoft.y9public.service.resource.CompositeResourceService;

/**
 * 身份资源计算实现
 *
 * @author shidaobang
 * @date 2024/03/08
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class IdentityResourceCalculatorImpl implements IdentityResourceCalculator {

    private final Y9AuthorizationRepository y9AuthorizationRepository;
    private final Y9OrgBasesToRolesRepository y9OrgBasesToRolesRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final CompositeResourceService compositeResourceService;
    private final Y9PersonManager y9PersonManager;
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
        return resourceRelatedY9AuthorizationList.stream()
            .anyMatch(authorization -> AuthorityEnum.HIDDEN.equals(authorization.getAuthority()));
    }

    /**
     * 根据资源id找到与其相关的授权（继承的）
     *
     * @param resourceId 资源id
     * @return {@link List}<{@link Y9Authorization}>
     */
    public List<Y9Authorization> listByResourceIdRelated(String resourceId) {
        List<Y9Authorization> authorizationList = new ArrayList<>();
        listByResourceIdRelated(authorizationList, resourceId);
        return authorizationList;
    }

    @Override
    public void recalculateByOrgUnitId(String orgUnitId) {
        Optional<Y9OrgBase> y9OrgBaseOptional = compositeOrgBaseManager.findOrgUnit(orgUnitId);
        if (y9OrgBaseOptional.isPresent()) {
            Y9OrgBase y9OrgBase = y9OrgBaseOptional.get();
            if (OrgTypeEnum.PERSON.equals(y9OrgBase.getOrgType())) {
                this.recalculateByPerson((Y9Person)y9OrgBase);
                return;
            }

            if (OrgTypeEnum.POSITION.equals(y9OrgBase.getOrgType())) {
                this.recalculateByPosition((Y9Position)y9OrgBase);
                // 人员权限包含包含岗位的权限，所以岗位关联的人员也需要计算
                List<Y9Person> y9PersonList = y9PersonManager.listByPositionId(y9OrgBase.getId());
                for (Y9Person y9Person : y9PersonList) {
                    this.recalculateByPerson(y9Person);
                }
                return;
            }

            List<Y9Person> personList = compositeOrgBaseManager.listAllPersonsRecursionDownward(orgUnitId);
            for (Y9Person person : personList) {
                this.recalculateByPerson(person);
            }
            List<Y9Position> positionList = compositeOrgBaseManager.listAllPositionsRecursionDownward(orgUnitId);
            for (Y9Position position : positionList) {
                this.recalculateByPosition(position);
            }
        }
    }

    @Override
    public void recalculateByPerson(Y9Person person) {
        try {
            List<String> principalIdList = getIdentityRelatedPrincipalId(person.getId());
            List<Y9Authorization> principalRelatedY9AuthorizationList =
                y9AuthorizationRepository.getByPrincipalIdIn(principalIdList);

            // 清除已去掉的权限
            List<String> authorizationIdList =
                principalRelatedY9AuthorizationList.stream().map(Y9Authorization::getId).collect(Collectors.toList());
            y9PersonToResourceAndAuthorityManager.deleteByPersonIdAndAuthorizationIdNotIn(person.getId(),
                authorizationIdList);

            Map<String, List<Y9Authorization>> resourceIdAuthorizationListMap = principalRelatedY9AuthorizationList
                .stream().collect(Collectors.groupingBy(Y9Authorization::getResourceId));
            for (String resourceId : resourceIdAuthorizationListMap.keySet()) {
                Y9ResourceBase y9ResourceBase = compositeResourceService.findById(resourceId);
                this.recalculateByPerson(resourceIdAuthorizationListMap, new ArrayList<>(), y9ResourceBase, person,
                    new Y9Authorization());
            }
        } catch (Exception e) {
            LOGGER.warn("计算人员[{}]权限发生异常", person.getId(), e);
        }
    }

    @Override
    public void recalculateByPosition(Y9Position position) {
        try {
            List<String> principalIdList = getIdentityRelatedPrincipalId(position.getId());
            List<Y9Authorization> principalRelatedY9AuthorizationList =
                y9AuthorizationRepository.getByPrincipalIdIn(principalIdList);

            // 清除已去掉的权限
            List<String> authorizationIdList =
                principalRelatedY9AuthorizationList.stream().map(Y9Authorization::getId).collect(Collectors.toList());
            y9PositionToResourceAndAuthorityManager.deleteByPositionIdAndAuthorizationIdNotIn(position.getId(),
                authorizationIdList);

            Map<String, List<Y9Authorization>> resourceIdAuthorizationListMap = principalRelatedY9AuthorizationList
                .stream().collect(Collectors.groupingBy(Y9Authorization::getResourceId));
            for (String resourceId : resourceIdAuthorizationListMap.keySet()) {
                Y9ResourceBase y9ResourceBase = compositeResourceService.findById(resourceId);
                this.recalculateByPosition(resourceIdAuthorizationListMap, new ArrayList<>(), y9ResourceBase, position,
                    new Y9Authorization());
            }
        } catch (Exception e) {
            LOGGER.warn("计算岗位[{}]权限发生异常", position.getId(), e);
        }
    }

    @Override
    public void recalculateByResourceId(String resourceId) {
        List<Y9Authorization> authorizationList = listByResourceIdRelated(resourceId);
        for (Y9Authorization y9Authorization : authorizationList) {
            if (AuthorizationPrincipalTypeEnum.ROLE.equals(y9Authorization.getPrincipalType())) {
                List<String> orgUnitIdList =
                    y9OrgBasesToRolesRepository.listOrgIdsByRoleId(y9Authorization.getPrincipalId());
                for (String orgUnitId : orgUnitIdList) {
                    this.recalculateByOrgUnitId(orgUnitId);
                }
            } else {
                this.recalculateByOrgUnitId(y9Authorization.getPrincipalId());
            }
        }
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

    private void listByResourceIdRelated(List<Y9Authorization> authorizationList, String resourceId) {
        if (StringUtils.isNotBlank(resourceId)) {
            authorizationList.addAll(y9AuthorizationRepository.findByResourceId(resourceId));
            listByResourceIdRelated(authorizationList, compositeResourceService.findById(resourceId).getParentId());
        }
    }

    private void recalculateByPerson(Map<String, List<Y9Authorization>> resourceIdAuthorizationListMap,
        final List<String> solvedResourceIdList, Y9ResourceBase y9ResourceBase, Y9Person person,
        Y9Authorization inheritY9Authorization) {
        String resourceId = y9ResourceBase.getId();
        if (!solvedResourceIdList.contains(resourceId)) {
            // 对于已经处理过的资源及其子资源直接跳过
            solvedResourceIdList.add(resourceId);

            List<Y9Authorization> resourceRelatedY9AuthorizationList = resourceIdAuthorizationListMap.get(resourceId);
            if (resourceRelatedY9AuthorizationList != null && !resourceRelatedY9AuthorizationList.isEmpty()) {
                // 存在对资源的直接授权 不继承权限

                if (anyHidden(resourceRelatedY9AuthorizationList)) {
                    // 隐藏权限类型优先级最高
                    y9PersonToResourceAndAuthorityManager.deleteByPersonIdAndResourceId(person.getId(), resourceId);
                    return;
                }
                // if (anyPrincipleType(resourceRelatedY9AuthorizationList, AuthorizationPrincipalTypeEnum.PERSON)) {
                // // 对人直接授权 优先计算
                // y9AuthorizationList =
                // filter(resourceRelatedY9AuthorizationList, AuthorizationPrincipalTypeEnum.PERSON);
                // }

                for (Y9Authorization y9Authorization : resourceRelatedY9AuthorizationList) {
                    y9PersonToResourceAndAuthorityManager.saveOrUpdate(y9ResourceBase, person, y9Authorization,
                        Boolean.FALSE);

                    // 递归处理子资源
                    List<Y9ResourceBase> subResourceList =
                        compositeResourceService.listChildrenById(y9ResourceBase.getId());
                    for (Y9ResourceBase resource : subResourceList) {
                        this.recalculateByPerson(resourceIdAuthorizationListMap, solvedResourceIdList, resource, person,
                            y9Authorization);
                    }
                }

            } else if (Boolean.TRUE.equals(y9ResourceBase.getInherit())
                && !AuthorityEnum.HIDDEN.equals(inheritY9Authorization.getAuthority())) {
                // 不存在对资源的直接授权 继承权限
                y9PersonToResourceAndAuthorityManager.saveOrUpdate(y9ResourceBase, person, inheritY9Authorization,
                    Boolean.TRUE);

                // 递归处理子资源
                List<Y9ResourceBase> subResourceList =
                    compositeResourceService.listChildrenById(y9ResourceBase.getId());
                for (Y9ResourceBase resource : subResourceList) {
                    this.recalculateByPerson(resourceIdAuthorizationListMap, solvedResourceIdList, resource, person,
                        inheritY9Authorization);
                }
            }
        }
    }

    private void recalculateByPosition(final Map<String, List<Y9Authorization>> resourceIdAuthorizationListMap,
        final List<String> solvedResourceIdList, Y9ResourceBase y9ResourceBase, Y9Position position,
        Y9Authorization inheritY9Authorization) {
        String resourceId = y9ResourceBase.getId();
        if (!solvedResourceIdList.contains(resourceId)) {
            // 对于已经处理过的资源及其子资源直接跳过
            solvedResourceIdList.add(resourceId);

            List<Y9Authorization> resourceRelatedY9AuthorizationList =
                resourceIdAuthorizationListMap.get(y9ResourceBase.getId());
            if (resourceRelatedY9AuthorizationList != null && !resourceRelatedY9AuthorizationList.isEmpty()) {
                // 存在对资源的直接授权 不继承权限

                if (anyHidden(resourceRelatedY9AuthorizationList)) {
                    // 隐藏权限类型优先级最高
                    y9PositionToResourceAndAuthorityManager.deleteByPositionIdAndResourceId(position.getId(),
                        resourceId);
                    return;
                }

                // if (anyPrincipleType(resourceRelatedY9AuthorizationList, AuthorizationPrincipalTypeEnum.POSITION)) {
                // // 对岗直接授权 优先计算
                // y9AuthorizationList =
                // filter(resourceRelatedY9AuthorizationList, AuthorizationPrincipalTypeEnum.POSITION);
                // }

                for (Y9Authorization y9Authorization : resourceRelatedY9AuthorizationList) {
                    y9PositionToResourceAndAuthorityManager.saveOrUpdate(y9ResourceBase, position, y9Authorization,
                        Boolean.FALSE);
                    // 递归处理子资源
                    List<Y9ResourceBase> subResourceList =
                        compositeResourceService.listChildrenById(y9ResourceBase.getId());
                    for (Y9ResourceBase resource : subResourceList) {
                        this.recalculateByPosition(resourceIdAuthorizationListMap, solvedResourceIdList, resource,
                            position, y9Authorization);
                    }
                }

            } else if (Boolean.TRUE.equals(y9ResourceBase.getInherit())
                && !AuthorityEnum.HIDDEN.equals(inheritY9Authorization.getAuthority())) {
                // 不存在对资源的直接授权 继承权限

                y9PositionToResourceAndAuthorityManager.saveOrUpdate(y9ResourceBase, position, inheritY9Authorization,
                    Boolean.TRUE);

                // 递归处理子资源
                List<Y9ResourceBase> subResourceList =
                    compositeResourceService.listChildrenById(y9ResourceBase.getId());
                for (Y9ResourceBase resource : subResourceList) {
                    this.recalculateByPosition(resourceIdAuthorizationListMap, solvedResourceIdList, resource, position,
                        inheritY9Authorization);
                }
            }
        }
    }
}
