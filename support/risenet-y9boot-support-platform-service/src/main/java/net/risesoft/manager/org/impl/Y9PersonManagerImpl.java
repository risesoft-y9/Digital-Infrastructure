package net.risesoft.manager.org.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
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
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.util.Y9PublishServiceUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;

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
        return y9PersonRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(PersonErrorCodeEnum.PERSON_NOT_FOUND, id));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Person findById(String id) {
        return y9PersonRepository.findById(id).orElse(null);
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

    @Override
    @CacheEvict(key = "#y9Person.id")
    @Transactional(readOnly = false)
    public void delete(Y9Person y9Person) {
        y9PersonRepository.delete(y9Person);
    }

    @Override
    @CacheEvict(key = "#y9Person.id")
    @Transactional(readOnly = false)
    public Y9Person save(Y9Person y9Person) {
        return y9PersonRepository.save(y9Person);
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#id")
    public Y9Person updateTabIndex(String id, int tabIndex) {
        Y9Person person = this.getById(id);
        person.setTabIndex(tabIndex);
        person = this.save(person);

        Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.orgPersonToPerson(person),
            Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_PERSON_TABINDEX, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "更新人员排序号", person.getName() + "的排序号更新为" + tabIndex);

        return person;
    }
}
