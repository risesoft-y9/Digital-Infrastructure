package net.risesoft.manager.permission.cache.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.org.Y9Position;
import net.risesoft.entity.permission.cache.position.Y9PositionToRole;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.permission.cache.Y9PositionToRoleManager;
import net.risesoft.repository.permission.cache.position.Y9PositionToRoleRepository;
import net.risesoft.y9public.entity.Y9Role;

/**
 * 岗位角色 Manager 实现类
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
@Service
@RequiredArgsConstructor
public class Y9PositionToRoleManagerImpl implements Y9PositionToRoleManager {

    private final Y9PositionToRoleRepository y9PositionToRoleRepository;

    @Override
    public void deleteByPositionIdAndRoleId(String positionId, String roleId) {
        y9PositionToRoleRepository.deleteByPositionIdAndRoleId(positionId, roleId);
    }

    @Override
    public List<String> listRoleIdByPositionId(String positionId) {
        return y9PositionToRoleRepository.listRoleIdsByPositionId(positionId);
    }

    @Override
    public void save(Y9Position y9Position, Y9Role role) {
        Optional<Y9PositionToRole> optionalY9PositionToRole =
            y9PositionToRoleRepository.findByPositionIdAndRoleId(y9Position.getId(), role.getId());
        if (optionalY9PositionToRole.isEmpty()) {
            Y9PositionToRole y9PositionToRole = new Y9PositionToRole();
            y9PositionToRole.setId(Y9IdGenerator.genId());
            y9PositionToRole.setTenantId(y9Position.getTenantId());
            y9PositionToRole.setPositionId(y9Position.getId());
            y9PositionToRole.setRoleId(role.getId());
            y9PositionToRole.setAppId(role.getAppId());
            y9PositionToRole.setSystemId(role.getSystemId());
            y9PositionToRoleRepository.save(y9PositionToRole);
        } else {
            Y9PositionToRole y9PositionToRole = optionalY9PositionToRole.get();
            y9PositionToRole.setAppId(role.getAppId());
            y9PositionToRole.setSystemId(role.getSystemId());
            y9PositionToRoleRepository.save(y9PositionToRole);
        }
    }
}
