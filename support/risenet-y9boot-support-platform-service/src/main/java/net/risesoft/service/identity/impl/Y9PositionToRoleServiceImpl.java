package net.risesoft.service.identity.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.entity.identity.position.Y9PositionToRole;
import net.risesoft.repository.identity.position.Y9PositionToRoleRepository;
import net.risesoft.service.identity.Y9PositionToRoleService;

/**
 * PositionToRoleServiceImpl
 *
 * @author shidaobang
 * @date 2022/4/6
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@CacheConfig(cacheNames = CacheNameConsts.POSITIONS_TO_ROLES)
@Service
@RequiredArgsConstructor
public class Y9PositionToRoleServiceImpl implements Y9PositionToRoleService {

    private final Y9PositionToRoleRepository y9PositionToRoleRepository;
    
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
