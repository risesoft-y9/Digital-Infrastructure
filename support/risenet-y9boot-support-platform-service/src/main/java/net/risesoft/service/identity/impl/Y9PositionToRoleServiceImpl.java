package net.risesoft.service.identity.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.identity.position.Y9PositionToRole;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.manager.authorization.Y9PositionToRoleManager;
import net.risesoft.manager.org.Y9PositionManager;
import net.risesoft.repository.identity.position.Y9PositionToRoleRepository;
import net.risesoft.service.identity.Y9PositionToRoleService;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.manager.role.Y9RoleManager;
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

    private final Y9RoleManager y9RoleManager;
    private final Y9PositionManager y9PositionManager;
    private final Y9PositionToRoleManager y9PositionToRoleManager;

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
        List<Y9Role> y9RoleList;
        if (StringUtils.isBlank(properties)) {
            y9RoleList = y9RoleRepository.findByNameAndSystemNameAndType(roleName, systemName, RoleTypeEnum.ROLE);
        } else {
            y9RoleList = y9RoleRepository.findByNameAndSystemNameAndPropertiesAndType(roleName, systemName, properties,
                RoleTypeEnum.ROLE);
        }

        return y9RoleList.stream().anyMatch(y9Role -> hasRole(positionId, y9Role.getId()));
    }

    @Override
    public Boolean hasRoleByCustomId(String positionId, String customId) {
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
    public void recalculate(String positionId) {
        List<Y9Role> positionRelatedY9RoleList = y9RoleManager.listOrgUnitRelatedWithoutNegative(positionId);
        Y9Position y9Position = y9PositionManager.getById(positionId);
        y9PositionToRoleManager.update(y9Position, positionRelatedY9RoleList);
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
