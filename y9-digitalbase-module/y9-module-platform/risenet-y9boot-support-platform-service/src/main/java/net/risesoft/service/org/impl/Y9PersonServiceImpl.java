package net.risesoft.service.org.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.org.Y9Department;
import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.entity.org.Y9Organization;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9PersonExt;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.enums.AuditLogEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9PersonExtManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.manager.org.Y9PositionManager;
import net.risesoft.manager.relation.Y9PersonsToPositionsManager;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.PersonExt;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.query.platform.PersonQuery;
import net.risesoft.repository.org.Y9PersonRepository;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.setting.Y9SettingService;
import net.risesoft.specification.Y9PersonSpecification;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9AssertUtil;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9StringUtil;
import net.risesoft.y9.util.signing.Y9MessageDigestUtil;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class Y9PersonServiceImpl implements Y9PersonService {

    private final Y9PersonRepository y9PersonRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9PersonExtManager y9PersonExtManager;
    private final Y9PersonManager y9PersonManager;
    private final Y9PositionManager y9PositionManager;
    private final Y9PersonsToPositionsManager y9PersonsToPositionsManager;

    private final Y9SettingService y9SettingService;

    @Override
    public boolean isCaidAvailable(String personId, String caid) {
        List<Y9Person> y9PersonList = y9PersonRepository.findByCaid(caid);
        if (y9PersonList.isEmpty()) {
            return true;
        }

        // 修改信息
        return y9PersonList.stream().map(Y9OrgBase::getId).anyMatch(id -> id.equals(personId));
    }

    @Override
    @Transactional
    public List<Person> addPersons(String parentId, List<String> personIds) {
        List<Y9Person> personList = new ArrayList<>();
        for (String originalId : personIds) {
            Y9Person originalPerson = y9PersonManager.getById(originalId);
            if (StringUtils.isNotBlank(originalPerson.getOriginalId())) {
                originalId = originalPerson.getOriginalId();
                originalPerson = y9PersonManager.getById(originalId);
            }
            Optional<Y9Person> y9PersonOptional = y9PersonRepository.findByOriginalIdAndParentId(originalId, parentId);
            if (y9PersonOptional.isPresent()) {
                Y9Person currentPerson = y9PersonOptional.get();
                currentPerson.setDisabled(Boolean.FALSE);
                currentPerson.setName(originalPerson.getName());
                currentPerson.setLoginName(originalPerson.getLoginName());
                currentPerson.setEmail(originalPerson.getEmail());
                currentPerson.setMobile(originalPerson.getMobile());
                currentPerson.setDescription(originalPerson.getDescription());
                final Y9Person savedPerson = y9PersonManager.update(currentPerson);
                personList.add(savedPerson);
                continue;
            }
            Y9Person person = new Y9Person();
            Y9BeanUtil.copyProperties(originalPerson, person);
            person.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            person.setOriginal(Boolean.FALSE);
            person.setOriginalId(originalId);
            person.setParentId(parentId);
            final Y9Person savedPerson = y9PersonManager.insert(person);

            Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedPerson));

            personList.add(savedPerson);
        }
        return PlatformModelConvertUtil.y9PersonToPerson(personList);
    }

    @Override
    @Transactional
    public Person changeDisabled(String id) {
        Y9Person currentPerson = y9PersonManager.getById(id);
        Y9Person originalPerson = PlatformModelConvertUtil.convert(currentPerson, Y9Person.class);

        boolean disableStatusToUpdate = !originalPerson.getDisabled();
        currentPerson.setDisabled(disableStatusToUpdate);
        Y9Person savedPerson = y9PersonManager.update(currentPerson);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.PERSON_UPDATE_DISABLED.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.PERSON_UPDATE_DISABLED.getDescription(),
                savedPerson.getName(), disableStatusToUpdate ? "禁用" : "启用"))
            .objectId(id)
            .oldObject(originalPerson)
            .currentObject(savedPerson)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return PlatformModelConvertUtil.y9PersonToPerson(savedPerson);
    }

    @Override
    public long countByGuidPathLikeAndDisabledAndDeletedFalse(String guidPath) {
        return y9PersonRepository.countByDisabledAndGuidPathContaining(Boolean.FALSE, guidPath);
    }

    @Override
    @Transactional
    public Person create(String parentId, String name, String loginName, String mobile) {
        Optional<Person> y9PersonOptional = this.findByLoginName(loginName);
        Person y9Person = y9PersonOptional.orElse(new Person());
        y9Person.setParentId(parentId);
        y9Person.setName(name);
        y9Person.setLoginName(loginName);
        y9Person.setMobile(mobile);
        return this.saveOrUpdate(y9Person, new PersonExt());
    }

    @Override
    @Transactional
    public void delete(List<String> ids) {
        for (String id : ids) {
            delete(id);
        }
    }

    @Override
    @Transactional
    public void delete(String id) {
        Y9Person y9Person = y9PersonManager.getById(id);

        // 删除人员关联数据
        y9PersonsToPositionsManager.deleteByPersonId(id);
        y9PersonExtManager.deleteByPersonId(id);
        y9PersonManager.delete(y9Person);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.PERSON_DELETE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.PERSON_DELETE.getDescription(), y9Person.getName()))
            .objectId(id)
            .oldObject(y9Person)
            .currentObject(null)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        // 发布事件，程序内部监听处理相关业务
        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Person));
    }

    @Override
    @Transactional
    public void deleteByParentId(String parentId) {
        List<Y9Person> personList = y9PersonManager.listByParentId(parentId, null);
        for (Y9Person person : personList) {
            delete(person.getId());
        }
    }

    @Override
    public Optional<Person> findById(String id) {
        return y9PersonManager.findByIdFromCache(id).map(PlatformModelConvertUtil::y9PersonToPerson);
    }

    @Override
    public Optional<Person> findByLoginName(final String loginName) {
        return y9PersonRepository.findByLoginNameAndOriginalTrue(loginName)
            .map(PlatformModelConvertUtil::y9PersonToPerson);
    }

    @Override
    public Optional<Person> findByCaId(final String caId) {
        return y9PersonRepository.findByCaidAndOriginalTrue(caId).map(PlatformModelConvertUtil::y9PersonToPerson);
    }

    @Override
    public List<String> findIdByGuidPathStartingWith(String guidPath) {
        return y9PersonRepository.findIdByGuidPathStartingWith(guidPath);
    }

    @Override
    @Transactional(readOnly = true)
    public Person getById(String id) {
        return PlatformModelConvertUtil.y9PersonToPerson(y9PersonManager.getByIdFromCache(id));
    }

    @Override
    public Optional<Person> getByLoginNameAndParentId(String loginName, String parentId) {
        return y9PersonRepository.findByLoginNameAndParentId(loginName, parentId)
            .map(PlatformModelConvertUtil::y9PersonToPerson);
    }

    @Override
    @Transactional(readOnly = true)
    public Person getPersonByLoginNameAndTenantId(String loginName, String tenantId) {
        List<Y9Person> personList = new ArrayList<>();
        try {
            personList = y9PersonRepository.findByLoginNameAndTenantIdAndOriginal(loginName, tenantId, Boolean.TRUE);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        if (personList.isEmpty()) {
            return null;
        }
        return PlatformModelConvertUtil.y9PersonToPerson(personList.get(0));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLoginNameAvailable(String personId, final String loginName) {
        Optional<Y9Person> y9PersonOptional = y9PersonRepository.findByLoginNameAndOriginalTrue(loginName);

        if (y9PersonOptional.isEmpty()) {
            // 不存在同登录名的人员肯定可用
            return true;
        }

        // 编辑人员时没修改登录名同样认为可用
        return y9PersonOptional.get().getId().equals(personId);
    }

    @Override
    public List<Person> list(Boolean disabled) {
        List<Y9Person> y9PersonList;
        if (disabled == null) {
            y9PersonList = y9PersonRepository.findAll();
        } else {
            y9PersonList = y9PersonRepository.findByDisabled(disabled);
        }
        Collections.sort(y9PersonList);
        return PlatformModelConvertUtil.y9PersonToPerson(y9PersonList);
    }

    @Override
    public List<Person> listAll() {
        return PlatformModelConvertUtil.y9PersonToPerson(y9PersonRepository.findAll());
    }

    @Override
    public List<Person> listByGroupId(String groupId, Boolean disabled) {
        return PlatformModelConvertUtil.y9PersonToPerson(y9PersonManager.listByGroupId(groupId, disabled));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> listByIdTypeAndIdNum(String idType, String idNum, Boolean disabled) {
        List<Y9PersonExt> y9PersonExtList = y9PersonExtManager.listByIdTypeAndIdNum(idType, idNum);
        List<Person> y9PersonList = new ArrayList<>();
        for (Y9PersonExt ext : y9PersonExtList) {
            Optional<Person> y9PersonOptional = this.findById(ext.getPersonId());
            if (y9PersonOptional.isPresent()) {
                Person y9Person = y9PersonOptional.get();
                if (disabled == null || disabled.equals(y9Person.getDisabled())) {
                    y9PersonList.add(y9Person);
                }
            }
        }
        return y9PersonList;
    }

    @Override
    public List<Person> listByNameLike(String name, Boolean disabled) {
        List<Y9Person> y9PersonList;
        if (disabled == null) {
            y9PersonList = y9PersonRepository.findByNameContaining(name);
        } else {
            y9PersonList = y9PersonRepository.findByNameContainingAndDisabled(name, disabled);
        }
        return PlatformModelConvertUtil.y9PersonToPerson(y9PersonList);
    }

    @Override
    public List<Person> listByParentId(String parentId, Boolean disabled) {
        return PlatformModelConvertUtil.y9PersonToPerson(y9PersonManager.listByParentId(parentId, disabled));
    }

    @Override
    public List<Person> listByPositionId(String positionId, Boolean disabled) {
        return PlatformModelConvertUtil.y9PersonToPerson(y9PersonManager.listByPositionId(positionId, disabled));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrgUnit> listParents(String personId) {
        List<String> parentIds = this.listParentIdByPersonId(personId);
        List<Y9OrgBase> parentList = new ArrayList<>();
        for (String parentId : parentIds) {
            Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(parentId);
            parentList.add(parent);
        }
        return PlatformModelConvertUtil.orgBaseToOrgUnit(parentList);
    }

    @Override
    @Transactional
    public Person modifyPassword(String id, String oldPassword, String newPassword) {
        Y9Person currentPerson = y9PersonManager.getById(id);

        if (StringUtils.isNotBlank(oldPassword)) {
            // 兼容旧接口，无 oldPassword
            Y9AssertUtil.isTrue(Y9MessageDigestUtil.bcryptMatch(oldPassword, currentPerson.getPassword()),
                OrgUnitErrorCodeEnum.OLD_PASSWORD_IS_INCORRECT);
        }

        Y9Person originalPerson = PlatformModelConvertUtil.convert(currentPerson, Y9Person.class);
        currentPerson.setPassword(Y9MessageDigestUtil.bcrypt(newPassword));
        Y9Person savedPerson = y9PersonManager.update(currentPerson);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.PERSON_UPDATE_PASSWORD.getAction())
            .description(
                Y9StringUtil.format(AuditLogEnum.PERSON_UPDATE_PASSWORD.getDescription(), currentPerson.getName()))
            .objectId(id)
            .oldObject(originalPerson)
            .currentObject(savedPerson)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return PlatformModelConvertUtil.y9PersonToPerson(savedPerson);
    }

    @Override
    @Transactional
    public Person move(String id, String parentId) {
        Y9Person currentPerson = y9PersonManager.getById(id);
        Y9OrgBase parentToMove = compositeOrgBaseManager.getOrgUnitAsParent(parentId);
        Y9Person originalPerson = PlatformModelConvertUtil.convert(currentPerson, Y9Person.class);

        currentPerson.setParentId(parentId);
        currentPerson.setTabIndex(compositeOrgBaseManager.getNextSubTabIndex(parentId));
        Y9Person savedPerson = y9PersonManager.update(currentPerson);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.PERSON_UPDATE_PARENTID.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.PERSON_UPDATE_PARENTID.getDescription(),
                savedPerson.getName(), parentToMove.getName()))
            .objectId(id)
            .oldObject(originalPerson)
            .currentObject(savedPerson)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return PlatformModelConvertUtil.y9PersonToPerson(savedPerson);
    }

    @Override
    @Transactional
    public List<Person> order(List<String> personIds) {
        List<Person> personList = new ArrayList<>();

        int tabIndex = 0;
        for (String personId : personIds) {
            personList.add(this.updateTabIndex(personId, tabIndex++));
        }
        return personList;
    }

    @Override
    @Transactional
    public void resetDefaultPassword(String id) {
        Y9Person currentPerson = y9PersonManager.getById(id);
        Y9Person originalPerson = PlatformModelConvertUtil.convert(currentPerson, Y9Person.class);

        String password = y9SettingService.getTenantSetting().getUserDefaultPassword();
        currentPerson.setPassword(Y9MessageDigestUtil.bcrypt(password));
        Y9Person savedPerson = y9PersonManager.update(currentPerson);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.PERSON_RESET_PASSWORD.getAction())
            .description(
                Y9StringUtil.format(AuditLogEnum.PERSON_RESET_PASSWORD.getDescription(), currentPerson.getName()))
            .objectId(id)
            .oldObject(originalPerson)
            .currentObject(savedPerson)
            .build();
        Y9Context.publishEvent(auditLogEvent);
    }

    @Override
    @Transactional
    public Person saveAvator(String id, String avatorUrl) {
        final Y9Person person = y9PersonManager.getById(id);

        Person updatedPerson = PlatformModelConvertUtil.convert(person, Person.class);
        updatedPerson.setAvator(avatorUrl);
        return this.saveOrUpdate(updatedPerson, null);
    }

    @Override
    @Transactional
    public Person saveOrUpdate(Person person, PersonExt personExt) {
        Y9Person y9Person = PlatformModelConvertUtil.convert(person, Y9Person.class);
        Y9PersonExt y9PersonExt = PlatformModelConvertUtil.convert(personExt, Y9PersonExt.class);

        if (StringUtils.isNotBlank(y9Person.getId())) {
            Optional<Y9Person> personOptional = y9PersonManager.findById(y9Person.getId());
            if (personOptional.isPresent()) {
                Y9Person originalPerson = PlatformModelConvertUtil.convert(personOptional.get(), Y9Person.class);

                Y9Person savedPerson = y9PersonManager.update(y9Person);
                y9PersonExtManager.saveOrUpdate(y9PersonExt, savedPerson);
                y9PersonManager.updatePersonByOriginalId(savedPerson, y9PersonExt);

                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(AuditLogEnum.PERSON_UPDATE.getAction())
                    .description(
                        Y9StringUtil.format(AuditLogEnum.PERSON_UPDATE.getDescription(), savedPerson.getName()))
                    .objectId(savedPerson.getId())
                    .oldObject(originalPerson)
                    .currentObject(savedPerson)
                    .build();
                Y9Context.publishEvent(auditLogEvent);

                return PlatformModelConvertUtil.y9PersonToPerson(savedPerson);
            }
        }

        Y9Person savedPerson = y9PersonManager.insert(y9Person);
        y9PersonExtManager.saveOrUpdate(y9PersonExt, savedPerson);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.PERSON_CREATE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.PERSON_CREATE.getDescription(), savedPerson.getName()))
            .objectId(savedPerson.getId())
            .oldObject(null)
            .currentObject(savedPerson)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return PlatformModelConvertUtil.y9PersonToPerson(savedPerson);
    }

    @Override
    @Transactional
    public Person saveOrUpdate(Person person, PersonExt ext, List<String> positionIds, List<String> jobIds) {
        person = this.saveOrUpdate(person, ext);

        if (positionIds != null) {
            // 关联已有岗位
            y9PersonsToPositionsManager.addPositions(person.getId(), positionIds);
        }

        if (jobIds != null) {
            // 根据职位初始化新岗位并关联
            List<String> newPositionIdList = new ArrayList<>();
            for (String jobId : jobIds) {
                Y9Position y9Position = new Y9Position();
                y9Position.setJobId(jobId);
                y9Position.setParentId(person.getParentId());

                Y9Position newPosition = y9PositionManager.insert(y9Position);
                newPositionIdList.add(newPosition.getId());
            }
            y9PersonsToPositionsManager.addPositions(person.getId(), newPositionIdList);
        }
        return person;
    }

    @Override
    @Transactional
    public Person saveProperties(String id, String properties) {
        Y9Person currentPerson = y9PersonManager.getById(id);
        Y9Person originalPerson = PlatformModelConvertUtil.convert(currentPerson, Y9Person.class);

        currentPerson.setProperties(properties);
        Y9Person savedPerson = y9PersonManager.update(currentPerson);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.PERSON_UPDATE_PROPERTIES.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.PERSON_UPDATE_PROPERTIES.getDescription(),
                savedPerson.getName(), savedPerson.getProperties()))
            .objectId(id)
            .oldObject(originalPerson)
            .currentObject(savedPerson)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return PlatformModelConvertUtil.y9PersonToPerson(savedPerson);
    }

    @Override
    @Transactional
    public Person saveWeixinId(String id, String weixinId) {
        Y9Person currentPerson = y9PersonManager.getById(id);
        currentPerson.setWeixinId(weixinId);
        Y9Person savedPerson = y9PersonManager.update(currentPerson);
        return PlatformModelConvertUtil.y9PersonToPerson(savedPerson);

    }

    @Override
    @Transactional
    public Person updateTabIndex(String id, int tabIndex) {
        Y9Person currentPerson = y9PersonManager.getById(id);
        Y9Person originalPerson = PlatformModelConvertUtil.convert(currentPerson, Y9Person.class);

        currentPerson.setTabIndex(tabIndex);
        currentPerson.setOrderedPath(compositeOrgBaseManager.buildOrderedPath(currentPerson));
        Y9Person savedPerson = y9PersonManager.update(currentPerson);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.PERSON_UPDATE_TABINDEX.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.PERSON_UPDATE_TABINDEX.getDescription(),
                savedPerson.getName(), savedPerson.getTabIndex()))
            .objectId(id)
            .oldObject(originalPerson)
            .currentObject(savedPerson)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return PlatformModelConvertUtil.y9PersonToPerson(savedPerson);
    }

    @Override
    public Y9Page<Person> page(PersonQuery personQuery, Y9PageQuery pageQuery) {
        Pageable pageable =
            PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by(Sort.Direction.ASC, "createTime"));
        Page<Y9Person> y9PersonPage = y9PersonRepository.findAll(new Y9PersonSpecification(personQuery), pageable);
        return Y9Page.success(pageQuery.getPage(), y9PersonPage.getTotalPages(), y9PersonPage.getTotalElements(),
            PlatformModelConvertUtil.y9PersonToPerson(y9PersonPage.getContent()));
    }

    /**
     * 根据人员id，获取该人员所有的父节点id列表
     *
     * @param personId 人员id
     * @return {@link List}<{@link String}>
     */
    private List<String> listParentIdByPersonId(String personId) {
        List<String> parentIdList = new ArrayList<>();
        Y9Person person = y9PersonManager.getById(personId);
        String parentId = person.getParentId();
        if (!Boolean.TRUE.equals(person.getOriginal())) {
            Y9Person originalPerson = y9PersonManager.getById(person.getOriginalId());
            parentId = originalPerson.getParentId();
            personId = originalPerson.getId();
        }
        parentIdList.add(parentId);
        List<Y9Person> personList = y9PersonRepository.findByOriginalIdAndDisabled(personId, Boolean.FALSE);
        parentIdList.addAll(personList.stream().map(Y9Person::getParentId).collect(Collectors.toList()));
        return parentIdList;
    }

    @EventListener
    @Transactional
    public void onParentDepartmentDeleted(Y9EntityDeletedEvent<Y9Department> event) {
        Y9Department parentDepartment = event.getEntity();
        // 删除部门时其下人员也要删除
        deleteByParentId(parentDepartment.getId());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("删除部门时其下人员同步删除执行完成！");
        }
    }

    @EventListener
    @Transactional
    public void onParentOrganizationDeleted(Y9EntityDeletedEvent<Y9Organization> event) {
        Y9Organization y9Organization = event.getEntity();
        // 删除组织时其下人员也要删除
        deleteByParentId(y9Organization.getId());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("删除组织时其下人员同步删除执行完成！");
        }
    }

    @EventListener
    @Transactional
    public void onParentUpdated(Y9EntityUpdatedEvent<? extends Y9OrgBase> event) {
        Y9OrgBase originOrgBase = event.getOriginEntity();
        Y9OrgBase updatedOrgBase = event.getUpdatedEntity();

        if (Y9OrgUtil.isCurrentOrAncestorChanged(originOrgBase, updatedOrgBase)) {
            List<Y9Person> personList = y9PersonRepository.findByParentIdOrderByTabIndex(updatedOrgBase.getId());
            for (Y9Person person : personList) {
                y9PersonManager.update(person);
            }
        } else if (Y9OrgUtil.isTabIndexChanged(originOrgBase, updatedOrgBase)) {
            List<Y9Person> personList = compositeOrgBaseManager.listAllDescendantPersons(updatedOrgBase.getId());
            for (Y9Person y9Person : personList) {
                y9PersonManager.update(y9Person);
            }
        }
    }
}
