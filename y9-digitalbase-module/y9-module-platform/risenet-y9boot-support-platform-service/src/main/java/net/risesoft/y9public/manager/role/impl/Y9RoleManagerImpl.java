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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.consts.DefaultConsts;
import net.risesoft.consts.InitDataConsts;
import net.risesoft.consts.RoleLevelConsts;
import net.risesoft.entity.org.Y9Group;
import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.exception.RoleErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.repository.permission.Y9OrgBasesToRolesRepository;
import net.risesoft.repository.relation.Y9PersonsToGroupsRepository;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.manager.resource.Y9AppManager;
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
@RequiredArgsConstructor
@Slf4j
public class Y9RoleManagerImpl implements Y9RoleManager {

    private final Y9RoleRepository y9RoleRepository;
    private final Y9OrgBasesToRolesRepository y9OrgBasesToRolesRepository;
    private final Y9PersonsToGroupsRepository y9PersonsToGroupsRepository;
    private final Y9PersonsToPositionsRepository y9PersonsToPositionsRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9AppManager y9AppManager;

    @CacheEvict(key = "#id")
    @Override
    public void delete(String id) {
        Y9Role y9Role = this.getById(id);
        y9RoleRepository.delete(y9Role);
    }

    @Override
    public Optional<Y9Role> findById(String id) {
        return y9RoleRepository.findById(id);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Optional<Y9Role> findByIdFromCache(String id) {
        return y9RoleRepository.findById(id);
    }

    @Override
    public Y9Role getById(String id) {
        return y9RoleRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(RoleErrorCodeEnum.ROLE_NOT_FOUND, id));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Role getByIdFromCache(String id) {
        return this.getById(id);
    }

    @Override
    public List<String> listOrgUnitIdRecursively(String orgUnitId) {
        List<String> orgUnitIdList = new ArrayList<>();
        this.getOrgUnitIdsByUpwardRecursion(orgUnitIdList, orgUnitId);
        return orgUnitIdList.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<Y9Role> listOrgUnitRelatedWithoutNegative(String orgUnitId) {
        Set<String> positiveRoleIdSet = new HashSet<>();
        Set<String> negativeRoleIdSet = new HashSet<>();
        Set<String> calculatedRoleIdSet = new HashSet<>();

        List<String> orgUnitIds = this.listOrgUnitIdRecursively(orgUnitId);
        for (String id : orgUnitIds) {
            positiveRoleIdSet.addAll(y9OrgBasesToRolesRepository.findRoleIdsByOrgIdAndNegative(id, Boolean.FALSE));
            negativeRoleIdSet.addAll(y9OrgBasesToRolesRepository.findRoleIdsByOrgIdAndNegative(id, Boolean.TRUE));
        }
        for (String roleId : positiveRoleIdSet) {
            if (!negativeRoleIdSet.contains(roleId)) {
                // 负角色关联列表中没包含当前正角色关联，说明当前组织节点拥有该角色
                calculatedRoleIdSet.add(roleId);
            }
        }

        return calculatedRoleIdSet.stream().map(this::getByIdFromCache).collect(Collectors.toList());
    }

    @Override
    public Y9Role insert(Y9Role y9Role) {
        Y9Role parent = null;
        if (StringUtils.isNotEmpty(y9Role.getParentId())) {
            parent = this.findById(y9Role.getParentId()).orElse(null);
        }

        if (StringUtils.isBlank(y9Role.getId())) {
            y9Role.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }

        y9Role.setTabIndex(
            DefaultConsts.TAB_INDEX.equals(y9Role.getTabIndex()) ? getNextTabIndex() : y9Role.getTabIndex());
        if (parent != null) {
            y9Role.setParentId(parent.getId());
            y9Role.setDn(RoleLevelConsts.CN + y9Role.getName() + RoleLevelConsts.SEPARATOR + parent.getDn());
            y9Role.setGuidPath(parent.getGuidPath() + RoleLevelConsts.SEPARATOR + y9Role.getId());
        } else {
            y9Role.setParentId(y9Role.getParentId());
            y9Role.setDn(RoleLevelConsts.CN + y9Role.getName());
            y9Role.setGuidPath(y9Role.getId());
        }

        if (StringUtils.isNotBlank(y9Role.getAppId())) {
            // 应用角色
            Y9App y9App = y9AppManager.getByIdFromCache(y9Role.getAppId());
            y9Role.setSystemId(y9App.getSystemId());
        } else if (StringUtils.isNotBlank(y9Role.getSystemId())) {
            // 系统角色
            y9Role.setAppId(null);
        } else {
            // 公共角色
            y9Role.setAppId(null);
            y9Role.setSystemId(null);
        }

        if (!InitDataConsts.TOP_PUBLIC_ROLE_ID.equals(y9Role.getParentId())) {
            y9Role.setTenantId(Y9LoginUserHolder.getTenantId());
        }
        if (InitDataConsts.OPERATION_TENANT_ID.equals(Y9LoginUserHolder.getTenantId())) {
            y9Role.setTenantId(null);
        }
        return y9RoleRepository.save(y9Role);
    }

    @Override
    @CacheEvict(key = "#y9Role.id", condition = "#y9Role.id!=null")
    public Y9Role update(Y9Role y9Role) {
        Y9Role parent = null;
        if (StringUtils.isNotEmpty(y9Role.getParentId())) {
            parent = this.findById(y9Role.getParentId()).orElse(null);
        }

        Y9Role currentRole = this.getById(y9Role.getId());
        Y9BeanUtil.copyProperties(y9Role, currentRole);

        if (parent != null) {
            currentRole.setParentId(parent.getId());
            currentRole.setDn(RoleLevelConsts.CN + y9Role.getName() + RoleLevelConsts.SEPARATOR + parent.getDn());
            currentRole.setGuidPath(parent.getGuidPath() + RoleLevelConsts.SEPARATOR + y9Role.getId());
        } else {
            currentRole.setParentId(y9Role.getParentId());
            currentRole.setDn(RoleLevelConsts.CN + y9Role.getName());
            currentRole.setGuidPath(y9Role.getId());
        }

        if (StringUtils.isNotBlank(y9Role.getAppId())) {
            // 应用角色
            Y9App y9App = y9AppManager.getByIdFromCache(y9Role.getAppId());
            y9Role.setSystemId(y9App.getSystemId());
        } else if (StringUtils.isNotBlank(y9Role.getSystemId())) {
            // 系统角色
            y9Role.setAppId(null);
        } else {
            // 公共角色
            y9Role.setAppId(null);
            y9Role.setSystemId(null);
        }

        return y9RoleRepository.save(currentRole);
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

    private Integer getNextTabIndex() {
        return y9RoleRepository.findTopByOrderByTabIndexDesc().map(Y9Role::getTabIndex).orElse(-1) + 1;
    }
}
