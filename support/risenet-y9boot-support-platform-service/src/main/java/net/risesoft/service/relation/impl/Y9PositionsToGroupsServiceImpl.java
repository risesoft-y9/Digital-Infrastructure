package net.risesoft.service.relation.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9PositionsToGroups;
import net.risesoft.repository.Y9PositionRepository;
import net.risesoft.repository.relation.Y9PositionsToGroupsRepository;
import net.risesoft.service.relation.Y9PositionsToGroupsService;

/**
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@CacheConfig(cacheNames = CacheNameConsts.POSITIONS_TO_GROUPS)
@Service
@RequiredArgsConstructor
public class Y9PositionsToGroupsServiceImpl implements Y9PositionsToGroupsService {

    private final Y9PositionsToGroupsRepository y9PositionsToGroupsRepository;
    private final Y9PositionRepository y9PositionRepository;

    @Transactional(readOnly = false)
    public void delete(Y9PositionsToGroups groupPosition) {
        y9PositionsToGroupsRepository.delete(groupPosition);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByGroupId(String groupId) {
        List<Y9PositionsToGroups> list = y9PositionsToGroupsRepository.findByGroupId(groupId);
        for (Y9PositionsToGroups groupPosition : list) {
            delete(groupPosition);
        }

    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByPositionId(String positionId) {
        List<Y9PositionsToGroups> list = y9PositionsToGroupsRepository.findByPositionId(positionId);
        for (Y9PositionsToGroups groupPosition : list) {
            delete(groupPosition);
        }
    }

    @Override
    public Integer getMaxGroupIdOrderByPositionId(String positionId) {
        Y9PositionsToGroups position = y9PositionsToGroupsRepository.findTopByPositionIdOrderByGroupOrderDesc(positionId);
        if (position != null) {
            return position.getGroupOrder();
        }
        return 0;
    }

    @Override
    public Integer getMaxPositionOrderByGroupId(String groupId) {
        Y9PositionsToGroups position = y9PositionsToGroupsRepository.findTopByGroupIdOrderByPositionOrderDesc(groupId);
        if (position != null) {
            return position.getPositionOrder();
        }
        return 0;
    }

    @Override
    public List<Y9PositionsToGroups> listByGroupId(String groupId) {
        return y9PositionsToGroupsRepository.findByGroupIdOrderByPositionOrder(groupId);
    }

    @Override
    public List<Y9Position> listPositionsByGroupId(String groupId) {
        List<Y9PositionsToGroups> orgGroupPositions = y9PositionsToGroupsRepository.findByGroupIdOrderByPositionOrder(groupId);
        List<Y9Position> orgPositions = new ArrayList<>();
        for (Y9PositionsToGroups group : orgGroupPositions) {
            orgPositions.add(y9PositionRepository.findById(group.getPositionId()).orElse(null));
        }
        return orgPositions;
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9PositionsToGroups> orderPositions(String groupId, String[] positionIds) {
        List<Y9PositionsToGroups> positionList = new ArrayList<>();
        for (int i = 0; i < positionIds.length; i++) {
            Y9PositionsToGroups groupPosition = y9PositionsToGroupsRepository.findByGroupIdAndPositionId(groupId, positionIds[i]);
            groupPosition.setPositionOrder(i);
            positionList.add(y9PositionsToGroupsRepository.save(groupPosition));
        }
        return positionList;
    }

    @Override
    @Transactional(readOnly = false)
    public void removePositions(String groupId, String[] positionIds) {
        for (int i = 0; i < positionIds.length; i++) {
            Y9PositionsToGroups groupPosition = y9PositionsToGroupsRepository.findByGroupIdAndPositionId(groupId, positionIds[i]);
            delete(groupPosition);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9Position> saveGroupPosition(String groupId, String[] positionIds) {
        List<Y9Position> positionList = new ArrayList<>();
        Integer maxPositionOrder = getMaxPositionOrderByGroupId(groupId);
        for (int i = 0; i < positionIds.length; i++) {
            Y9Position position = y9PositionRepository.findById(positionIds[i]).orElse(null);
            if (y9PositionsToGroupsRepository.findByGroupIdAndPositionId(groupId, position.getId()) != null) {
                continue;
            }
            Integer maxGroupsOrder = getMaxGroupIdOrderByPositionId(positionIds[i]);
            Y9PositionsToGroups groupPosition = new Y9PositionsToGroups();
            groupPosition.setGroupId(groupId);
            groupPosition.setPositionId(position.getId());
            groupPosition.setPositionOrder(maxPositionOrder != null ? maxPositionOrder + i + 1 : i);
            groupPosition.setGroupOrder(maxGroupsOrder != null ? maxGroupsOrder + 1 : 0);
            y9PositionsToGroupsRepository.save(groupPosition);
            positionList.add(position);
        }
        return positionList;
    }

}
