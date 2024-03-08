package net.risesoft.manager.authorization.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Position;
import net.risesoft.entity.identity.position.Y9PositionToRole;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.authorization.Y9PositionToRoleManager;
import net.risesoft.repository.identity.position.Y9PositionToRoleRepository;
import net.risesoft.y9public.entity.role.Y9Role;

/**
 * 岗位角色 Manager 实现类
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9PositionToRoleManagerImpl implements Y9PositionToRoleManager {

    private final Y9PositionToRoleRepository y9PositionToRoleRepository;

    @Override
    @Transactional(readOnly = false)
    public void deleteByPositionIdAndRoleId(String positionId, String roleId) {
        y9PositionToRoleRepository.deleteByPositionIdAndRoleId(positionId, roleId);
    }

    @Override
    public List<String> listRoleIdByPositionId(String positionId) {
        return y9PositionToRoleRepository.listRoleIdsByPositionId(positionId);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void save(Y9Position y9Position, Y9Role role) {
        Optional<Y9PositionToRole> optionalY9PositionToRole =
            y9PositionToRoleRepository.findByPositionIdAndRoleId(y9Position.getId(), role.getId());
        if (optionalY9PositionToRole.isEmpty()) {
            Y9PositionToRole y9PositionToRole = new Y9PositionToRole();
            y9PositionToRole.setId(Y9IdGenerator.genId());
            y9PositionToRole.setTenantId(y9Position.getTenantId());
            y9PositionToRole.setPositionId(y9Position.getId());
            y9PositionToRole.setDepartmentId(y9Position.getParentId());
            y9PositionToRole.setRoleId(role.getId());
            y9PositionToRole.setRoleName(role.getName());
            y9PositionToRole.setSystemCnName(role.getSystemCnName());
            y9PositionToRole.setSystemName(role.getSystemName());
            y9PositionToRole.setAppName(role.getAppCnName());
            y9PositionToRole.setAppId(role.getAppId());
            y9PositionToRole.setDescription(role.getDescription());
            y9PositionToRoleRepository.save(y9PositionToRole);
        }
    }
}
