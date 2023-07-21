package net.risesoft.service.relation.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.relation.Y9OrgBasesToRoles;
import net.risesoft.repository.Y9DepartmentRepository;
import net.risesoft.repository.Y9GroupRepository;
import net.risesoft.repository.Y9OrganizationRepository;
import net.risesoft.repository.Y9PersonRepository;
import net.risesoft.repository.Y9PositionRepository;
import net.risesoft.repository.relation.Y9OrgBasesToRolesRepository;
import net.risesoft.service.relation.Y9OrgBasesToRolesService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;

/**
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@CacheConfig(cacheNames = CacheNameConsts.ORGBASES_TO_ROLES)
@Service
@RequiredArgsConstructor
public class Y9OrgBasesToRolesServiceImpl implements Y9OrgBasesToRolesService {

    private final Y9DepartmentRepository departmentRepository;
    private final Y9GroupRepository groupRepository;
    private final Y9OrgBasesToRolesRepository y9OrgBasesToRolesRepository;
    private final Y9OrganizationRepository organizationRepository;
    private final Y9PersonRepository personRepository;
    private final Y9PositionRepository positionRepository;

    @Override
    @Transactional(readOnly = false)
    public List<Y9OrgBasesToRoles> addOrgBases(String roleId, String[] orgIds, Boolean negative) {
        List<Y9OrgBasesToRoles> mappingList = new ArrayList<>();
        for (String orgId : orgIds) {
            if (y9OrgBasesToRolesRepository.findByRoleIdAndOrgIdAndNegative(roleId, orgId, negative) != null) {
                continue;
            }
            mappingList.add(saveOrUpdate(roleId, orgId, negative));
        }
        return mappingList;
    }

    @Override
    public long countByRoleIdAndOrgIds(String roleId, List<String> orgIds) {
        return y9OrgBasesToRolesRepository.countByRoleIdAndOrgIdIn(roleId, orgIds);
    }

    @Override
    public long countByRoleIdAndOrgIdsWithoutNegative(String roleId, List<String> orgIds) {
        Set<String> orgIdset = new HashSet<>();
        orgIdset.addAll(orgIds);
        long count = y9OrgBasesToRolesRepository.countByRoleIdAndOrgIdInAndNegative(roleId, orgIdset, Boolean.TRUE);
        if (count > 0) {
            return 0;
        }
        return y9OrgBasesToRolesRepository.countByRoleIdAndOrgIdInAndNegative(roleId, orgIdset, Boolean.FALSE);
    }

    @Override
    public Y9OrgBasesToRoles getById(Integer id) {
        return y9OrgBasesToRolesRepository.findById(id).orElse(null);
    }

    private Y9OrgBase getOrgBaseById(String id) {
        Y9OrgBase y9OrgBase = organizationRepository.findById(id).orElse(null);
        if (y9OrgBase != null) {
            return y9OrgBase;
        }

        y9OrgBase = departmentRepository.findById(id).orElse(null);
        if (y9OrgBase != null) {
            return y9OrgBase;
        }

        y9OrgBase = personRepository.findById(id).orElse(null);
        if (y9OrgBase != null) {
            return y9OrgBase;
        }

        y9OrgBase = positionRepository.findById(id).orElse(null);
        if (y9OrgBase != null) {
            return y9OrgBase;
        }

        y9OrgBase = groupRepository.findById(id).orElse(null);
        if (y9OrgBase != null) {
            return y9OrgBase;
        }

        return y9OrgBase;
    }

    @Override
    public List<Y9OrgBasesToRoles> listByRoleId(String roleId) {
        return y9OrgBasesToRolesRepository.findByRoleId(roleId, Sort.by(Sort.Direction.DESC, "orgOrder"));
    }

    @Override
    public List<Y9OrgBasesToRoles> listByRoleIdAndNegative(String roleId, Boolean negative) {
        return y9OrgBasesToRolesRepository.findByRoleIdAndNegativeOrderByOrgOrderDesc(roleId, negative);
    }

    @Override
    public List<String> listDistinctRoleIdByOrgId(String orgId) {
        return y9OrgBasesToRolesRepository.findDistinctRoleIdByOrgId(orgId);
    }

    @Override
    public List<String> listOrgIdsByRoleId(String roleId) {
        List<Y9OrgBasesToRoles> lists = y9OrgBasesToRolesRepository.findByRoleId(roleId, Sort.by(Sort.Direction.DESC, "orgOrder"));
        return lists.stream().map(Y9OrgBasesToRoles::getOrgId).collect(Collectors.toList());
    }

    @Override
    public List<String> listRoleIdByParentId(String parentId) {
        return y9OrgBasesToRolesRepository.findDistinctRoleIdByParentId(parentId);
    }

    @Override
    public List<String> listRoleIdsByOrgIdAndNegative(String orgId, Boolean negative) {
        List<Y9OrgBasesToRoles> roleNodeMappings = y9OrgBasesToRolesRepository.findByOrgIdAndNegativeOrderByOrgOrderDesc(orgId, negative);
        return roleNodeMappings.stream().map(Y9OrgBasesToRoles::getRoleId).collect(Collectors.toList());
    }

    @Transactional(readOnly = false)
    @Override
    public void remove(Integer id) {
        remove(getById(id));
    }

    @Transactional(readOnly = false)
    @Override
    public void remove(Integer[] ids) {
        for (Integer id : ids) {
            this.remove(id);
        }
    }

    @Transactional(readOnly = false)
    @CacheEvict(key = "#orgId")
    public void remove(String roleId, String orgId) {
        List<Y9OrgBasesToRoles> y9OrgBasesToRolesList = y9OrgBasesToRolesRepository.findByRoleIdAndOrgId(roleId, orgId);
        for (Y9OrgBasesToRoles y9OrgBasesToRoles : y9OrgBasesToRolesList) {
            remove(y9OrgBasesToRoles);
        }
    }

    @Transactional(readOnly = false)
    public void remove(Y9OrgBasesToRoles y9OrgBasesToRoles) {
        y9OrgBasesToRolesRepository.delete(y9OrgBasesToRoles);

        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9OrgBasesToRoles));
    }

    @Override
    @Transactional(readOnly = false)
    public void removeOrgBases(String roleId, String[] orgIds) {
        for (String orgId : orgIds) {
            remove(roleId, orgId);
        }
    }

    @Transactional(readOnly = false)
    @Override
    public Y9OrgBasesToRoles save(Y9OrgBasesToRoles y9OrgBasesToRoles) {
        return y9OrgBasesToRolesRepository.save(y9OrgBasesToRoles);
    }

    @Transactional(readOnly = false)
    @CacheEvict(key = "#orgId")
    public Y9OrgBasesToRoles saveOrUpdate(String roleId, String orgId, Boolean negative) {
        Y9OrgBasesToRoles oldOrgBasesToRoles = y9OrgBasesToRolesRepository.findByRoleIdAndOrgIdAndNegative(roleId, orgId, negative);
        if (oldOrgBasesToRoles == null) {
            Integer maxOrgUnitsOrder = y9OrgBasesToRolesRepository.getMaxOrgOrderByRoleId(roleId);
            Y9OrgBase orgBase = getOrgBaseById(orgId);
            Integer nextOrgUnitsOrder = maxOrgUnitsOrder == null ? 0 : maxOrgUnitsOrder + 1;
            Y9OrgBasesToRoles y9OrgBasesToRoles = new Y9OrgBasesToRoles();
            y9OrgBasesToRoles.setRoleId(roleId);
            y9OrgBasesToRoles.setOrgId(orgId);
            y9OrgBasesToRoles.setOrgOrder(nextOrgUnitsOrder);
            y9OrgBasesToRoles.setNegative(negative);
            y9OrgBasesToRoles.setParentId(orgBase.getParentId());
            y9OrgBasesToRoles.setOrgType(orgBase.getOrgType());

            Y9OrgBasesToRoles savedOrgBasesToRoles = y9OrgBasesToRolesRepository.save(y9OrgBasesToRoles);

            Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedOrgBasesToRoles));

            return savedOrgBasesToRoles;
        }
        return oldOrgBasesToRoles;
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9OrgBasesToRoles> saveRoles(String orgId, String[] roleIds, Boolean negative) {
        List<Y9OrgBasesToRoles> mappingList = new ArrayList<>();
        for (String roleId : roleIds) {
            if (y9OrgBasesToRolesRepository.findByRoleIdAndOrgIdAndNegative(roleId, orgId, negative) != null) {
                continue;
            }
            mappingList.add(saveOrUpdate(roleId, orgId, negative));
        }
        return mappingList;
    }
}
