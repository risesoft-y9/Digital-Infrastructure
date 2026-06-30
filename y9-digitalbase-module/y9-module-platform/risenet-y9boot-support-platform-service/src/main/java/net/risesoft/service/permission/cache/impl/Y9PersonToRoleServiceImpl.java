package net.risesoft.service.permission.cache.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.entity.permission.cache.person.Y9PersonToRole;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.model.platform.Role;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.permission.cache.PersonToRole;
import net.risesoft.repository.permission.cache.person.Y9PersonToRoleRepository;
import net.risesoft.service.permission.cache.Y9PersonToRoleService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9public.entity.Y9Role;
import net.risesoft.y9public.entity.Y9System;
import net.risesoft.y9public.manager.resource.Y9SystemManager;
import net.risesoft.y9public.manager.role.Y9RoleManager;
import net.risesoft.y9public.repository.Y9RoleRepository;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
@Slf4j
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
    public boolean hasPublicRole(String personId, String roleName) {
        List<Y9Role> y9RoleList = y9RoleRepository.findByParentIdAndName(InitDataConsts.TOP_PUBLIC_ROLE_ID, roleName);
        return y9RoleList.stream().anyMatch(y9Role -> hasRole(personId, y9Role.getId()));
    }

    @Override
    @Transactional(readOnly = true)
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
    public boolean hasRoleByCustomId(String personId, String customId) {
        List<Y9Role> y9RoleList = y9RoleRepository.findByCustomId(customId);
        return y9RoleList.stream().anyMatch(y9Role -> hasRole(personId, y9Role.getId()));
    }

    @Override
    public List<PersonToRole> listByPersonId(String personId) {
        return entityToModel(y9PersonToRoleRepository.findByPersonId(personId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> listPersonsByRoleId(String roleId, Boolean personDisabled) {
        List<String> personIdList = y9PersonToRoleRepository.findPersonIdByRoleId(roleId);
        return personIdList.stream().map(y9PersonManager::getByIdFromCache).filter(p -> {
            if (personDisabled == null) {
                return true;
            } else {
                return personDisabled.equals(p.getDisabled());
            }
        }).map(PlatformModelConvertUtil::y9PersonToPerson).collect(Collectors.toList());
    }

    @Override
    public List<Role> listRolesByPersonId(String personId) {
        List<String> roleIdList = y9PersonToRoleRepository.findRoleIdByPersonId(personId);
        return roleIdList.stream()
            .map(y9RoleManager::getByIdFromCache)
            .map(PlatformModelConvertUtil::y9RoleToRole)
            .collect(Collectors.toList());
    }

    private List<PersonToRole> entityToModel(List<Y9PersonToRole> y9PersonToRoleList) {
        return PlatformModelConvertUtil.convert(y9PersonToRoleList, PersonToRole.class);
    }

}
