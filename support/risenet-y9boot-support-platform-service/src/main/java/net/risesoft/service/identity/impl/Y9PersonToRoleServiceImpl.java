package net.risesoft.service.identity.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.identity.person.Y9PersonToRole;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.manager.authorization.Y9PersonToRoleManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.repository.identity.person.Y9PersonToRoleRepository;
import net.risesoft.service.identity.Y9PersonToRoleService;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.manager.role.Y9RoleManager;
import net.risesoft.y9public.repository.role.Y9RoleRepository;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class Y9PersonToRoleServiceImpl implements Y9PersonToRoleService {

    private final Y9PersonToRoleRepository y9PersonToRoleRepository;
    private final Y9RoleRepository y9RoleRepository;

    private final Y9PersonToRoleManager y9PersonToRoleManager;
    private final Y9RoleManager y9RoleManager;
    private final Y9PersonManager y9PersonManager;

    @Override
    public long countByPersonId(String personId) {
        return y9PersonToRoleRepository.countByPersonId(personId);
    }

    @Override
    public long countByPersonIdAndSystemName(String personId, String systemName) {
        return y9PersonToRoleRepository.countByPersonIdAndSystemName(personId, systemName);
    }

    @Override
    public String getRoleIdsByPersonId(String personId) {
        List<String> roleIdList = y9PersonToRoleRepository.findRoleIdByPersonId(personId);
        return StringUtils.join(roleIdList, ",");
    }

    @Override
    public boolean hasPublicRole(String personId, String roleName) {
        List<Y9Role> y9RoleList = y9RoleRepository.findByParentIdAndName(InitDataConsts.TOP_PUBLIC_ROLE_ID, roleName);
        return y9RoleList.stream().anyMatch(y9Role -> hasRole(personId, y9Role.getId()));
    }

    @Override
    public boolean hasRole(String personId, String roleId) {
        return y9PersonToRoleRepository.countByPersonIdAndRoleId(personId, roleId) > 0;
    }

    @Override
    public Boolean hasRole(String personId, String systemName, String roleName, String properties) {
        List<Y9Role> y9RoleList;
        if (StringUtils.isBlank(properties)) {
            y9RoleList = y9RoleRepository.findByNameAndSystemNameAndType(roleName, systemName, RoleTypeEnum.ROLE);
        } else {
            y9RoleList = y9RoleRepository.findByNameAndSystemNameAndPropertiesAndType(roleName, systemName, properties,
                RoleTypeEnum.ROLE);
        }

        return y9RoleList.stream().anyMatch(y9Role -> hasRole(personId, y9Role.getId()));
    }

    @Override
    public Boolean hasRoleByCustomId(String personId, String customId) {
        int count = y9PersonToRoleRepository.countByPersonIdAndRoleCustomId(personId, customId);
        return count > 0;
    }

    @Override
    public List<Y9PersonToRole> listByPersonId(String personId) {
        return y9PersonToRoleRepository.findByPersonId(personId);
    }

    @Override
    public List<Y9Person> listPersonsByRoleId(String roleId) {
        List<String> personIdList = y9PersonToRoleRepository.findPersonIdByRoleId(roleId);
        return personIdList.stream().map(y9PersonManager::getById).collect(Collectors.toList());
    }

    @Override
    public List<Y9Role> listRolesByPersonId(String personId) {
        List<String> roleIdList = y9PersonToRoleRepository.findRoleIdByPersonId(personId);
        return roleIdList.stream().map(y9RoleManager::getById).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = false)
    public void recalculate(String personId) {
        Y9Person y9Person = y9PersonManager.getById(personId);
        List<Y9Role> personRelatedY9RoleList = y9RoleManager.listOrgUnitRelatedWithoutNegative(y9Person.getId());
        y9PersonToRoleManager.update(y9Person, personRelatedY9RoleList);
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
            Optional<Y9PersonToRole> y9PersonToRoleOptional = y9PersonToRoleRepository.findById(id);
            if (y9PersonToRoleOptional.isPresent()) {
                Y9PersonToRole y9PersonToRole = y9PersonToRoleOptional.get();
                y9PersonToRole.setRoleName(roleName);
                y9PersonToRole.setSystemCnName(systemCnName);
                y9PersonToRole.setSystemName(systemName);
                y9PersonToRole.setDescription(description);
                y9PersonToRoleRepository.save(y9PersonToRole);
            }
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
}
