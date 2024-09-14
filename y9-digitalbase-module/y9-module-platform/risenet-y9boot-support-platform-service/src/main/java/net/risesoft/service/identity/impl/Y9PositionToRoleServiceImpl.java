package net.risesoft.service.identity.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.identity.position.Y9PositionToRole;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.repository.identity.position.Y9PositionToRoleRepository;
import net.risesoft.service.identity.Y9PositionToRoleService;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.manager.resource.Y9SystemManager;
import net.risesoft.y9public.repository.role.Y9RoleRepository;

/**
 * PositionToRoleServiceImpl
 *
 * @author shidaobang
 * @date 2022/4/6
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class Y9PositionToRoleServiceImpl implements Y9PositionToRoleService {

    private final Y9PositionToRoleRepository y9PositionToRoleRepository;
    private final Y9RoleRepository y9RoleRepository;

    private final Y9SystemManager y9SystemManager;

    @Override
    public Boolean hasPublicRole(String positionId, String roleName) {
        List<Y9Role> y9RoleList = y9RoleRepository.findByParentIdAndName(InitDataConsts.TOP_PUBLIC_ROLE_ID, roleName);
        return y9RoleList.stream().anyMatch(y9Role -> hasRole(positionId, y9Role.getId()));
    }

    @Override
    public boolean hasRole(String positionId, String roleId) {
        return y9PositionToRoleRepository.countByPositionIdAndRoleId(positionId, roleId) > 0;
    }

    @Override
    public Boolean hasRole(String positionId, String systemName, String roleName, String properties) {
        Y9System y9System = y9SystemManager.getByName(systemName);

        List<Y9Role> y9RoleList;
        if (StringUtils.isBlank(properties)) {
            y9RoleList = y9RoleRepository.findByNameAndSystemIdAndType(roleName, y9System.getId(), RoleTypeEnum.ROLE);
        } else {
            y9RoleList = y9RoleRepository.findByNameAndSystemIdAndPropertiesAndType(roleName, y9System.getId(), properties,
                    RoleTypeEnum.ROLE);
        }

        return y9RoleList.stream().anyMatch(y9Role -> hasRole(positionId, y9Role.getId()));
    }

    @Override
    public Boolean hasRoleByCustomId(String positionId, String customId) {
        List<Y9Role> y9RoleList = y9RoleRepository.findByCustomId(customId);
        return y9RoleList.stream().anyMatch(y9Role -> hasRole(positionId, y9Role.getId()));
    }

    @Override
    public List<Y9PositionToRole> listByPositionId(String positionId) {
        return y9PositionToRoleRepository.findByPositionId(positionId);
    }

    @Override
    @Transactional(readOnly = false)
    public void removeByPositionId(String positionId) {
        List<Y9PositionToRole> y9PositionToRoleList = y9PositionToRoleRepository.findByPositionId(positionId);
        if (!y9PositionToRoleList.isEmpty()) {
            y9PositionToRoleRepository.deleteAll(y9PositionToRoleList);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void removeByRoleId(String roleId) {
        List<Y9PositionToRole> y9PositionToRoleList = y9PositionToRoleRepository.findByRoleId(roleId);
        if (!y9PositionToRoleList.isEmpty()) {
            y9PositionToRoleRepository.deleteAll(y9PositionToRoleList);
        }
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onPositionDeleted(Y9EntityDeletedEvent<Y9Position> event) {
        Y9Position position = event.getEntity();
        y9PositionToRoleRepository.deleteByPositionId(position.getId());
    }

}
