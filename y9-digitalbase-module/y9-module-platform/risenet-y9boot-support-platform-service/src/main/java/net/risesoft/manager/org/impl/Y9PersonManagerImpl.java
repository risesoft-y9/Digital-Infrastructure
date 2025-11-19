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

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.consts.DefaultConsts;
import net.risesoft.consts.InitDataConsts;
import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9PersonExt;
import net.risesoft.entity.relation.Y9PersonsToGroups;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9PersonExtManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.repository.org.Y9PersonRepository;
import net.risesoft.repository.relation.Y9PersonsToGroupsRepository;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.service.setting.Y9SettingService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.signing.Y9MessageDigest;

@Service
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
    public void delete(Y9Person y9Person) {
        y9PersonRepository.delete(y9Person);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Optional<Y9Person> findByIdFromCache(String id) {
        return y9PersonRepository.findById(id);
    }

    @Override
    public Optional<Y9Person> findById(String id) {
        return y9PersonRepository.findById(id);
    }

    @Override
    public Y9Person getById(String id) {
        return y9PersonRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.PERSON_NOT_FOUND, id));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Person getByIdFromCache(String id) {
        return this.getById(id);
    }

    @Override
    public List<Y9Person> listByGroupId(String groupId, Boolean disabled) {
        List<Y9PersonsToGroups> pgs = y9PersonsToGroupsRepository.findByGroupIdOrderByPersonOrder(groupId);
        List<String> personIdList = pgs.stream().map(Y9PersonsToGroups::getPersonId).collect(Collectors.toList());
        List<Y9Person> personList = new ArrayList<>();
        for (String personId : personIdList) {
            Y9Person y9Person = getByIdFromCache(personId);
            if (disabled == null || disabled.equals(y9Person.getDisabled())) {
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
            Y9Person y9Person = getByIdFromCache(personId);
            if (disabled == null || disabled.equals(y9Person.getDisabled())) {
                personList.add(y9Person);
            }
        }
        return personList;
    }

    @CacheEvict(key = "#y9Person.id")
    public Y9Person save(Y9Person y9Person) {
        return y9PersonRepository.save(y9Person);
    }

    @Override
    public Y9Person insert(Y9Person person) {
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(person.getParentId());
        if (StringUtils.isBlank(person.getId())) {
            person.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            person.setDisabled(false);
        }

        if (StringUtils.isBlank(person.getEmail())) {
            person.setEmail(null);
        }

        if (StringUtils.isEmpty(person.getPassword())) {
            String defaultPassword = y9SettingService.getTenantSetting().getUserDefaultPassword();
            person.setPassword(Y9MessageDigest.bcrypt(defaultPassword));
        } else {
            if (!Y9MessageDigest.BCRYPT_PATTERN.matcher(person.getPassword()).matches()) {
                // 避免重复加密（导入的情况直接使用原密文）
                person.setPassword(Y9MessageDigest.bcrypt(person.getPassword()));
            }
        }

        person.setTenantId(Y9LoginUserHolder.getTenantId());
        person.setVersion(InitDataConsts.Y9_VERSION);
        person.setOfficial(1);
        person.setParentId(parent.getId());
        person.setDn(Y9OrgUtil.buildDn(OrgTypeEnum.PERSON, person.getName(), parent.getDn()));
        person.setGuidPath(compositeOrgBaseManager.buildGuidPath(person));
        person.setTabIndex((null == person.getTabIndex() || DefaultConsts.TAB_INDEX.equals(person.getTabIndex()))
            ? compositeOrgBaseManager.getNextSubTabIndex(parent.getId()) : person.getTabIndex());
        person.setOrderedPath(compositeOrgBaseManager.buildOrderedPath(person));

        final Y9Person savedPerson = save(person);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedPerson));

        return savedPerson;
    }

    @Override
    public Y9Person update(Y9Person person) {
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(person.getParentId());
        Y9Person currentPerson = this.getById(person.getId());
        Y9Person originPerson = PlatformModelConvertUtil.convert(currentPerson, Y9Person.class);

        Y9BeanUtil.copyProperties(person, currentPerson, "tenantId");
        currentPerson.setTenantId(Y9LoginUserHolder.getTenantId());
        currentPerson.setGuidPath(Y9OrgUtil.buildGuidPath(parent.getGuidPath(), currentPerson.getId()));
        currentPerson.setDn(Y9OrgUtil.buildDn(OrgTypeEnum.PERSON, currentPerson.getName(), parent.getDn()));
        currentPerson.setOrderedPath(compositeOrgBaseManager.buildOrderedPath(currentPerson));

        if (StringUtils.isBlank(currentPerson.getEmail())) {
            currentPerson.setEmail(null);
        }

        final Y9Person savedPerson = save(currentPerson);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originPerson, savedPerson));

        return savedPerson;
    }

    @Override
    @CacheEvict(key = "#id")
    public Y9Person updateTabIndex(String id, int tabIndex) {
        Y9Person person = this.getById(id);

        Y9Person updatedPerson = PlatformModelConvertUtil.convert(person, Y9Person.class);
        updatedPerson.setTabIndex(tabIndex);
        updatedPerson.setOrderedPath(compositeOrgBaseManager.buildOrderedPath(person));
        return this.update(updatedPerson);
    }

    @Override
    public void updatePersonByOriginalId(Y9Person originalPerson, Y9PersonExt originalExt) {
        List<Y9Person> persons = y9PersonRepository.findByOriginalId(originalPerson.getId());
        for (Y9Person person : persons) {
            Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(person.getParentId());

            person.setName(originalPerson.getName());
            person.setLoginName(originalPerson.getLoginName());
            person.setEmail(originalPerson.getEmail());
            person.setMobile(originalPerson.getMobile());
            person.setPassword(originalPerson.getPassword());
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
