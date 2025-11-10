package net.risesoft.service.permission.cache.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.entity.permission.cache.position.Y9PositionToRole;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.manager.org.Y9PositionManager;
import net.risesoft.model.platform.Role;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.platform.permission.cache.PositionToRole;
import net.risesoft.repository.permission.cache.position.Y9PositionToRoleRepository;
import net.risesoft.service.permission.cache.Y9PositionToRoleService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.util.Y9PlatformUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9public.entity.Y9Role;
import net.risesoft.y9public.entity.Y9System;
import net.risesoft.y9public.manager.resource.Y9SystemManager;
import net.risesoft.y9public.manager.role.Y9RoleManager;
import net.risesoft.y9public.repository.Y9RoleRepository;

/**
 * PositionToRoleServiceImpl
 *
 * @author shidaobang
 * @date 2022/4/6
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class Y9PositionToRoleServiceImpl implements Y9PositionToRoleService {

    private final Y9PositionToRoleRepository y9PositionToRoleRepository;
    private final Y9RoleRepository y9RoleRepository;

    private final Y9SystemManager y9SystemManager;
    private final Y9PositionManager y9PositionManager;
    private final Y9RoleManager y9RoleManager;

    @Override
    public boolean hasPublicRole(String positionId, String roleName) {
        List<Y9Role> y9RoleList = y9RoleRepository.findByParentIdAndName(InitDataConsts.TOP_PUBLIC_ROLE_ID, roleName);
        return y9RoleList.stream().anyMatch(y9Role -> hasRole(positionId, y9Role.getId()));
    }

    @Override
    public boolean hasRole(String positionId, String roleId) {
        return y9PositionToRoleRepository.countByPositionIdAndRoleId(positionId, roleId) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(String positionId, String systemName, String roleName, String properties) {
        Y9System y9System = y9SystemManager.getByName(systemName);

        List<Y9Role> y9RoleList;
        if (StringUtils.isBlank(properties)) {
            y9RoleList = y9RoleRepository.findByNameAndSystemIdAndType(roleName, y9System.getId(), RoleTypeEnum.ROLE);
        } else {
            y9RoleList = y9RoleRepository.findByNameAndSystemIdAndPropertiesAndType(roleName, y9System.getId(),
                properties, RoleTypeEnum.ROLE);
        }

        return y9RoleList.stream().anyMatch(y9Role -> hasRole(positionId, y9Role.getId()));
    }

    @Override
    public boolean hasRoleByCustomId(String positionId, String customId) {
        List<Y9Role> y9RoleList = y9RoleRepository.findByCustomId(customId);
        return y9RoleList.stream().anyMatch(y9Role -> hasRole(positionId, y9Role.getId()));
    }

    @Override
    public List<PositionToRole> listByPositionId(String positionId) {
        return entityToModel(y9PositionToRoleRepository.findByPositionId(positionId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Position> listPositionsByRoleId(String roleId, Boolean disabled) {
        List<String> positionIdList = y9PositionToRoleRepository.findPositionIdByRoleId(roleId);
        return positionIdList.stream().map(y9PositionManager::getByIdFromCache).filter(p -> {
            if (disabled == null) {
                return true;
            } else {
                return disabled.equals(p.getDisabled());
            }
        }).map(PlatformModelConvertUtil::y9PositionToPosition).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> listRolesByPositionId(String positionId) {
        List<String> roleIdList = y9PositionToRoleRepository.findRoleIdByPositionId(positionId);
        return roleIdList.stream()
            .map(y9RoleManager::getByIdFromCache)
            .map(PlatformModelConvertUtil::y9RoleToRole)
            .collect(Collectors.toList());
    }

    @EventListener
    @Transactional
    public void onPositionDeleted(Y9EntityDeletedEvent<Y9Position> event) {
        Y9Position position = event.getEntity();
        y9PositionToRoleRepository.deleteByPositionId(position.getId());
    }

    @TransactionalEventListener
    public void onRoleDeleted(Y9EntityDeletedEvent<Y9Role> event) {
        Y9Role entity = event.getEntity();
        for (String tenantId : Y9PlatformUtil.getTenantIds()) {
            deleteByRole(tenantId, entity);
        }
    }

    @Async
    protected void deleteByRole(String tenantId, Y9Role entity) {
        Y9LoginUserHolder.setTenantId(tenantId);
        y9PositionToRoleRepository.deleteByRoleId(entity.getId());
        LOGGER.debug("角色[{}]删除时同步删除租户[{}]的岗位角色缓存数据", entity.getId(), tenantId);
    }

    private List<PositionToRole> entityToModel(List<Y9PositionToRole> y9PositionToRoleList) {
        return PlatformModelConvertUtil.convert(y9PositionToRoleList, PositionToRole.class);
    }
}
