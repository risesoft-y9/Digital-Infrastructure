package net.risesoft.manager.relation.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.manager.relation.Y9PositionsToGroupsManager;
import net.risesoft.repository.relation.Y9PositionsToGroupsRepository;

/**
 * 岗位用户组关联 Manager 实现类
 *
 * @author shidaobang
 * @date 2024/03/14
 */
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9PositionsToGroupsManagerImpl implements Y9PositionsToGroupsManager {

    private final Y9PositionsToGroupsRepository y9PositionsToGroupsRepository;

    @Override
    public void deleteByPositionId(String positionId) {
        y9PositionsToGroupsRepository.deleteByPositionId(positionId);
    }
}
