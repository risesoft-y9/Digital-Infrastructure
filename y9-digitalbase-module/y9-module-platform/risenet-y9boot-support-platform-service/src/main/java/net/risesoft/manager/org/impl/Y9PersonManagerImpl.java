package net.risesoft.manager.org.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.consts.InitDataConsts;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9PersonExt;
import net.risesoft.entity.relation.Y9PersonsToGroups;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9PersonExtManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.repository.Y9PersonRepository;
import net.risesoft.repository.relation.Y9PersonsToGroupsRepository;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.service.setting.Y9SettingService;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9.util.signing.Y9MessageDigest;

@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@CacheConfig(cacheNames = CacheNameConsts.ORG_PERSON)
@RequiredArgsConstructor
public class Y9PersonManagerImpl implements Y9PersonManager {

    private final Y9PersonRepository y9PersonRepository;
    private final Y9PersonsToGroupsRepository y9PersonsToGroupsRepository;
    private final Y9PersonsToPositionsRepository y9PersonsToPositionsRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9PersonExtManager y9PersonExtManager;

    private final Y9SettingService y9SettingService;

    @Override
    @CacheEvict(key = "#y9Person.id")
    @Transactional(readOnly = false)
    public void delete(Y9Person y9Person) {
        y9PersonRepository.delete(y9Person);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Optional<Y9Person> findById(String id) {
        return y9PersonRepository.findById(id);
    }

    @Override
    public Optional<Y9Person> findByIdNotCache(String id) {
        return y9PersonRepository.findById(id);
    }

    @Override
    public Y9Person getByIdNotCache(String id) {
        return y9PersonRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.PERSON_NOT_FOUND, id));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Person getById(String id) {
        return y9PersonRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.PERSON_NOT_FOUND, id));
    }

    @Override
    public List<Y9Person> listByGroupId(String groupId, Boolean disabled) {
        List<Y9PersonsToGroups> pgs = y9PersonsToGroupsRepository.findByGroupIdOrderByPersonOrder(groupId);
        List<String> personIdList = pgs.stream().map(Y9PersonsToGroups::getPersonId).collect(Collectors.toList());
        List<Y9Person> personList = new ArrayList<>();
        for (String personId : personIdList) {
            Y9Person y9Person = getById(personId);
            if (disabled == null) {
                personList.add(y9Person);
            } else if (disabled.equals(y9Person.getDisabled())) {
                personList.add(y9Person);
            }
        }
        return personList;
    }

    @Override
    public List<Y9Person> listByParentId(String parentId, Boolean disabled) {
        if (disabled == null) {
            return y9PersonRepository.findByParentIdOrderByTabIndex(parentId);
        } else {
            return y9PersonRepository.findByParentIdAndDisabledOrderByTabIndex(parentId, disabled);
        }
    }

    @Override
    public List<Y9Person> listByPositionId(String positionId, Boolean disabled) {
        List<Y9PersonsToPositions> pps = y9PersonsToPositionsRepository.findByPositionIdOrderByPersonOrder(positionId);
        List<String> personIdList = pps.stream().map(Y9PersonsToPositions::getPersonId).collect(Collectors.toList());
        List<Y9Person> personList = new ArrayList<>();
        for (String personId : personIdList) {
            Y9Person y9Person = getById(personId);
            if (disabled == null) {
                personList.add(y9Person);
            } else if (disabled.equals(y9Person.getDisabled())) {
                personList.add(y9Person);
            }
        }
        return personList;
    }

    @Override
    @CacheEvict(key = "#y9Person.id")
    @Transactional(readOnly = false)
    public Y9Person save(Y9Person y9Person) {
        return y9PersonRepository.save(y9Person);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person saveOrUpdate(Y9Person person, Y9PersonExt personExt) {
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(person.getParentId());
        if (StringUtils.isNotEmpty(person.getId())) {
            Optional<Y9Person> y9PersonOptional = this.findByIdNotCache(person.getId());
            if (y9PersonOptional.isPresent()) {
                // 判断为更新信息
                Y9Person originPerson = new Y9Person();
                Y9Person updatedPerson = y9PersonOptional.get();
                Y9BeanUtil.copyProperties(updatedPerson, originPerson);
                Y9BeanUtil.copyProperties(person, updatedPerson, "tenantId");

                updatedPerson.setGuidPath(Y9OrgUtil.buildGuidPath(parent.getGuidPath(), updatedPerson.getId()));
                updatedPerson.setDn(Y9OrgUtil.buildDn(OrgTypeEnum.PERSON, updatedPerson.getName(), parent.getDn()));
                updatedPerson.setOrderedPath(compositeOrgBaseManager.buildOrderedPath(updatedPerson));

                if (StringUtils.isBlank(originPerson.getEmail())) {
                    updatedPerson.setEmail(null);
                }

                if (Boolean.TRUE.equals(originPerson.getOriginal()) && null != personExt) {
                    updatePersonByOriginalId(updatedPerson, personExt);
                }
                final Y9Person savedPerson = save(updatedPerson);

                Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originPerson, savedPerson));

                if (personExt != null) {
                    y9PersonExtManager.saveOrUpdate(personExt, savedPerson);
                }

                return savedPerson;
            }
        } else {
            person.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }

        if (StringUtils.isBlank(person.getEmail())) {
            person.setEmail(null);
        }
        person.setTenantId(Y9LoginUserHolder.getTenantId());
        person.setVersion(InitDataConsts.Y9_VERSION);
        person.setOfficial(1);
        person.setDisabled(false);
        person.setParentId(parent.getId());
        person.setDn(Y9OrgUtil.buildDn(OrgTypeEnum.PERSON, person.getName(), parent.getDn()));
        person.setGuidPath(compositeOrgBaseManager.buildGuidPath(person));
        person.setTabIndex(compositeOrgBaseManager.getNextSubTabIndex(parent.getId()));
        person.setOrderedPath(compositeOrgBaseManager.buildOrderedPath(person));

        String password = y9SettingService.getTenantSetting().getUserDefaultPassword();
        person.setPassword(Y9MessageDigest.bcrypt(password));

        final Y9Person savedPerson = save(person);
        if (null != personExt) {
            y9PersonExtManager.saveOrUpdate(personExt, savedPerson);
        }

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedPerson));

        return savedPerson;
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#id")
    public Y9Person saveProperties(String id, String properties) {
        final Y9Person person = this.getById(id);

        Y9Person updatedPerson = Y9ModelConvertUtil.convert(person, Y9Person.class);
        updatedPerson.setProperties(properties);
        return saveOrUpdate(updatedPerson, null);
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#id")
    public Y9Person updateTabIndex(String id, int tabIndex) {
        Y9Person person = this.getById(id);

        Y9Person updatedPerson = Y9ModelConvertUtil.convert(person, Y9Person.class);
        updatedPerson.setTabIndex(tabIndex);
        updatedPerson.setOrderedPath(compositeOrgBaseManager.buildOrderedPath(person));
        return this.saveOrUpdate(updatedPerson, null);
    }

    @Transactional(readOnly = false)
    public void updatePersonByOriginalId(Y9Person originalPerson, Y9PersonExt originalExt) {
        List<Y9Person> persons = y9PersonRepository.findByOriginalId(originalPerson.getId());
        for (Y9Person person : persons) {
            person.setName(originalPerson.getName());
            person.setLoginName(originalPerson.getLoginName());
            person.setEmail(originalPerson.getEmail());
            person.setMobile(originalPerson.getMobile());
            person.setPassword(originalPerson.getPassword());

            Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(person.getParentId());
            person.setDn(Y9OrgUtil.buildDn(OrgTypeEnum.PERSON, person.getName(), parent.getDn()));

            this.save(person);

            Y9PersonExt ext = y9PersonExtManager.findByPersonId(person.getId()).orElse(new Y9PersonExt());
            if (originalExt != null) {
                ext.setIdType(originalExt.getIdType());
                ext.setIdNum(originalExt.getIdNum());
                Y9BeanUtil.copyProperties(originalExt, ext);
            }
            ext.setName(originalPerson.getName());
            ext.setPersonId(person.getId());
            y9PersonExtManager.saveOrUpdate(ext, person);
        }
    }
}
