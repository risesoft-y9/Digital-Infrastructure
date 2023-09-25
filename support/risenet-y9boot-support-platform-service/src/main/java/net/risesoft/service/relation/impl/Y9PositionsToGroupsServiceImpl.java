package net.risesoft.service.relation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9PositionsToGroups;
import net.risesoft.manager.org.Y9PositionManager;
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
@Service
@RequiredArgsConstructor
public class Y9PositionsToGroupsServiceImpl implements Y9PositionsToGroupsService {

    private final Y9PositionsToGroupsRepository y9PositionsToGroupsRepository;
    private final Y9PositionManager y9PositionManager;

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
        return y9PositionsToGroupsRepository.findTopByPositionIdOrderByGroupOrderDesc(positionId)
            .map(Y9PositionsToGroups::getGroupOrder).orElse(0);
    }

    @Override
    public Integer getMaxPositionOrderByGroupId(String groupId) {
        return y9PositionsToGroupsRepository.findTopByGroupIdOrderByPositionOrderDesc(groupId)
            .map(Y9PositionsToGroups::getPositionOrder).orElse(0);
    }

    @Override
    public List<Y9PositionsToGroups> listByGroupId(String groupId) {
        return y9PositionsToGroupsRepository.findByGroupIdOrderByPositionOrder(groupId);
    }

    @Override
    public List<Y9Position> listPositionsByGroupId(String groupId) {
        List<Y9PositionsToGroups> orgGroupPositions =
            y9PositionsToGroupsRepository.findByGroupIdOrderByPositionOrder(groupId);
        List<Y9Position> orgPositions = new ArrayList<>();
        for (Y9PositionsToGroups group : orgGroupPositions) {
            orgPositions.add(y9PositionManager.getById(group.getPositionId()));
        }
        return orgPositions;
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9PositionsToGroups> orderPositions(String groupId, String[] positionIds) {
        List<Y9PositionsToGroups> positionList = new ArrayList<>();
        for (int i = 0; i < positionIds.length; i++) {
            Optional<Y9PositionsToGroups> optionalY9PositionsToGroups =
                y9PositionsToGroupsRepository.findByGroupIdAndPositionId(groupId, positionIds[i]);
            if (optionalY9PositionsToGroups.isPresent()) {
                Y9PositionsToGroups groupPosition = optionalY9PositionsToGroups.get();
                groupPosition.setPositionOrder(i);
                positionList.add(y9PositionsToGroupsRepository.save(groupPosition));
            }
        }
        return positionList;
    }

    @Override
    @Transactional(readOnly = false)
    public void removePositions(String groupId, String[] positionIds) {
        for (int i = 0; i < positionIds.length; i++) {
            Optional<Y9PositionsToGroups> optionalY9PositionsToGroups =
                y9PositionsToGroupsRepository.findByGroupIdAndPositionId(groupId, positionIds[i]);
            if (optionalY9PositionsToGroups.isPresent()) {
                delete(optionalY9PositionsToGroups.get());
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void saveGroupPosition(String groupId, String[] positionIds) {
        Integer maxPositionOrder = getMaxPositionOrderByGroupId(groupId);
        for (int i = 0; i < positionIds.length; i++) {
            if (y9PositionsToGroupsRepository.findByGroupIdAndPositionId(groupId, positionIds[i]).isPresent()) {
                continue;
            }
            Integer maxGroupsOrder = getMaxGroupIdOrderByPositionId(positionIds[i]);
            Y9PositionsToGroups groupPosition = new Y9PositionsToGroups();
            groupPosition.setGroupId(groupId);
            groupPosition.setPositionId(positionIds[i]);
            groupPosition.setPositionOrder(maxPositionOrder != null ? maxPositionOrder + i + 1 : i);
            groupPosition.setGroupOrder(maxGroupsOrder != null ? maxGroupsOrder + 1 : 0);
            y9PositionsToGroupsRepository.save(groupPosition);
        }
    }

}
