package net.risesoft.service.relation.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9OrgBasesToRoles;
import net.risesoft.exception.RoleErrorCodeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.repository.relation.Y9OrgBasesToRolesRepository;
import net.risesoft.service.relation.Y9OrgBasesToRolesService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class Y9OrgBasesToRolesServiceImpl implements Y9OrgBasesToRolesService {

    private final CompositeOrgBaseManager compositeOrgBaseManager;

    private final Y9OrgBasesToRolesRepository y9OrgBasesToRolesRepository;

    @Override
    @Transactional(readOnly = false)
    public List<Y9OrgBasesToRoles> addOrgUnitsForRole(String roleId, List<String> orgIds, Boolean negative) {
        List<Y9OrgBasesToRoles> mappingList = new ArrayList<>();
        for (String orgId : orgIds) {
            if (y9OrgBasesToRolesRepository.findByRoleIdAndOrgIdAndNegative(roleId, orgId, negative).isPresent()) {
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
    public Y9OrgBasesToRoles getById(String id) {
        return y9OrgBasesToRolesRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(RoleErrorCodeEnum.ORG_UNIT_ROLE_NOT_FOUND, id));
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
        List<Y9OrgBasesToRoles> lists =
            y9OrgBasesToRolesRepository.findByRoleId(roleId, Sort.by(Sort.Direction.DESC, "orgOrder"));
        return lists.stream().map(Y9OrgBasesToRoles::getOrgId).collect(Collectors.toList());
    }

    @Override
    public List<String> listRoleIdByParentId(String parentId) {
        return y9OrgBasesToRolesRepository.findDistinctRoleIdByParentId(parentId);
    }

    @Override
    public List<String> listRoleIdsByOrgIdAndNegative(String orgId, Boolean negative) {
        List<Y9OrgBasesToRoles> roleNodeMappings =
            y9OrgBasesToRolesRepository.findByOrgIdAndNegativeOrderByOrgOrderDesc(orgId, negative);
        return roleNodeMappings.stream().map(Y9OrgBasesToRoles::getRoleId).collect(Collectors.toList());
    }

    @Transactional(readOnly = false)
    @Override
    public void remove(String id) {
        remove(getById(id));
    }

    @Transactional(readOnly = false)
    @Override
    public void remove(List<String> ids) {
        for (String id : ids) {
            this.remove(id);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void removeOrgBases(String roleId, List<String> orgIds) {
        for (String orgId : orgIds) {
            remove(roleId, orgId);
        }
    }

    @Override
    public Page<Y9OrgBasesToRoles> page(Y9PageQuery pageQuery, String roleId, String unitName) {
        if (StringUtils.isNotEmpty(unitName)) {
            List<String> orgUnitIdList = compositeOrgBaseManager.listOrgUnitIdByName(unitName);
            return y9OrgBasesToRolesRepository.findByRoleIdAndOrgIdIn(roleId, orgUnitIdList, PageRequest
                .of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by(Sort.Direction.DESC, "createTime")));
        } else {
            return y9OrgBasesToRolesRepository.findByRoleId(roleId, PageRequest.of(pageQuery.getPage4Db(),
                pageQuery.getSize(), Sort.by(Sort.Direction.DESC, "createTime")));
        }
    }

    @Override
    public List<String> listOrgUnitIdByRoleId(String roleId, Boolean negative) {
        if (negative == null) {
            return y9OrgBasesToRolesRepository.listOrgIdsByRoleId(roleId);
        } else {
            return y9OrgBasesToRolesRepository.listOrgIdsByRoleIdAndNegative(roleId, negative);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9OrgBasesToRoles> addRolesForOrgUnit(String orgId, List<String> roleIds, Boolean negative) {
        List<Y9OrgBasesToRoles> mappingList = new ArrayList<>();
        for (String roleId : roleIds) {
            if (y9OrgBasesToRolesRepository.findByRoleIdAndOrgIdAndNegative(roleId, orgId, negative).isPresent()) {
                continue;
            }
            mappingList.add(saveOrUpdate(roleId, orgId, negative));
        }
        return mappingList;
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onDepartmentDeleted(Y9EntityDeletedEvent<Y9Department> event) {
        Y9Department department = event.getEntity();
        y9OrgBasesToRolesRepository.deleteByOrgId(department.getId());
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onGroupDeleted(Y9EntityDeletedEvent<Y9Group> event) {
        Y9Group group = event.getEntity();
        y9OrgBasesToRolesRepository.deleteByOrgId(group.getId());
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onPersonDeleted(Y9EntityDeletedEvent<Y9Person> event) {
        Y9Person person = event.getEntity();
        y9OrgBasesToRolesRepository.deleteByOrgId(person.getId());
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onPositionDeleted(Y9EntityDeletedEvent<Y9Position> event) {
        Y9Position position = event.getEntity();
        y9OrgBasesToRolesRepository.deleteByOrgId(position.getId());
    }

    @Transactional(readOnly = false)
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

    @Transactional(readOnly = false)
    public Y9OrgBasesToRoles saveOrUpdate(String roleId, String orgId, Boolean negative) {
        Y9OrgBase orgBase = compositeOrgBaseManager.getOrgUnit(orgId);
        checkOrgUnitIncludedInRoleRelatedMapping(roleId, negative, orgBase);

        Optional<Y9OrgBasesToRoles> optionalY9OrgBasesToRoles =
            y9OrgBasesToRolesRepository.findByRoleIdAndOrgIdAndNegative(roleId, orgId, negative);
        if (optionalY9OrgBasesToRoles.isEmpty()) {
            Integer maxOrgUnitsOrder = y9OrgBasesToRolesRepository.getMaxOrgOrderByRoleId(roleId);
            Integer nextOrgUnitsOrder = maxOrgUnitsOrder == null ? 0 : maxOrgUnitsOrder + 1;
            Y9OrgBasesToRoles y9OrgBasesToRoles = new Y9OrgBasesToRoles();
            y9OrgBasesToRoles.setId(Y9IdGenerator.genId());
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
        return optionalY9OrgBasesToRoles.get();
    }

    private void checkOrgUnitIncludedInRoleRelatedMapping(String roleId, Boolean negative, Y9OrgBase orgBase) {
        List<Y9OrgBasesToRoles> y9OrgBasesToRolesList =
            y9OrgBasesToRolesRepository.findByRoleIdAndNegativeOrderByOrgOrderDesc(roleId, negative);
        boolean orgUnitIncluded = y9OrgBasesToRolesList.stream().map(Y9OrgBasesToRoles::getOrgId)
            .anyMatch(orgUnitId -> orgBase.getGuidPath().contains(orgUnitId));
        if (orgUnitIncluded) {
            throw Y9ExceptionUtil.businessException(RoleErrorCodeEnum.ORG_UNIT_INCLUDED, orgBase.getId());
        }
    }
}
