package net.risesoft.manager.relation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.Y9PositionManager;
import net.risesoft.manager.relation.Y9PersonsToPositionsManager;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.util.Y9Assert;

/**
 * 人员岗位关联 Manager 实现类
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9PersonsToPositionsManagerImpl implements Y9PersonsToPositionsManager {

    private final Y9PersonsToPositionsRepository y9PersonsToPositionsRepository;

    private final Y9PositionManager y9PositionManager;

    @Override
    @Transactional(readOnly = false)
    public List<Y9PersonsToPositions> addPositions(String personId, List<String> positionIds) {
        List<Y9PersonsToPositions> personsToPositionsList = new ArrayList<>();
        for (String positionId : positionIds) {
            if (y9PersonsToPositionsRepository.findByPositionIdAndPersonId(positionId, personId).isPresent()) {
                continue;
            }
            personsToPositionsList.add(save(personId, positionId));
        }
        return personsToPositionsList;
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String positionId, String personId) {
        Optional<Y9PersonsToPositions> optionalY9PersonsToPositions =
            y9PersonsToPositionsRepository.findByPositionIdAndPersonId(positionId, personId);
        if (optionalY9PersonsToPositions.isPresent()) {
            delete(optionalY9PersonsToPositions.get());
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(Y9PersonsToPositions y9PersonsToPositions) {
        y9PersonsToPositionsRepository.delete(y9PersonsToPositions);

        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9PersonsToPositions));
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByPersonId(String personId) {
        List<Y9PersonsToPositions> y9PersonsToPositionsList = y9PersonsToPositionsRepository.findByPersonId(personId);
        for (Y9PersonsToPositions y9PersonsToPositions : y9PersonsToPositionsList) {
            this.delete(y9PersonsToPositions);
        }
    }

    @Override
    public void deleteByPositionId(String positionId) {
        List<Y9PersonsToPositions> y9PersonsToPositionsList =
            y9PersonsToPositionsRepository.findByPositionId(positionId);
        for (Y9PersonsToPositions y9PersonsToPositions : y9PersonsToPositionsList) {
            this.delete(y9PersonsToPositions);
        }
    }

    @Override
    public Integer getNextPersonOrderByPositionId(String positionId) {
        return y9PersonsToPositionsRepository.findTopByPositionIdOrderByPersonOrderDesc(positionId)
            .map(Y9PersonsToPositions::getPersonOrder).orElse(-1) + 1;
    }

    @Override
    public Integer getNextPositionOrderByPersonId(String personId) {
        return y9PersonsToPositionsRepository.findTopByPersonIdOrderByPositionOrderDesc(personId)
            .map(Y9PersonsToPositions::getPositionOrder).orElse(-1) + 1;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9PersonsToPositions save(String personId, String positionId) {
        // 校验岗位容量是否已满
        Y9Position y9Position = y9PositionManager.getById(positionId);
        Y9Assert.lessThanOrEqualTo(countByPositionId(positionId) + 1, y9Position.getCapacity(),
            OrgUnitErrorCodeEnum.POSITION_IS_FULL);

        Y9PersonsToPositions y9PersonsToPositions = new Y9PersonsToPositions();
        y9PersonsToPositions.setId(Y9IdGenerator.genId());
        y9PersonsToPositions.setPositionId(positionId);
        y9PersonsToPositions.setPersonId(personId);
        y9PersonsToPositions.setPositionOrder(getNextPositionOrderByPersonId(personId));
        y9PersonsToPositions.setPersonOrder(getNextPersonOrderByPositionId(positionId));
        Y9PersonsToPositions savedPersonToPositions = y9PersonsToPositionsRepository.save(y9PersonsToPositions);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedPersonToPositions));

        return savedPersonToPositions;
    }

    public Integer countByPositionId(String positionId) {
        return y9PersonsToPositionsRepository.countByPositionId(positionId);
    }
}
