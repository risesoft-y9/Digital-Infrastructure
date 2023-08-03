package net.risesoft.service.identity.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9Person;
import net.risesoft.entity.identity.person.Y9PersonToRole;
import net.risesoft.manager.authorization.Y9PersonToRoleManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.repository.identity.person.Y9PersonToRoleRepository;
import net.risesoft.service.identity.Y9PersonToRoleService;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.manager.role.Y9RoleManager;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@Slf4j
@RequiredArgsConstructor
public class Y9PersonToRoleServiceImpl implements Y9PersonToRoleService {

    private final Y9PersonToRoleRepository y9PersonToRoleRepository;
    private final Y9PersonToRoleManager y9PersonToRoleManager;
    private final Y9RoleManager y9RoleManager;
    private final Y9PersonManager y9PersonManager;

    @Override
    @Transactional(readOnly = false)
    public void recalculate(String personId) {
        Y9Person y9Person = y9PersonManager.getById(personId);
        List<Y9Role> personRelatedY9RoleList = y9RoleManager.listOrgUnitRelatedWithoutNegative(y9Person.getId());
        y9PersonToRoleManager.update(y9Person, personRelatedY9RoleList);
    }

    @Override
    public long countByPersonId(String personId) {
        return y9PersonToRoleRepository.countByPersonId(personId);
    }

    @Override
    public long countByPersonIdAndSystemName(String personId, String systemName) {
        return y9PersonToRoleRepository.countByPersonIdAndSystemName(personId, systemName);
    }

    @Override
    public Boolean hasRole(String personId, String customId) {
        int count = y9PersonToRoleRepository.countByPersonIdAndRoleCustomId(personId, customId);
        return count > 0;
    }

    @Override
    public List<Y9PersonToRole> listByPersonId(String personId) {
        return y9PersonToRoleRepository.findByPersonId(personId);
    }

    @Override
    @Transactional(readOnly = false)
    public void removeByPersonId(String personId) {
        List<Y9PersonToRole> y9PersonToRoleList = y9PersonToRoleRepository.findByPersonId(personId);
        y9PersonToRoleRepository.deleteAll(y9PersonToRoleList);
    }

    @Override
    @Transactional(readOnly = false)
    public void removeByRoleId(String roleId) {
        List<Y9PersonToRole> mappingList = y9PersonToRoleRepository.findByRoleId(roleId);
        y9PersonToRoleRepository.deleteAll(mappingList);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(String roleId, String roleName, String systemName, String systemCnName, String description) {
        List<Y9PersonToRole> list = y9PersonToRoleRepository.findByRoleId(roleId);
        List<Integer> ids = list.stream().map(Y9PersonToRole::getId).collect(Collectors.toList());
        for (Integer id : ids) {
            Y9PersonToRole personRoleMapping = y9PersonToRoleRepository.findById(id).orElse(null);
            personRoleMapping.setRoleName(roleName);
            personRoleMapping.setSystemCnName(systemCnName);
            personRoleMapping.setSystemName(systemName);
            personRoleMapping.setDescription(description);
            y9PersonToRoleRepository.save(personRoleMapping);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void updateByRole(Y9Role y9Role) {
        List<Y9PersonToRole> mappingList = y9PersonToRoleRepository.findByRoleId(y9Role.getId());
        for (Y9PersonToRole positionToRole : mappingList) {
            positionToRole.setRoleName(y9Role.getName());
            positionToRole.setSystemName(y9Role.getSystemName());
            positionToRole.setSystemCnName(y9Role.getSystemCnName());
            positionToRole.setDescription(y9Role.getDescription());
            y9PersonToRoleRepository.save(positionToRole);
        }
    }

    @Override
    public String getRoleIdsByPersonId(String personId) {
        List<String> roleIdList = y9PersonToRoleRepository.findRoleIdByPersonId(personId);
        return StringUtils.join(roleIdList, ",");
    }
}
