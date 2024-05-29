package net.risesoft.y9public.manager.role.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.enums.platform.AuthorizationPrincipalTypeEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.exception.RoleErrorCodeEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.repository.identity.person.Y9PersonToResourceAndAuthorityRepository;
import net.risesoft.repository.identity.person.Y9PersonToRoleRepository;
import net.risesoft.repository.identity.position.Y9PositionToResourceAndAuthorityRepository;
import net.risesoft.repository.identity.position.Y9PositionToRoleRepository;
import net.risesoft.repository.permission.Y9AuthorizationRepository;
import net.risesoft.repository.relation.Y9OrgBasesToRolesRepository;
import net.risesoft.repository.relation.Y9PersonsToGroupsRepository;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.util.Y9PlatformUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.manager.role.Y9RoleManager;
import net.risesoft.y9public.repository.role.Y9RoleRepository;

/**
 * 角色管理 Y9RoleManager 实现类
 *
 * @author shidaobang
 * @date 2023/06/13
 * @since 9.6.2
 */
@Service
@CacheConfig(cacheNames = CacheNameConsts.ROLE)
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9RoleManagerImpl implements Y9RoleManager {

    private final Y9RoleRepository y9RoleRepository;
    private final Y9OrgBasesToRolesRepository y9OrgBasesToRolesRepository;
    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9PersonsToGroupsRepository y9PersonsToGroupsRepository;
    private final Y9PersonsToPositionsRepository y9PersonsToPositionsRepository;
    private final Y9AuthorizationRepository y9AuthorizationRepository;
    private final Y9PersonToRoleRepository y9PersonToRoleRepository;
    private final Y9PositionToRoleRepository y9PositionToRoleRepository;
    private final Y9PersonToResourceAndAuthorityRepository y9PersonToResourceAndAuthorityRepository;
    private final Y9PositionToResourceAndAuthorityRepository y9PositionToResourceAndAuthorityRepository;

    @CacheEvict(key = "#id")
    @Transactional(readOnly = false)
    @Override
    public void delete(String id) {
        Y9Role y9Role = this.getById(id);
        if (RoleTypeEnum.ROLE.equals(y9Role.getType())) {
            // 删除租户关联数据
            List<String> tenantIds = Y9PlatformUtil.getTenantIds();
            for (String tenantId : tenantIds) {
                Y9LoginUserHolder.setTenantId(tenantId);
                this.deleteTenantRelatedByAppId(id);
            }
        } else if (RoleTypeEnum.FOLDER.equals(y9Role.getType())) {
            List<Y9Role> roleNodeList = y9RoleRepository.findByParentIdOrderByTabIndexAsc(id);
            for (Y9Role role : roleNodeList) {
                delete(role.getId());
            }
        }
        y9RoleRepository.delete(y9Role);
    }

    @Override
    public void deleteByApp(String appId) {
        List<Y9Role> y9Roles = listByAppIdAndParentId(appId, appId);
        for (Y9Role role : y9Roles) {
            this.delete(role.getId());
        }
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Optional<Y9Role> findById(String id) {
        return y9RoleRepository.findById(id);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Role getById(String id) {
        return y9RoleRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(RoleErrorCodeEnum.ROLE_NOT_FOUND, id));
    }

    @Override
    public List<Y9Role> listByAppIdAndParentId(String appId, String parentId) {
        if (StringUtils.isNotBlank(parentId)) {
            return y9RoleRepository.findByAppIdAndParentId(appId, parentId);
        } else {
            return y9RoleRepository.findByAppIdAndParentIdIsNull(appId);
        }
    }

    @Override
    public List<String> listOrgUnitIdRecursively(String orgUnitId) {
        List<String> orgUnitIdList = new ArrayList<>();
        this.getOrgUnitIdsByUpwardRecursion(orgUnitIdList, orgUnitId);
        return orgUnitIdList.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<Y9Role> listOrgUnitRelatedWithoutNegative(String orgUnitId) {
        List<String> positiveRoleIdList = new ArrayList<>();
        List<String> negativeRoleIdList = new ArrayList<>();
        Set<String> calculatedRoleIdList = new HashSet<>();

        List<String> orgUnitIds = this.listOrgUnitIdRecursively(orgUnitId);
        for (String id : orgUnitIds) {
            positiveRoleIdList.addAll(y9OrgBasesToRolesRepository.findRoleIdsByOrgIdAndNegative(id, Boolean.FALSE));
            negativeRoleIdList.addAll(y9OrgBasesToRolesRepository.findRoleIdsByOrgIdAndNegative(id, Boolean.TRUE));
        }
        for (String roleId : positiveRoleIdList) {
            if (negativeRoleIdList.isEmpty() || !negativeRoleIdList.contains(roleId)) {
                // 负角色关联列表中没包含当前正角色关联，说明当前组织节点拥有该角色
                calculatedRoleIdList.add(roleId);
            }
        }

        List<Y9Role> y9RoleList = new ArrayList<>();
        for (String roleId : calculatedRoleIdList) {
            Y9Role roleNode = getById(roleId);
            if (roleNode != null && !y9RoleList.contains(roleNode)) {
                y9RoleList.add(roleNode);
            }
        }
        return y9RoleList;
    }

    @Override
    @CacheEvict(key = "#y9Role.id", condition = "#y9Role.id!=null")
    @Transactional(readOnly = false)
    public Y9Role save(Y9Role y9Role) {
        return y9RoleRepository.save(y9Role);
    }

    /**
     * 删除相关租户数据 <br/>
     * 切换不同的数据源 需开启新事务
     *
     * @param roleId 角色id
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void deleteTenantRelatedByAppId(String roleId) {
        y9OrgBasesToRolesRepository.deleteByRoleId(roleId);

        List<Y9Authorization> authorizationList =
            y9AuthorizationRepository.findByPrincipalIdAndPrincipalType(roleId, AuthorizationPrincipalTypeEnum.ROLE);
        for (Y9Authorization y9Authorization : authorizationList) {
            y9PersonToResourceAndAuthorityRepository.deleteByAuthorizationId(y9Authorization.getId());
            y9PositionToResourceAndAuthorityRepository.deleteByAuthorizationId(y9Authorization.getId());
        }
        y9AuthorizationRepository.deleteAll(authorizationList);

        y9PersonToRoleRepository.deleteByRoleId(roleId);
        y9PositionToRoleRepository.deleteByRoleId(roleId);
    }

    private void getOrgUnitIdsByUpwardRecursion(List<String> orgUnitIds, String orgUnitId) {
        if (StringUtils.isNotBlank(orgUnitId)) {
            Y9OrgBase y9OrgBase = compositeOrgBaseManager.getOrgUnit(orgUnitId);

            orgUnitIds.add(orgUnitId);
            if (OrgTypeEnum.PERSON.equals(y9OrgBase.getOrgType())) {
                getOrgUnitIdsByUpwardRecursion(orgUnitIds, y9OrgBase.getParentId());

                List<String> groupList = y9PersonsToGroupsRepository.listGroupIdsByPersonId(y9OrgBase.getId());
                orgUnitIds.addAll(groupList);
                for (String groupId : groupList) {
                    Y9Group group = (Y9Group)compositeOrgBaseManager.getOrgUnit(groupId);
                    getOrgUnitIdsByUpwardRecursion(orgUnitIds, group.getParentId());
                }

                List<String> positionIds = y9PersonsToPositionsRepository.listPositionIdsByPersonId(y9OrgBase.getId());
                orgUnitIds.addAll(positionIds);
                for (String positionId : positionIds) {
                    Y9Position position = (Y9Position)compositeOrgBaseManager.getOrgUnit(positionId);
                    getOrgUnitIdsByUpwardRecursion(orgUnitIds, position.getParentId());
                }
            } else if (OrgTypeEnum.POSITION.equals(y9OrgBase.getOrgType())) {
                getOrgUnitIdsByUpwardRecursion(orgUnitIds, y9OrgBase.getParentId());
            } else {
                getOrgUnitIdsByUpwardRecursion(orgUnitIds, y9OrgBase.getParentId());
            }
        }
    }
}
