package net.risesoft.service.org.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.OrgLevelConsts;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9PersonExt;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToGroups;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.AuthorizationPrincipalTypeEnum;
import net.risesoft.enums.OrgTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9PersonExtManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.manager.org.Y9PositionManager;
import net.risesoft.manager.relation.Y9PersonsToPositionsManager;
import net.risesoft.model.Message;
import net.risesoft.repository.Y9DepartmentPropRepository;
import net.risesoft.repository.Y9DepartmentRepository;
import net.risesoft.repository.Y9GroupRepository;
import net.risesoft.repository.Y9PersonRepository;
import net.risesoft.repository.Y9PositionRepository;
import net.risesoft.repository.identity.person.Y9PersonToResourceAndAuthorityRepository;
import net.risesoft.repository.identity.person.Y9PersonToRoleRepository;
import net.risesoft.repository.permission.Y9AuthorizationRepository;
import net.risesoft.repository.relation.Y9OrgBasesToRolesRepository;
import net.risesoft.repository.relation.Y9PersonsToGroupsRepository;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.util.Y9PlatformUtil;
import net.risesoft.util.Y9PublishServiceUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.base64.Y9Base64Util;
import net.risesoft.y9.util.signing.Y9MessageDigest;
import net.risesoft.y9public.entity.user.Y9User;
import net.risesoft.y9public.repository.user.Y9UserRepository;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@Slf4j
public class Y9PersonServiceImpl implements Y9PersonService {

    private static final String LOGINNAME_EMPTY = "loginName cannt be empty";
    private static final String PASSWORD_EMPTY = "password cannt be empty";

    private final Y9PersonRepository y9PersonRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final Y9PersonsToGroupsRepository y9PersonsToGroupsRepository;
    private final Y9PersonsToPositionsRepository y9PersonsToPositionsRepository;
    private final Y9DepartmentRepository y9DepartmentRepository;
    private final Y9GroupRepository y9GroupRepository;
    private final Y9PositionRepository y9PositionRepository;
    private final Y9UserRepository y9UserRepository;
    private final Y9OrgBasesToRolesRepository y9OrgBasesToRolesRepository;
    private final Y9AuthorizationRepository y9AuthorizationRepository;
    private final Y9DepartmentPropRepository y9DepartmentPropRepository;
    private final Y9PersonToResourceAndAuthorityRepository y9PersonToResourceAndAuthorityRepository;
    private final Y9PersonToRoleRepository y9PersonToRoleRepository;

    private final Y9PersonExtManager y9PersonExtManager;
    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9PositionManager y9PositionManager;
    private final Y9PersonsToPositionsManager y9PersonsToPositionsManager;
    private final Y9PersonManager y9PersonManager;

    private final Y9Properties y9config;

    public Y9PersonServiceImpl(Y9PersonRepository y9PersonRepository, @Qualifier("rsTenantEntityManagerFactory") EntityManagerFactory entityManagerFactory, Y9PersonsToGroupsRepository y9PersonsToGroupsRepository, Y9PersonsToPositionsRepository y9PersonsToPositionsRepository,
                               Y9DepartmentRepository y9DepartmentRepository, Y9GroupRepository y9GroupRepository, Y9PositionRepository y9PositionRepository, Y9UserRepository y9UserRepository, Y9PersonExtManager y9PersonExtManager, CompositeOrgBaseManager compositeOrgBaseManager, Y9Properties y9config,
                               Y9OrgBasesToRolesRepository y9OrgBasesToRolesRepository, Y9AuthorizationRepository y9AuthorizationRepository, Y9DepartmentPropRepository y9DepartmentPropRepository, Y9PersonToResourceAndAuthorityRepository y9PersonToResourceAndAuthorityRepository,
                               Y9PersonToRoleRepository y9PersonToRoleRepository, Y9PersonsToPositionsManager y9PersonsToPositionsManager, Y9PositionManager y9PositionManager, Y9PersonManager y9PersonManager) {
        this.y9PersonRepository = y9PersonRepository;
        this.entityManagerFactory = entityManagerFactory;
        this.y9PersonsToGroupsRepository = y9PersonsToGroupsRepository;
        this.y9PersonsToPositionsRepository = y9PersonsToPositionsRepository;
        this.y9DepartmentRepository = y9DepartmentRepository;
        this.y9GroupRepository = y9GroupRepository;
        this.y9PositionRepository = y9PositionRepository;
        this.y9UserRepository = y9UserRepository;
        this.y9PersonExtManager = y9PersonExtManager;
        this.compositeOrgBaseManager = compositeOrgBaseManager;
        this.y9config = y9config;
        this.y9OrgBasesToRolesRepository = y9OrgBasesToRolesRepository;
        this.y9AuthorizationRepository = y9AuthorizationRepository;
        this.y9DepartmentPropRepository = y9DepartmentPropRepository;
        this.y9PersonToResourceAndAuthorityRepository = y9PersonToResourceAndAuthorityRepository;
        this.y9PersonToRoleRepository = y9PersonToRoleRepository;
        this.y9PersonsToPositionsManager = y9PersonsToPositionsManager;
        this.y9PositionManager = y9PositionManager;
        this.y9PersonManager = y9PersonManager;
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9Person> addPersons(String parentId, String[] personIds) {
        List<Y9Person> personList = new ArrayList<>();
        Y9OrgBase parent = compositeOrgBaseManager.getOrgBase(parentId);
        for (String originalId : personIds) {
            Y9Person originalPerson = getById(originalId);
            if (StringUtils.isNotBlank(originalPerson.getOriginalId())) {
                originalId = originalPerson.getOriginalId();
                originalPerson = getById(originalId);
            }
            Y9Person oldperson = y9PersonRepository.findByOriginalIdAndParentId(originalId, parentId);
            if (null != oldperson) {
                oldperson.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.PERSON) + oldperson.getName() + OrgLevelConsts.SEPARATOR + parent.getDn());
                oldperson.setDisabled(false);
                oldperson.setName(originalPerson.getName());
                oldperson.setLoginName(originalPerson.getLoginName());
                oldperson.setEmail(originalPerson.getEmail());
                oldperson.setMobile(originalPerson.getMobile());
                oldperson.setDescription(originalPerson.getDescription());
                oldperson = save(oldperson);
                personList.add(oldperson);
                continue;
            }
            Integer maxIndex = compositeOrgBaseManager.getMaxSubTabIndex(parentId, OrgTypeEnum.PERSON);
            Y9Person person = new Y9Person();
            Y9BeanUtil.copyProperties(originalPerson, person);
            person.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            person.setOriginal(Boolean.FALSE);
            person.setOriginalId(originalId);
            person.setParentId(parentId);
            person.setTabIndex(maxIndex);
            person.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.PERSON) + person.getName() + OrgLevelConsts.SEPARATOR + parent.getDn());
            person.setOrgType(OrgTypeEnum.PERSON.getEnName());
            person = save(person);

            Y9Context.publishEvent(new Y9EntityCreatedEvent<>(person));

            Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.orgPersonToPerson(person), Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_PERSON, Y9LoginUserHolder.getTenantId());
            Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "添加人员", "添加" + person.getName());

            personList.add(person);
        }
        return personList;
    }

    @Override
    public Message authenticate(final String loginName, final String password) {
        Message message = new Message();
        if (StringUtils.isEmpty(loginName)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg(LOGINNAME_EMPTY);
            return message;
        }
        if (StringUtils.isEmpty(password)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg(PASSWORD_EMPTY);
            return message;
        }
        String newpassword = "";
        try {
            newpassword = Y9Base64Util.decode(password);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        Y9Person person = y9PersonRepository.findByLoginNameAndOriginalTrue(loginName);
        if (null == person || !(Y9MessageDigest.checkpw(newpassword, person.getPassword()))) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg("loginName or password is incorrect");
            return message;
        }

        message.setStatus(Message.STATUS_SUCCESS);
        message.setMsg(person.getId());
        return message;
    }

    @Override
    public Message authenticate2(final String tenantName, final String loginName, final String password) {
        Message message = new Message();
        if (StringUtils.isEmpty(tenantName)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg("tenantName cannt be empty");
            return message;
        }
        if (StringUtils.isEmpty(loginName)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg(LOGINNAME_EMPTY);
            return message;
        }
        if (StringUtils.isEmpty(password)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg(PASSWORD_EMPTY);
            return message;
        }
        String newpassword = "";
        try {
            newpassword = Y9Base64Util.decode(password);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        Y9Person person = y9PersonRepository.findByLoginNameAndOriginalTrue(loginName);
        if (null == person || !(Y9MessageDigest.checkpw(newpassword, person.getPassword()))) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg("loginName or password is incorrect");
            return message;
        }

        message.setStatus(Message.STATUS_SUCCESS);
        message.setMsg(person.getId());
        return message;
    }

    @Override
    public Message authenticate3(final String tenantName, final String loginName, final String password) {
        Message message = new Message();
        if (StringUtils.isEmpty(tenantName)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg("tenantName cannt be empty");
            return message;
        }
        if (StringUtils.isEmpty(loginName)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg(LOGINNAME_EMPTY);
            return message;
        }
        if (StringUtils.isEmpty(password)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg(PASSWORD_EMPTY);
            return message;
        }
        String newpassword = "";
        try {
            newpassword = Y9Base64Util.decode(password);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        Y9Person person = null;
        String tenantId = "";
        if (loginName.contains("&")) {
            String realLoginName = loginName.split("&")[0];
            String fakeLoginName = loginName.split("&")[1];
            List<String> tenantIds = Y9PlatformUtil.getTenantByLoginName("operation");

            if (!tenantIds.isEmpty()) {
                tenantId = tenantIds.get(0);
            }
            Y9User orgUser = y9UserRepository.findByLoginNameAndTenantIdAndOriginalTrue(fakeLoginName, tenantId);
            if (null == orgUser || !(Y9MessageDigest.checkpw(newpassword, orgUser.getPassword()))) {
                message.setStatus(Message.STATUS_FAIL);
                message.setMsg("loginName or password is incorrect");
                return message;
            }

            person = y9PersonRepository.findByLoginNameAndOriginalTrue(realLoginName);
        } else {
            person = y9PersonRepository.findByLoginNameAndOriginalTrue(loginName);
            if (null == person || !(Y9MessageDigest.checkpw(newpassword, person.getPassword()))) {
                message.setStatus(Message.STATUS_FAIL);
                message.setMsg("loginName or password is incorrect");
                return message;
            }
        }
        message.setStatus(Message.STATUS_SUCCESS);

        HashMap<String, Object> map = new HashMap<>(16);
        map.put("person", person);
        map.put("tenantId", Y9LoginUserHolder.getTenantId());
        Y9Department y9Department = y9DepartmentRepository.findById(person.getParentId()).orElse(null);
        Y9OrgBase y9OrgBase = this.getBureau(person.getId());
        map.put("deptName", y9Department != null ? y9Department.getName() : "");
        map.put("bureauName", y9OrgBase != null ? y9OrgBase.getName() : "");
        message.setMsg(Y9JsonUtil.writeValueAsString(map));
        return message;
    }

    @Override
    public Message authenticate4(final String tenantName, final String loginName) {
        Message message = new Message();
        if (StringUtils.isEmpty(tenantName)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg("tenantName cannt be empty");
            return message;
        }
        if (StringUtils.isEmpty(loginName)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg(LOGINNAME_EMPTY);
            return message;
        }

        Y9Person person = y9PersonRepository.findByLoginNameAndOriginalTrue(loginName);
        if (person == null) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg("No matching user according to the loginName");
            return message;
        }

        message.setStatus(Message.STATUS_SUCCESS);
        HashMap<String, Object> jsonObject = new HashMap<>(16);
        jsonObject.put("person", person);
        jsonObject.put("tenantId", Y9LoginUserHolder.getTenantId());
        message.setMsg(jsonObject.toString());
        return message;
    }

    @Override
    public Message authenticate5(final String tenantShortName, final String mobile, final String password) {
        Message message = new Message();
        if (StringUtils.isEmpty(tenantShortName)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg("tenantShortName cannt be empty");
            return message;
        }
        if (StringUtils.isEmpty(mobile)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg("mobile cannt be empty");
            return message;
        }
        if (StringUtils.isEmpty(password)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg(PASSWORD_EMPTY);
            return message;
        }
        String newpassword = "";
        try {
            newpassword = Y9Base64Util.decode(password);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        Y9Person person = y9PersonRepository.findByDisabledFalseAndMobileAndOriginal(mobile, Boolean.TRUE);
        if (null == person || !(Y9MessageDigest.checkpw(newpassword, person.getPassword()))) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg("mobile or password is incorrect");
            return message;
        }

        message.setStatus(Message.STATUS_SUCCESS);

        HashMap<String, Object> map = new HashMap<>(16);
        map.put("person", person);
        map.put("tenantId", Y9LoginUserHolder.getTenantId());
        Y9Department y9Department = y9DepartmentRepository.findById(person.getParentId()).orElse(null);
        Y9OrgBase y9OrgBase = this.getBureau(person.getId());
        map.put("deptName", y9Department != null ? y9Department.getName() : "");
        map.put("bureauName", y9OrgBase != null ? y9OrgBase.getName() : "");
        message.setMsg(Y9JsonUtil.writeValueAsString(map));
        return message;
    }

    @Override
    public Message authenticate6(final String tenantShortName, final String loginName, final String password, final String parentId) {
        Message message = new Message();
        if (StringUtils.isEmpty(tenantShortName)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg("tenantName cannt be empty");
            return message;
        }
        if (StringUtils.isEmpty(loginName)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg(LOGINNAME_EMPTY);
            return message;
        }
        if (StringUtils.isEmpty(password)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg(PASSWORD_EMPTY);
            return message;
        }
        String newpassword = "";
        try {
            newpassword = Y9Base64Util.decode(password);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        Y9Person person = null;
        String tenantId = "";
        if (loginName.contains("&")) {
            String realLoginName = loginName.split("&")[0];
            String fakeLoginName = loginName.split("&")[1];
            List<String> tenantIds = Y9PlatformUtil.getTenantByLoginName("operation");
            if (!tenantIds.isEmpty()) {
                tenantId = tenantIds.get(0);
            }
            Y9User orgUser = y9UserRepository.findByLoginNameAndTenantIdAndOriginalTrue(fakeLoginName, tenantId);
            if (null == orgUser || !(Y9MessageDigest.checkpw(newpassword, orgUser.getPassword()))) {
                message.setStatus(Message.STATUS_FAIL);
                message.setMsg("loginName or password is incorrect");
                return message;
            }

            person = y9PersonRepository.findByLoginNameAndOriginalTrue(realLoginName);
        } else {
            person = y9PersonRepository.findByLoginNameAndOriginalTrue(loginName);
            if (null == person || !(Y9MessageDigest.checkpw(newpassword, person.getPassword()))) {
                message.setStatus(Message.STATUS_FAIL);
                message.setMsg("loginName or password is incorrect");
                return message;
            }
        }

        message.setStatus(Message.STATUS_SUCCESS);

        HashMap<String, Object> map = new HashMap<>(16);
        map.put("person", person);
        map.put("tenantId", Y9LoginUserHolder.getTenantId());
        Y9Department y9Department = y9DepartmentRepository.findById(person.getParentId()).orElse(null);
        Y9OrgBase y9OrgBase = this.getBureau(person.getId());
        map.put("deptName", y9Department != null ? y9Department.getName() : "");
        map.put("bureauName", y9OrgBase != null ? y9OrgBase.getName() : "");
        message.setMsg(Y9JsonUtil.writeValueAsString(map));
        return message;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person changeDisabled(String id) {
        Y9Person y9Person = this.getById(id);
        boolean disabled = y9Person.getDisabled();
        y9Person.setDisabled(!disabled);
        y9Person = y9PersonManager.save(y9Person);

        Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.orgPersonToPerson(y9Person), Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_PERSON, Y9LoginUserHolder.getTenantId());
        String event = "禁用";
        if (Boolean.TRUE.equals(disabled)) {
            event = "启用";
        } else {
            // 禁用人员的时候，将人员岗位移除
            y9PersonsToPositionsManager.deleteByPersonId(id);
        }
        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(y9Person, y9Person));
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, event + "人员", event + y9Person.getName());

        return y9Person;
    }

    @Override
    public boolean checkEmailAvailability(final String email) {
        List<Y9Person> personList = y9PersonRepository.findByEmailAndOriginal(email, Boolean.TRUE);
        return personList.isEmpty();
    }

    @Override
    public boolean checkLoginNameAvailability(String personId, final String loginName) {
        Y9Person y9Person = y9PersonRepository.findByLoginNameAndOriginalTrue(loginName);

        if (y9Person == null) {
            // 不存在同登录名的人员肯定可用
            return true;
        }

        // 编辑人员时没修改登录名同样认为可用
        return y9Person.getId().equals(personId);
    }

    @Override
    public long countByGuidPathLikeAndDisabledAndDeletedFalse(String guidPath) {
        return y9PersonRepository.countByDisabledAndGuidPathContaining(false, guidPath);
    }

    @Override
    public long countByParentId(String parentId) {
        return y9PersonRepository.countByParentId(parentId);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person createPerson(Y9Person person, Y9OrgBase parent) {
        if (person == null || parent == null) {
            return null;
        }
        if (StringUtils.isBlank(person.getId())) {
            person.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        String password = y9config.getCommon().getDefaultPassword();

        if (StringUtils.isBlank(person.getEmail())) {
            person.setEmail(null);
        }
        person.setTabIndex(compositeOrgBaseManager.getMaxSubTabIndex(parent.getId(), OrgTypeEnum.PERSON));
        if (person.getSex() == null) {
            person.setSex(1);
        }
        if (person.getOfficial() == null) {
            person.setOfficial(1);
        }
        if (person.getVersion() == null) {
            person.setVersion(OrgTypeEnum.Y9_VERSION);
        }
        person.setOrgType(OrgTypeEnum.PERSON.getEnName());
        person.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.PERSON) + person.getName() + OrgLevelConsts.SEPARATOR + parent.getDn());
        person.setDisabled(false);
        person.setParentId(parent.getId());
        String pwd = person.getPassword();
        if (StringUtils.isBlank(pwd)) {
            person.setPassword(Y9MessageDigest.hashpw(password));
        }

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(person));
        return save(person);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        Y9Person y9Person = this.getById(id);

        y9PersonsToPositionsManager.deleteByPersonId(id);

        // 删除人员关联数据
        y9OrgBasesToRolesRepository.deleteByOrgId(id);
        y9PersonsToGroupsRepository.deleteByPersonId(id);
        y9DepartmentPropRepository.deleteByOrgBaseId(id);

        y9PersonToResourceAndAuthorityRepository.deleteByPersonId(id);
        y9PersonToRoleRepository.deleteByPersonId(id);
        y9AuthorizationRepository.deleteByPrincipalIdAndPrincipalType(id, AuthorizationPrincipalTypeEnum.PERSON.getValue());

        y9PersonManager.delete(y9Person);
        // 发布事件，程序内部监听处理相关业务
        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Person));
        Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.orgPersonToPerson(y9Person), Y9OrgEventConst.RISEORGEVENT_TYPE_DELETE_PERSON, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "删除人员", "删除" + y9Person.getName());
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String[] ids) {
        for (String id : ids) {
            delete(id);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByParentId(String parentId) {
        List<Y9Person> personList = listByParentId(parentId);
        for (Y9Person person : personList) {
            delete(person.getId());
        }
    }

    /**
     * 根据人员id，做逻辑删除
     *
     * @param id
     */
    @Transactional(readOnly = false)
    public void deleteLogically(String id) {
        Y9Person y9Person = this.getById(id);
        String guid = Y9IdGenerator.genId(IdType.SNOWFLAKE);
        y9Person.setDisabled(true);
        y9Person.setLoginName(y9Person.getLoginName() + ":" + guid);
        y9Person.setMobile(y9Person.getMobile() + ":" + guid);
        if (StringUtils.isNotBlank(y9Person.getEmail())) {
            y9Person.setEmail(guid + ":" + y9Person.getEmail());
        }
        y9Person.setDescription(y9Person.getDescription() + ",parentId:" + y9Person.getParentId());
        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Person));
        y9PersonManager.save(y9Person);
    }

    @Override
    public boolean existsById(String id) {
        return y9PersonRepository.existsById(id);
    }

    @Override
    public Y9Person findById(String id) {
        return y9PersonManager.findById(id);
    }

    @Override
    public Y9OrgBase getBureau(String id) {
        return compositeOrgBaseManager.getOrgUnitBureau(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Y9Person getById(String id) {
        return y9PersonManager.getById(id);
    }

    @Override
    public Y9Person getByLoginNameAndParentId(String loginName, String parentId) {
        return y9PersonRepository.findByLoginNameAndParentId(loginName, parentId);
    }

    @Override
    public Integer getMaxTabIndex() {
        Y9Person person = y9PersonRepository.findTopByOrderByTabIndexDesc();
        if (person != null) {
            return person.getTabIndex();
        }
        return 0;
    }

    @Override
    public Y9Person getPersonByLoginName(final String loginName) {
        return y9PersonRepository.findByLoginNameAndOriginalTrue(loginName);
    }

    @Override
    public Y9Person getPersonByLoginNameAndTenantId(String loginName, String tenantId) {
        List<Y9Person> personList = new ArrayList<>();
        try {
            personList = y9PersonRepository.findByLoginNameAndTenantIdAndOriginal(loginName, tenantId, Boolean.TRUE);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        if (personList.isEmpty()) {
            return null;
        }
        return personList.get(0);
    }

    @Override
    public Y9Person getPersonByMobile(String mobile) {
        List<Y9Person> personList = new ArrayList<>();
        try {
            personList = y9PersonRepository.findByMobileAndOriginal(mobile, Boolean.TRUE);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        if (personList.isEmpty()) {
            return null;
        }
        return personList.get(0);
    }

    /**
     * 获取人员，不包括子部门里的人员，但包括用户组和岗位的人员
     *
     * @param parentId
     * @return
     */
    private List<Y9Person> getPersonByParentId(String parentId) {
        List<Y9Person> list = this.listByParentId(parentId);
        // 查找部门下的所有岗位
        List<Y9Position> positions = y9PositionRepository.findByParentIdOrderByTabIndexAsc(parentId);
        for (Y9Position y9Position : positions) {
            List<Y9PersonsToPositions> orgPositionPersons = y9PersonsToPositionsRepository.findByPositionId(y9Position.getId());
            for (Y9PersonsToPositions orgPositionsPerson : orgPositionPersons) {
                List<Y9Person> positionPersons = this.listByPositionId(orgPositionsPerson.getPositionId());
                list.addAll(positionPersons);
            }
        }
        // 查找部门下的所有用户组
        List<Y9Group> groups = y9GroupRepository.findByParentIdOrderByTabIndexAsc(parentId);
        for (Y9Group y9Group : groups) {
            List<Y9PersonsToGroups> y9PersonsToGroups = y9PersonsToGroupsRepository.findByGroupId(y9Group.getId());
            for (Y9PersonsToGroups p : y9PersonsToGroups) {
                List<Y9Person> groupPersons = this.listByGroupId(p.getGroupId());
                list.addAll(groupPersons);
            }
        }
        return list;
    }

    @Override
    public List<Y9Person> list() {
        return y9PersonRepository.findAll();
    }

    @Override
    public List<Y9Person> listAll() {
        return y9PersonRepository.findAll();
    }

    @Override
    public List<Y9Person> listAllByParentId(String parentId) {
        List<Y9Person> persons = new ArrayList<>();
        recursionAllPersons(parentId, persons);
        return persons;
    }

    @Override
    public List<Y9Person> listByDisabledAndDeletedAndGuidPathLike(String guidPath) {
        return y9PersonRepository.findByDisabledAndGuidPathContaining(false, guidPath);
    }

    @Override
    public List<Y9Person> listByGroupId(String groupId) {
        return y9PersonManager.listByGroupId(groupId);
    }

    @Override
    public List<Y9Person> listByNameLike(String name) {
        return y9PersonRepository.findByNameContaining(name);
    }

    @Override
    public List<Y9Person> listByNameLike(String name, String dnName) {
        return y9PersonRepository.findByNameContainingAndDnContaining(name, dnName);
    }

    @Override
    public List<Y9Person> listByParentId(String parentId) {
        return y9PersonRepository.findByParentIdOrderByTabIndex(parentId);
    }

    @Override
    public List<Y9Person> listByParentIdAndDisabled(String parentId, boolean disabled) {
        return y9PersonRepository.findByDisabledAndParentIdOrderByTabIndex(disabled, parentId);
    }

    @Override
    public List<Y9Person> listByPositionId(String positionId) {
        return y9PersonManager.listByPositionId(positionId);
    }


    /**
     * 根据人员id，获取该人员所有的父节点id列表
     *
     * @param personId 人员id
     * @return {@link List}<{@link String}>
     */
    private List<String> listParentIdByPersonId(String personId) {
        List<String> parentIdList = new ArrayList<>();
        Y9Person person = getById(personId);
        String parentId = person.getParentId();
        if (!Boolean.TRUE.equals(person.getOriginal())) {
            Y9Person originalPerson = getById(person.getOriginalId());
            parentId = originalPerson.getParentId();
            personId = originalPerson.getId();
        }
        parentIdList.add(parentId);
        List<Y9Person> personList = y9PersonRepository.findByOriginalIdAndDisabled(personId, false);
        parentIdList.addAll(personList.stream().map(Y9Person::getParentId).collect(Collectors.toList()));
        return parentIdList;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person modifyPassword(String personId, String newPassword) {
        Y9Person y9Person = null;
        if (StringUtils.isNotEmpty(personId)) {
            y9Person = this.getById(personId);
            if (StringUtils.isNotBlank(newPassword)) {
                y9Person.setPassword(Y9MessageDigest.hashpw(newPassword));
                y9Person = y9PersonManager.save(y9Person);
                Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(y9Person, y9Person));
                Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.orgPersonToPerson(y9Person), Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_PERSON, Y9LoginUserHolder.getTenantId());
                Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "修改密码", "修改" + y9Person.getName() + "的密码");

            }
        }
        return y9Person;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person move(String personId, String parentId) {
        Y9Person originPerson = this.getById(personId);
        Y9Person updatedPerson = new Y9Person();

        Y9BeanUtil.copyProperties(originPerson, updatedPerson);
        updatedPerson.setTabIndex(compositeOrgBaseManager.getMaxSubTabIndex(parentId, OrgTypeEnum.PERSON));
        updatedPerson.setParentId(parentId);
        updatedPerson = this.save(updatedPerson);

        Y9OrgBase parent = compositeOrgBaseManager.getOrgBase(parentId);
        Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.orgPersonToPerson(updatedPerson), Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_PERSON, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "移动人员", updatedPerson.getName() + "移动到" + parent.getName());

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originPerson, updatedPerson));

        return updatedPerson;
    }

    @EventListener
    public void onParentDepartmentDeleted(Y9EntityDeletedEvent<Y9Department> event) {
        Y9Department parentDepartment = event.getEntity();
        // 删除部门时其下人员也要删除
        deleteByParentId(parentDepartment.getId());
    }

    @EventListener
    public void onParentOrganizationDeleted(Y9EntityDeletedEvent<Y9Organization> event) {
        Y9Organization y9Organization = event.getEntity();
        // 删除组织时其下人员也要删除
        deleteByParentId(y9Organization.getId());
    }

    @Transactional(readOnly = false)
    public Y9Person order(String personId, String tabIndex) {
        Y9Person person = this.getById(personId);
        person.setTabIndex(Integer.parseInt(tabIndex));
        Y9Person y9Person = this.save(person);

        Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.orgPersonToPerson(y9Person), Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_PERSON, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.publishMessageOrg(msg);

        return y9Person;
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9OrgBase> order(String[] personIds, String[] tabIndexs) {
        List<Y9OrgBase> personList = new ArrayList<>();
        for (int i = 0; i < personIds.length; i++) {
            personList.add(order(personIds[i], tabIndexs[i]));
        }
        return personList;
    }

    @Override
    public Page<Y9Person> pageByNameLike(int page, int rows, String userName) {
        page = (page < 0) ? 0 : page - 1;
        Pageable pageable = PageRequest.of(page, rows, Sort.by(Sort.Direction.DESC, "guidPath"));
        return y9PersonRepository.findByDisabledAndNameContaining(false, userName, pageable);
    }

    @Override
    public Page<Y9Person> pageByParentId(int page, int rows, String parentId, boolean disabled) {
        page = (page < 0) ? 0 : page - 1;
        Pageable pageable = PageRequest.of(page, rows, Sort.by(Sort.Direction.DESC, "tabIndex"));
        return y9PersonRepository.findByDisabledAndParentId(disabled, parentId, pageable);
    }

    @Override
    public Page<Y9Person> pageByParentId(int page, int rows, String parentId, boolean disabled, String userName) {
        page = (page < 0) ? 0 : page - 1;
        Pageable pageable = PageRequest.of(page, rows, Sort.by(Sort.Direction.DESC, "tabIndex"));
        return y9PersonRepository.findByParentIdAndDisabledAndNameContaining(parentId, disabled, userName, pageable);
    }

    /**
     * 递归获取所有人员
     *
     * @param parentId
     * @param personList
     */
    private void recursionAllPersons(String parentId, List<Y9Person> personList) {
        List<Y9Person> lists = this.getPersonByParentId(parentId);
        personList.addAll(lists);
        List<Y9Department> deptList = y9DepartmentRepository.findByParentIdOrderByTabIndexAsc(parentId);
        for (Y9Department dept : deptList) {
            List<Y9Person> list = this.getPersonByParentId(dept.getId());
            personList.addAll(list);
            recursionAllPersons(dept.getId(), personList);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void resetPassword(String personId) {
        if (StringUtils.isNotEmpty(personId)) {
            Y9Person origPerson = this.getById(personId);
            String password = y9config.getCommon().getDefaultPassword();
            origPerson.setPassword(Y9MessageDigest.hashpw(password));
            Y9Person y9Person = y9PersonManager.save(origPerson);

            Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(origPerson, y9Person));
            Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.orgPersonToPerson(y9Person), Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_PERSON, Y9LoginUserHolder.getTenantId());
            Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "重置密码", "重置" + y9Person.getName() + "的密码");

        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person save(Y9Person person) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        person.setTenantId(tenantId);

        StringBuilder sb = new StringBuilder();
        compositeOrgBaseManager.getGuidPathRecursiveUp(sb, person);
        person.setGuidPath(sb.toString());

        sb = new StringBuilder();
        compositeOrgBaseManager.getOrderedPathRecursiveUp(sb, person);
        person.setOrderedPath(sb.toString());

        if (person.getOriginal() == null) {
            person.setOriginal(Boolean.TRUE);
        }
        return y9PersonManager.save(person);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person saveOrUpdate(Y9Person person, Y9PersonExt ext, String parentId, String[] positionIds, String[] jobIds) {
        Y9OrgBase parent = compositeOrgBaseManager.getOrgBase(parentId);

        person = this.saveOrUpdate(person, ext, parent);

        if (positionIds != null) {
            // 关联已有岗位
            y9PersonsToPositionsManager.addPositions(person.getId(), positionIds);
        }

        if (jobIds != null) {
            // 根据职位初始化新岗位并关联
            String[] newPositionIds = new String[jobIds.length];
            for (int i = 0; i < jobIds.length; i++) {
                String jobId = jobIds[i];
                Y9Position y9Position = new Y9Position();
                y9Position.setJobId(jobId);
                y9Position.setName(jobId);

                Y9Position newPosition = y9PositionManager.saveOrUpdate(y9Position, parent);
                newPositionIds[i] = newPosition.getId();
            }
            y9PersonsToPositionsManager.addPositions(person.getId(), newPositionIds);
        }
        return person;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person saveOrUpdate(Y9Person person, Y9PersonExt personExt, Y9OrgBase parent) {
        String password = y9config.getCommon().getDefaultPassword();
        if (StringUtils.isNotEmpty(person.getId())) {
            Y9Person originPerson = y9PersonManager.findById(person.getId());
            if (null != originPerson) {
                // 判断为更新信息
                Y9Person updatedPerson = new Y9Person();
                Y9BeanUtil.copyProperties(originPerson, updatedPerson);
                Y9BeanUtil.copyProperties(person, updatedPerson);
                updatedPerson.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.PERSON) + updatedPerson.getName() + OrgLevelConsts.SEPARATOR + parent.getDn());
                updatedPerson.setParentId(parent.getId());
                if (StringUtils.isBlank(updatedPerson.getEmail())) {
                    updatedPerson.setEmail(null);
                }

                if (updatedPerson.getOriginal() != null && updatedPerson.getOriginal()) {
                    Y9Person personTemp = getPersonByLoginName(person.getLoginName());
                    if (null != personTemp && !person.getId().equals(personTemp.getId())) {
                        deleteLogically(personTemp.getId());
                    }
                }
                if (Boolean.TRUE.equals(updatedPerson.getOriginal()) && null != personExt) {
                    updatePersonByOriginalId(updatedPerson, personExt);
                }
                updatedPerson = save(updatedPerson);

                Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originPerson, updatedPerson));

                Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.orgPersonToPerson(person), Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_PERSON, Y9LoginUserHolder.getTenantId());
                Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "更新人员信息", "更新" + person.getName());

                if (personExt != null) {
                    y9PersonExtManager.saveOrUpdate(personExt, updatedPerson);
                }

                return updatedPerson;
            } else {
                // 判断为从xml导入的代码并且数据库中没有相应信息,把密码统一设置为defaultPassword
                Integer maxTabIndex = getMaxTabIndex();
                person.setTabIndex(maxTabIndex != null ? maxTabIndex + 1 : 0);
                person.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.PERSON) + person.getName() + OrgLevelConsts.SEPARATOR + parent.getDn());
                person.setOrgType(OrgTypeEnum.PERSON.getEnName());
                person.setVersion(OrgTypeEnum.Y9_VERSION);
                person.setParentId(parent.getId());

                if (StringUtils.isBlank(person.getPassword())) {
                    person.setPassword(Y9MessageDigest.hashpw(y9config.getCommon().getDefaultPassword()));
                }
                if (person.getOriginal() != null && person.getOriginal()) {
                    Y9Person personTemp = getPersonByLoginName(person.getLoginName());
                    if (null != personTemp && !person.getId().equals(personTemp.getId())) {
                        deleteLogically(personTemp.getId());
                    }
                }
                person = save(person);
                if (null != personExt) {
                    y9PersonExtManager.saveOrUpdate(personExt, person);
                }

                Y9Context.publishEvent(new Y9EntityCreatedEvent<>(person));

                Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.orgPersonToPerson(person), Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_PERSON, Y9LoginUserHolder.getTenantId());
                Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "新增人员信息", "新增" + person.getName());

                return person;
            }
        }

        // personId为空则新建
        if (StringUtils.isEmpty(person.getId())) {
            person.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        if (StringUtils.isBlank(person.getEmail())) {
            person.setEmail(null);
        }
        person.setTabIndex(compositeOrgBaseManager.getMaxSubTabIndex(parent.getId(), OrgTypeEnum.PERSON));
        person.setOfficial(1);
        person.setVersion(OrgTypeEnum.Y9_VERSION);
        person.setOrgType(OrgTypeEnum.PERSON.getEnName());
        person.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.PERSON) + person.getName() + OrgLevelConsts.SEPARATOR + parent.getDn());
        person.setDisabled(false);
        person.setParentId(parent.getId());

        // 新增人员时，递归获取父级节点的所有权限，并赋予给当前人员。
        person.setPassword(Y9MessageDigest.hashpw(password));
        person = save(person);
        if (null != personExt) {
            y9PersonExtManager.saveOrUpdate(personExt, person);
        }

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(person));

        Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.orgPersonToPerson(person), Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_PERSON, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "新增人员信息", "新增" + person.getName());

        return person;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person saveOrUpdate4ImpOrg(Y9Person person, Y9PersonExt personExt, Y9OrgBase parent) {
        Y9Person oldperson = y9PersonManager.findById(person.getId());
        if (null != oldperson) {
            // 判断为更新信息
            person.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.PERSON) + person.getName() + OrgLevelConsts.SEPARATOR + parent.getDn());
            Y9BeanUtil.copyProperties(person, oldperson);
            oldperson.setParentId(parent.getId());
            oldperson = save(oldperson);

            if (personExt != null) {
                y9PersonExtManager.saveOrUpdate(personExt, oldperson);
            }
            return oldperson;
        } else {
            // 判断为从xml导入的代码并且数据库中没有相应信息,把密码统一设置为defaultPassword
            if (null == person.getTabIndex()) {
                Integer maxTabIndex = getMaxTabIndex();
                person.setTabIndex(maxTabIndex != null ? maxTabIndex + 1 : 0);
            }
            person.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.PERSON) + person.getName() + OrgLevelConsts.SEPARATOR + parent.getDn());
            person.setOrgType(OrgTypeEnum.PERSON.getEnName());
            person.setVersion(OrgTypeEnum.Y9_VERSION);
            person.setParentId(parent.getId());

            if (StringUtils.isBlank(person.getPassword())) {
                person.setPassword(Y9MessageDigest.hashpw(y9config.getCommon().getDefaultPassword()));
            }
            if (person.getOriginal() != null && person.getOriginal()) {
                Y9Person personTemp = getPersonByLoginName(person.getLoginName());
                if (null != personTemp && !person.getId().equals(personTemp.getId())) {
                    deleteLogically(personTemp.getId());
                }
            }
            person = save(person);
            if (personExt != null) {
                y9PersonExtManager.saveOrUpdate(personExt, person);
            }
            return person;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person saveProperties(String personId, String properties) {
        Y9Person person = this.getById(personId);
        person.setProperties(properties);
        Y9Person y9Person = y9PersonManager.save(person);

        Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.orgPersonToPerson(y9Person), Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_PERSON, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.publishMessageOrg(msg);

        return y9Person;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Y9Person> search(String whereClause) {
        EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);
        Assert.notNull(em, "EntityManager must not be null");
        Query query = null;
        if (whereClause == null || "".equals(whereClause.trim())) {
            query = em.createNativeQuery(" select * from Y9_ORG_PERSON ", Y9Person.class);

        } else {
            query = em.createNativeQuery(" select * from Y9_ORG_PERSON where " + whereClause, Y9Person.class);
        }
        return query.getResultList();
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

            Y9OrgBase parent = compositeOrgBaseManager.getOrgBase(person.getParentId());
            person.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.PERSON) + person.getName() + OrgLevelConsts.SEPARATOR + parent.getDn());

            y9PersonManager.save(person);

            Y9PersonExt ext = y9PersonExtManager.findByPersonId(person.getId());
            if (ext == null) {
                ext = new Y9PersonExt();
            }
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


    @Override
    @Transactional(readOnly = false)
    public Y9Person updateTabIndex(String id, int tabIndex) {
        Y9Person person = this.getById(id);
        person.setTabIndex(tabIndex);
        person = this.save(person);

        Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.orgPersonToPerson(person), Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_PERSON_TABINDEX, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "更新人员排序号", person.getName() + "的排序号更新为" + tabIndex);

        return person;
    }

    @Override
    public List<Y9Person> listByIdTypeAndIdNum(String idType, String idNum) {
        List<Y9PersonExt> y9PersonExtList = y9PersonExtManager.listByIdTypeAndIdNum(idType, idNum);
        List<Y9Person> y9PersonList = new ArrayList<>();
        for (Y9PersonExt ext : y9PersonExtList) {
            Y9Person y9Person = this.findById(ext.getPersonId());
            if (y9Person != null) {
                y9PersonList.add(y9Person);
            }
        }
        return y9PersonList;
    }

    @Override
    public List<Y9OrgBase> listParents(String personId) {
        List<String> parentIds = this.listParentIdByPersonId(personId);
        List<Y9OrgBase> parentList = new ArrayList<>();
        for (String parentId : parentIds) {
            Y9OrgBase parent = compositeOrgBaseManager.getOrgBase(parentId);
            parentList.add(parent);
        }
        return parentList;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person changeWeixinId(String personId, String weixinId) {
        Y9Person y9Person = this.getById(personId);
        y9Person.setWeixinId(weixinId);
        y9Person = this.save(y9Person);
        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(y9Person, y9Person));
        return y9Person;
    }
}
