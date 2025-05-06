package net.risesoft.service.identity.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.identity.person.Y9PersonToRole;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.repository.identity.person.Y9PersonToRoleRepository;
import net.risesoft.service.identity.Y9PersonToRoleService;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.manager.resource.Y9SystemManager;
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

    private final Y9RoleManager y9RoleManager;
    private final Y9PersonManager y9PersonManager;
    private final Y9SystemManager y9SystemManager;

    @Override
    public long countByPersonId(String personId) {
        return y9PersonToRoleRepository.countByPersonId(personId);
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
    public Boolean hasRole(String personId, String systemName, String roleName, String properties) {
        Y9System y9System = y9SystemManager.getByName(systemName);

        List<Y9Role> y9RoleList;
        if (StringUtils.isBlank(properties)) {
            y9RoleList = y9RoleRepository.findByNameAndSystemIdAndType(roleName, y9System.getId(), RoleTypeEnum.ROLE);
        } else {
            y9RoleList = y9RoleRepository.findByNameAndSystemIdAndPropertiesAndType(roleName, y9System.getId(),
                properties, RoleTypeEnum.ROLE);
        }

        return y9RoleList.stream().anyMatch(y9Role -> hasRole(personId, y9Role.getId()));
    }

    @Override
    public boolean hasRole(String personId, String roleId) {
        return y9PersonToRoleRepository.countByPersonIdAndRoleId(personId, roleId) > 0;
    }

    @Override
    public Boolean hasRoleByCustomId(String personId, String customId) {
        List<Y9Role> y9RoleList = y9RoleRepository.findByCustomId(customId);
        return y9RoleList.stream().anyMatch(y9Role -> hasRole(personId, y9Role.getId()));
    }

    @Override
    public List<Y9PersonToRole> listByPersonId(String personId) {
        return y9PersonToRoleRepository.findByPersonId(personId);
    }

    @Override
    public List<Y9Person> listPersonsByRoleId(String roleId, Boolean disabled) {
        List<String> personIdList = y9PersonToRoleRepository.findPersonIdByRoleId(roleId);
        return personIdList.stream().map(y9PersonManager::getById).filter(p -> {
            if (disabled == null) {
                return true;
            } else {
                return disabled.equals(p.getDisabled());
            }
        }).collect(Collectors.toList());
    }

    @Override
    public List<Y9Role> listRolesByPersonId(String personId) {
        List<String> roleIdList = y9PersonToRoleRepository.findRoleIdByPersonId(personId);
        return roleIdList.stream().map(y9RoleManager::getById).collect(Collectors.toList());
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

    @EventListener
    @Transactional(readOnly = false)
    public void onPersonDeleted(Y9EntityDeletedEvent<Y9Person> event) {
        Y9Person person = event.getEntity();
        y9PersonToRoleRepository.deleteByPersonId(person.getId());
    }
}
