package net.risesoft.service.identity.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Position;
import net.risesoft.entity.identity.position.Y9PositionToRole;
import net.risesoft.manager.authorization.Y9PositionToRoleManager;
import net.risesoft.manager.org.Y9PositionManager;
import net.risesoft.repository.identity.position.Y9PositionToRoleRepository;
import net.risesoft.service.identity.Y9PositionToRoleService;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.manager.role.Y9RoleManager;

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

    private final Y9RoleManager y9RoleManager;
    private final Y9PositionManager y9PositionManager;
    private final Y9PositionToRoleManager y9PositionToRoleManager;

    @Override
    @Transactional(readOnly = false)
    public void recalculate(String positionId) {
        List<Y9Role> positionRelatedY9RoleList = y9RoleManager.listOrgUnitRelatedWithoutNegative(positionId);
        Y9Position y9Position = y9PositionManager.getById(positionId);
        y9PositionToRoleManager.update(y9Position, positionRelatedY9RoleList);
    }

    @Override
    public Boolean hasRole(String positionId, String customId) {
        long count = y9PositionToRoleRepository.countByPositionIdAndRoleCustomId(positionId, customId);
        return count > 0;
    }

    @Override
    public List<Y9PositionToRole> listByPositionId(String positionId) {
        return y9PositionToRoleRepository.findByPositionId(positionId);
    }

    @Override
    public List<Y9PositionToRole> listByPositionIdAndSystemName(String positionId, String systemName) {
        return y9PositionToRoleRepository.findByPositionIdAndSystemNameOrderByAppName(positionId, systemName);
    }

    @Override
    public Page<Y9PositionToRole> pageByPositionId(String positionId, int page, int rows, String sort) {
        return null;
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

    @Override
    @Transactional(readOnly = false)
    public void update(String roleId, String roleName, String systemName, String systemCnName, String description) {
        List<Y9PositionToRole> y9PositionToRoleList = y9PositionToRoleRepository.findByRoleId(roleId);
        for (Y9PositionToRole y9PositionToRole : y9PositionToRoleList) {
            y9PositionToRole.setSystemCnName(systemCnName);
            y9PositionToRole.setSystemName(systemName);
            y9PositionToRole.setRoleName(roleName);
            y9PositionToRole.setDescription(description);
            y9PositionToRoleRepository.save(y9PositionToRole);
        }
    }

}
