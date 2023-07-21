package net.risesoft.manager.org.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.relation.Y9PersonsToGroups;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.exception.PersonErrorCodeEnum;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.repository.Y9PersonRepository;
import net.risesoft.repository.relation.Y9PersonsToGroupsRepository;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;

@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@CacheConfig(cacheNames = CacheNameConsts.ORG_PERSON)
@RequiredArgsConstructor
public class Y9PersonManagerImpl implements Y9PersonManager {

    private final Y9PersonRepository y9PersonRepository;
    private final Y9PersonsToGroupsRepository y9PersonsToGroupsRepository;
    private final Y9PersonsToPositionsRepository y9PersonsToPositionsRepository;
    
    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Person getById(String id) {
        return y9PersonRepository.findById(id).orElseThrow(() -> Y9ExceptionUtil.notFoundException(PersonErrorCodeEnum.PERSON_NOT_FOUND, id));
    }

    @Override
    public List<Y9Person> listByPositionId(String positionId) {
        List<Y9PersonsToPositions> pps = y9PersonsToPositionsRepository.findByPositionIdOrderByPersonOrder(positionId);
        List<String> personIdList = pps.stream().map(Y9PersonsToPositions::getPersonId).collect(Collectors.toList());
        List<Y9Person> personList = new ArrayList<>();
        for (String personId : personIdList) {
            personList.add(getById(personId));
        }
        return personList;
    }    
    
    @Override
    public List<Y9Person> listByGroupId(String groupId) {
        List<Y9PersonsToGroups> pgs = y9PersonsToGroupsRepository.findByGroupIdOrderByPersonOrder(groupId);
        List<String> personIdList = pgs.stream().map(Y9PersonsToGroups::getPersonId).collect(Collectors.toList());
        List<Y9Person> personList = new ArrayList<>();
        for (String personId : personIdList) {
            personList.add(getById(personId));
        }
        return personList;
    }
}
