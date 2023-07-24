package net.risesoft.service.relation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.manager.relation.Y9PersonsToPositionsManager;
import net.risesoft.manager.org.Y9PositionManager;
import net.risesoft.model.PersonsPositions;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.service.relation.Y9PersonsToPositionsService;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.util.Y9PublishServiceUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@CacheConfig(cacheNames = CacheNameConsts.PERSONS_TO_POSITIONS)
@Service
@RequiredArgsConstructor
public class Y9PersonsToPositionsServiceImpl implements Y9PersonsToPositionsService {

    private final Y9PersonsToPositionsRepository y9PersonsToPositionsRepository;
    
    private final Y9PersonManager y9PersonManager;
    private final Y9PositionManager y9PositionManager;
    private final Y9PersonsToPositionsManager y9PersonsToPositionsManager;
    
    @Override
    @Transactional(readOnly = false)
    public List<Y9PersonsToPositions> addPersons(String positionId, String[] personIds) {
        List<Y9PersonsToPositions> personsToPositionsList = new ArrayList<>();
        for (int i = 0; i < personIds.length; i++) {
            String personId = personIds[i];
            if (y9PersonsToPositionsRepository.findByPositionIdAndPersonId(positionId, personId) != null) {
                continue;
            }
            personsToPositionsList.add(y9PersonsToPositionsManager.save(personId, positionId));
        }
        return personsToPositionsList;
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9PersonsToPositions> addPositions(String personId, String[] positionIds) {
        List<Y9PersonsToPositions> personsToPositionsList = new ArrayList<>();
        for (int i = 0; i < positionIds.length; i++) {
            String positionId = positionIds[i];
            if (y9PersonsToPositionsRepository.findByPositionIdAndPersonId(positionId, personId) != null) {
                continue;
            }
            personsToPositionsList.add(y9PersonsToPositionsManager.save(personId, positionId));
        }
        return personsToPositionsList;
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByPersonId(String personId) {
        List<Y9PersonsToPositions> y9PersonsToPositionsList = y9PersonsToPositionsRepository.findByPersonId(personId);
        for (Y9PersonsToPositions y9PersonsToPositions : y9PersonsToPositionsList) {
            y9PersonsToPositionsManager.delete(y9PersonsToPositions);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByPositionId(String positionId) {
        List<Y9PersonsToPositions> y9PersonsToPositionsList = y9PersonsToPositionsRepository.findByPositionId(positionId);
        for (Y9PersonsToPositions y9PersonsToPositions : y9PersonsToPositionsList) {
            y9PersonsToPositionsManager.delete(y9PersonsToPositions);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void deletePersons(String positionId, String[] personIds) {
        for (String personId : personIds) {
            y9PersonsToPositionsManager.delete(positionId, personId);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void deletePositions(String personId, String[] positionIds) {
        for (String positionId : positionIds) {
            y9PersonsToPositionsManager.delete(positionId, personId);
        }
    }


    @Override
    public List<Y9PersonsToPositions> listByPersonId(String personId) {
        return y9PersonsToPositionsRepository.getByPersonId(personId);
    }

    @Override
    public List<Y9PersonsToPositions> listByPositionId(String positionId) {
        return y9PersonsToPositionsRepository.findByPositionIdOrderByPersonOrder(positionId);
    }

    @Override
    public List<String> listPositionIdsByPersonId(String personId) {
        List<Y9PersonsToPositions> list = y9PersonsToPositionsRepository.findByPersonIdOrderByPositionOrderAsc(personId);
        return list.stream().map(Y9PersonsToPositions::getPositionId).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9PersonsToPositions> orderPersons(String positionId, String[] personIds) {
        List<Y9PersonsToPositions> orgPositionsPersonsList = new ArrayList<>();
        for (int i = 0; i < personIds.length; i++) {
            Y9PersonsToPositions y9PersonsToPositions = y9PersonsToPositionsRepository.findByPositionIdAndPersonId(positionId, personIds[i]);
            y9PersonsToPositions.setPersonOrder(i);

            y9PersonsToPositions = y9PersonsToPositionsRepository.save(y9PersonsToPositions);

            Y9Position y9Position = y9PositionManager.getById(positionId);
            Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.convert(y9PersonsToPositions, PersonsPositions.class), Y9OrgEventConst.RISEORGEVENT_TYPE_POSITION_ORDER, Y9LoginUserHolder.getTenantId());

            Y9Person person = y9PersonManager.getById(y9PersonsToPositions.getPersonId());
            Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "更新岗位的人员排序", y9Position.getName() + "的成员：" + person.getName() + "排序更改为" + y9PersonsToPositions.getPersonOrder());

            orgPositionsPersonsList.add(y9PersonsToPositions);
        }
        return orgPositionsPersonsList;
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9PersonsToPositions> orderPositions(String personId, String[] positionIds) {
        List<Y9PersonsToPositions> orgPositionsPersonsList = new ArrayList<>();

        for (int i = 0; i < positionIds.length; i++) {
            Y9PersonsToPositions y9PersonsToPositions = y9PersonsToPositionsRepository.findByPositionIdAndPersonId(positionIds[i], personId);
            y9PersonsToPositions.setPositionOrder(i);
            y9PersonsToPositions = y9PersonsToPositionsRepository.save(y9PersonsToPositions);

            Y9Person person = y9PersonManager.getById(personId);
            Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.convert(y9PersonsToPositions, PersonsPositions.class), Y9OrgEventConst.RISEORGEVENT_TYPE_POSITION_ORDER, Y9LoginUserHolder.getTenantId());
            Y9Position y9Position = y9PositionManager.getById(y9PersonsToPositions.getPositionId());
            Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "更新人员的岗位排序", person.getName() + "的岗位：" + y9Position.getName() + "排序更改为" + y9PersonsToPositions.getPositionOrder());

            orgPositionsPersonsList.add(y9PersonsToPositions);
        }
        return orgPositionsPersonsList;
    }

    @Override
    public String getPositionIdsByPersonId(String personId) {
        List<String> positionIdList = y9PersonsToPositionsRepository.listPositionIdsByPersonId(personId);
        return StringUtils.join(positionIdList, ",");
    }
}
