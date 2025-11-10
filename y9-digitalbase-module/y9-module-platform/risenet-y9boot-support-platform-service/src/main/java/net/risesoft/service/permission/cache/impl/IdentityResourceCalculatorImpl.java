package net.risesoft.service.permission.cache.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.permission.AuthorizationPrincipalTypeEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.manager.permission.cache.Y9PersonToResourceAndAuthorityManager;
import net.risesoft.manager.permission.cache.Y9PositionToResourceAndAuthorityManager;
import net.risesoft.repository.permission.Y9AuthorizationRepository;
import net.risesoft.repository.permission.Y9OrgBasesToRolesRepository;
import net.risesoft.service.permission.cache.IdentityResourceCalculator;
import net.risesoft.y9public.entity.Y9Role;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.manager.resource.CompositeResourceManager;
import net.risesoft.y9public.manager.role.Y9RoleManager;

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
    private final CompositeResourceManager compositeResourceManager;

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
     * 拿到人员或岗位所有关联的组织和角色id
     *
     * @param identityId
     * @return
     */
    private List<String> getIdentityRelatedPrincipalIdList(String identityId) {
        List<String> principalIdList = new ArrayList<>();

        List<Y9Role> y9RoleList = y9RoleManager.listOrgUnitRelatedWithoutNegative(identityId);
        principalIdList.addAll(y9RoleList.stream().map(Y9Role::getId).collect(Collectors.toList()));

        List<String> orgUnitIdList = y9RoleManager.listOrgUnitIdRecursively(identityId);
        principalIdList.addAll(orgUnitIdList);

        return principalIdList.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 根据资源id找到与其相关的授权（继承的）
     *
     * @param resourceId 资源id
     * @return {@code List<}{@link Y9Authorization}{@code >}
     */
    @Transactional(readOnly = true)
    public List<Y9Authorization> listByResourceIdRelated(String resourceId) {
        List<Y9Authorization> authorizationList = new ArrayList<>();
        listByResourceIdRelated(authorizationList, resourceId);
        return authorizationList;
    }

    private void listByResourceIdRelated(List<Y9Authorization> authorizationList, String resourceId) {
        if (StringUtils.isNotBlank(resourceId)) {
            authorizationList.addAll(y9AuthorizationRepository.findByResourceId(resourceId));
            listByResourceIdRelated(authorizationList, compositeResourceManager.getById(resourceId).getParentId());
        }
    }

    @Override
    @Transactional
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
                List<Y9Person> y9PersonList = y9PersonManager.listByPositionId(y9OrgBase.getId(), Boolean.FALSE);
                for (Y9Person y9Person : y9PersonList) {
                    this.recalculateByPerson(y9Person);
                }
                return;
            }

            List<Y9Person> personList = compositeOrgBaseManager.listAllDescendantPersons(orgUnitId);
            for (Y9Person person : personList) {
                this.recalculateByPerson(person);
            }
            List<Y9Position> positionList = compositeOrgBaseManager.listAllDescendantPositions(orgUnitId);
            for (Y9Position position : positionList) {
                this.recalculateByPosition(position);
            }
        }
    }

    @Override
    @Transactional
    public void recalculateByPerson(Y9Person person) {
        try {
            List<String> principalIdList = getIdentityRelatedPrincipalIdList(person.getId());
            List<Y9Authorization> principalRelatedY9AuthorizationList =
                y9AuthorizationRepository.getByPrincipalIdIn(principalIdList);

            // 清除已去掉的权限
            List<String> authorizationIdList =
                principalRelatedY9AuthorizationList.stream().map(Y9Authorization::getId).collect(Collectors.toList());
            y9PersonToResourceAndAuthorityManager.deleteByPersonIdAndAuthorizationIdNotIn(person.getId(),
                authorizationIdList);

            Map<String, List<Y9Authorization>> resourceIdAuthorizationListMap =
                principalRelatedY9AuthorizationList.stream()
                    .collect(Collectors.groupingBy(Y9Authorization::getResourceId));

            for (String resourceId : resourceIdAuthorizationListMap.keySet()) {
                Y9ResourceBase y9ResourceBase = compositeResourceManager.getById(resourceId);
                this.recalculateByPerson(resourceIdAuthorizationListMap, y9ResourceBase, person);
            }
        } catch (Exception e) {
            LOGGER.warn("计算人员[{}]权限发生异常", person.getId(), e);
        }
    }

    @Override
    @Transactional
    public void recalculateByPosition(Y9Position position) {
        try {
            List<String> principalIdList = getIdentityRelatedPrincipalIdList(position.getId());
            List<Y9Authorization> principalRelatedY9AuthorizationList =
                y9AuthorizationRepository.getByPrincipalIdIn(principalIdList);

            // 清除已去掉的权限
            List<String> authorizationIdList =
                principalRelatedY9AuthorizationList.stream().map(Y9Authorization::getId).collect(Collectors.toList());
            y9PositionToResourceAndAuthorityManager.deleteByPositionIdAndAuthorizationIdNotIn(position.getId(),
                authorizationIdList);

            Map<String, List<Y9Authorization>> resourceIdAuthorizationListMap =
                principalRelatedY9AuthorizationList.stream()
                    .collect(Collectors.groupingBy(Y9Authorization::getResourceId));

            for (String resourceId : resourceIdAuthorizationListMap.keySet()) {
                Y9ResourceBase y9ResourceBase = compositeResourceManager.getById(resourceId);
                this.recalculateByPosition(resourceIdAuthorizationListMap, y9ResourceBase, position);
            }
        } catch (Exception e) {
            LOGGER.warn("计算岗位[{}]权限发生异常", position.getId(), e);
        }
    }

    @Override
    @Transactional
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

    @Transactional
    public void recalculateByPerson(Map<String, List<Y9Authorization>> resourceIdAuthorizationListMap,
        Y9ResourceBase y9ResourceBase, Y9Person person) {
        String resourceId = y9ResourceBase.getId();

        List<Y9Authorization> resourceRelatedY9AuthorizationList =
            resourceIdAuthorizationListMap.getOrDefault(resourceId, new ArrayList<>());
        List<Y9Authorization> inheritY9AuthorizationList =
            getInheritY9AuthorizationList(y9ResourceBase.getId(), resourceIdAuthorizationListMap);

        boolean isInheritAuthorizations = Boolean.TRUE.equals(y9ResourceBase.getInherit());
        if ((isInheritAuthorizations
            && (anyHidden(resourceRelatedY9AuthorizationList) || anyHidden(inheritY9AuthorizationList)))
            || anyHidden(resourceRelatedY9AuthorizationList)) {
            // 隐藏权限类型优先级最高 无论是否继承 只要有一个隐藏权限对应的资源都应无权限
            y9PersonToResourceAndAuthorityManager.deleteByPersonIdAndResourceId(person.getId(), resourceId);
            return;
        }

        for (Y9Authorization y9Authorization : resourceRelatedY9AuthorizationList) {
            y9PersonToResourceAndAuthorityManager.saveOrUpdate(y9ResourceBase, person, y9Authorization, Boolean.FALSE);
        }

        for (Y9Authorization inheritY9Authorization : inheritY9AuthorizationList) {
            y9PersonToResourceAndAuthorityManager.saveOrUpdate(y9ResourceBase, person, inheritY9Authorization,
                Boolean.TRUE);
        }

        // 递归处理子资源
        List<Y9ResourceBase> subResourceList = compositeResourceManager.listByParentId(y9ResourceBase.getId());
        for (Y9ResourceBase resource : subResourceList) {
            this.recalculateByPerson(resourceIdAuthorizationListMap, resource, person);
        }
    }

    private List<Y9Authorization> getInheritY9AuthorizationList(String resourceId,
        Map<String, List<Y9Authorization>> resourceIdAuthorizationListMap) {
        List<Y9Authorization> inheritY9AuthorizationList = new ArrayList<>();

        String currentResourceId = resourceId;
        while (true) {
            Y9ResourceBase y9ResourceBase = compositeResourceManager.getById(currentResourceId);
            String parentResourceId = y9ResourceBase.getParentId();
            if (y9ResourceBase.getInherit() && StringUtils.isNotBlank(parentResourceId)) {
                inheritY9AuthorizationList
                    .addAll(resourceIdAuthorizationListMap.getOrDefault(parentResourceId, new ArrayList<>()));
            } else {
                break;
            }
            currentResourceId = parentResourceId;
        }

        return inheritY9AuthorizationList;
    }

    @Transactional
    public void recalculateByPosition(final Map<String, List<Y9Authorization>> resourceIdAuthorizationListMap,
        Y9ResourceBase y9ResourceBase, Y9Position position) {
        String resourceId = y9ResourceBase.getId();

        List<Y9Authorization> resourceRelatedY9AuthorizationList =
            resourceIdAuthorizationListMap.getOrDefault(resourceId, new ArrayList<>());
        List<Y9Authorization> inheritY9AuthorizationList =
            getInheritY9AuthorizationList(y9ResourceBase.getId(), resourceIdAuthorizationListMap);

        boolean inheritAuthorizations = Boolean.TRUE.equals(y9ResourceBase.getInherit());
        if ((inheritAuthorizations
            && (anyHidden(resourceRelatedY9AuthorizationList) || anyHidden(inheritY9AuthorizationList)))
            || anyHidden(resourceRelatedY9AuthorizationList)) {
            // 隐藏权限类型优先级最高 无论是否继承 只要有一个隐藏权限对应的资源都应无权限
            y9PositionToResourceAndAuthorityManager.deleteByPositionIdAndResourceId(position.getId(), resourceId);
            return;
        }

        for (Y9Authorization y9Authorization : resourceRelatedY9AuthorizationList) {
            y9PositionToResourceAndAuthorityManager.saveOrUpdate(y9ResourceBase, position, y9Authorization,
                Boolean.FALSE);
        }

        for (Y9Authorization inheritY9Authorization : inheritY9AuthorizationList) {
            y9PositionToResourceAndAuthorityManager.saveOrUpdate(y9ResourceBase, position, inheritY9Authorization,
                Boolean.TRUE);
        }

        // 递归处理子资源
        List<Y9ResourceBase> subResourceList = compositeResourceManager.listByParentId(y9ResourceBase.getId());
        for (Y9ResourceBase resource : subResourceList) {
            this.recalculateByPosition(resourceIdAuthorizationListMap, resource, position);
        }
    }
}
