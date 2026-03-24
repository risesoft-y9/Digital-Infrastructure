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
import net.risesoft.entity.org.Y9Group;
import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.exception.RoleErrorCodeEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.repository.permission.Y9OrgBasesToRolesRepository;
import net.risesoft.repository.relation.Y9PersonsToGroupsRepository;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9public.entity.Y9Role;
import net.risesoft.y9public.manager.role.Y9RoleManager;
import net.risesoft.y9public.repository.Y9RoleRepository;

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
        return y9RoleRepository.save(y9Role);
    }

    @Override
    @CacheEvict(key = "#y9Role.id", condition = "#y9Role.id!=null")
    public Y9Role update(Y9Role y9Role) {
        return y9RoleRepository.save(y9Role);
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
