package net.risesoft.manager.relation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.AuditLogEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.manager.org.Y9PositionManager;
import net.risesoft.manager.relation.Y9PersonsToPositionsManager;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.util.Y9Assert;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9StringUtil;

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
    private final Y9PersonManager y9PersonManager;

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
        Y9Position y9Position = y9PositionManager.getById(positionId);
        Y9Person y9Person = y9PersonManager.getById(personId);

        Optional<Y9PersonsToPositions> optionalY9PersonsToPositions =
            y9PersonsToPositionsRepository.findByPositionIdAndPersonId(positionId, personId);
        if (optionalY9PersonsToPositions.isPresent()) {
            Y9PersonsToPositions y9PersonsToPositions = optionalY9PersonsToPositions.get();
            delete(y9PersonsToPositions);

            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(AuditLogEnum.POSITION_REMOVE_PERSON.getAction())
                .description(Y9StringUtil.format(AuditLogEnum.POSITION_REMOVE_PERSON.getDescription(),
                    y9Position.getName(), y9Person.getName()))
                .objectId(y9PersonsToPositions.getId())
                .oldObject(y9PersonsToPositions)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
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
            .map(Y9PersonsToPositions::getPersonOrder)
            .orElse(-1) + 1;
    }

    @Override
    public Integer getNextPositionOrderByPersonId(String personId) {
        return y9PersonsToPositionsRepository.findTopByPersonIdOrderByPositionOrderDesc(personId)
            .map(Y9PersonsToPositions::getPositionOrder)
            .orElse(-1) + 1;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9PersonsToPositions save(String personId, String positionId) {
        Y9Position y9Position = y9PositionManager.getById(positionId);
        Y9Person y9Person = y9PersonManager.getById(personId);

        // 校验岗位容量是否已满
        Y9Assert.lessThanOrEqualTo(countByPositionId(positionId) + 1, y9Position.getCapacity(),
            OrgUnitErrorCodeEnum.POSITION_IS_FULL);

        Y9PersonsToPositions y9PersonsToPositions = new Y9PersonsToPositions();
        y9PersonsToPositions.setId(Y9IdGenerator.genId());
        y9PersonsToPositions.setPositionId(positionId);
        y9PersonsToPositions.setPersonId(personId);
        y9PersonsToPositions.setPositionOrder(getNextPositionOrderByPersonId(personId));
        y9PersonsToPositions.setPersonOrder(getNextPersonOrderByPositionId(positionId));
        Y9PersonsToPositions savedPersonToPositions = y9PersonsToPositionsRepository.save(y9PersonsToPositions);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.POSITION_ADD_PERSON.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.POSITION_ADD_PERSON.getDescription(), y9Position.getName(),
                y9Person.getName()))
            .objectId(savedPersonToPositions.getId())
            .oldObject(null)
            .currentObject(savedPersonToPositions)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedPersonToPositions));

        return savedPersonToPositions;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9PersonsToPositions saveOrUpdate(Y9PersonsToPositions y9PersonsToPositions) {
        checkPersonExist(y9PersonsToPositions.getPersonId());
        checkPositionExist(y9PersonsToPositions.getPositionId());

        if (StringUtils.isNotBlank(y9PersonsToPositions.getId())) {
            Optional<Y9PersonsToPositions> y9PersonsToPositionsOptional =
                y9PersonsToPositionsRepository.findById(y9PersonsToPositions.getId());
            if (y9PersonsToPositionsOptional.isPresent()) {
                Y9PersonsToPositions originY9PersonsToPositions = y9PersonsToPositionsOptional.get();
                Y9BeanUtil.copyProperties(y9PersonsToPositions, originY9PersonsToPositions);
                return y9PersonsToPositionsRepository.save(originY9PersonsToPositions);
            }
        } else {
            y9PersonsToPositions.setId(Y9IdGenerator.genId());
        }

        Y9PersonsToPositions savedPersonToPositions = y9PersonsToPositionsRepository.save(y9PersonsToPositions);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedPersonToPositions));

        return savedPersonToPositions;
    }

    /**
     * 检查岗位是否存在
     *
     * @param positionId 岗位 id
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    private void checkPositionExist(String positionId) {
        y9PositionManager.getById(positionId);
    }

    /**
     * 检查人员是否存在
     *
     * @param personId 人员 id
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    private void checkPersonExist(String personId) {
        y9PersonManager.getById(personId);
    }

    public Integer countByPositionId(String positionId) {
        return y9PersonsToPositionsRepository.countByPositionId(positionId);
    }
}
