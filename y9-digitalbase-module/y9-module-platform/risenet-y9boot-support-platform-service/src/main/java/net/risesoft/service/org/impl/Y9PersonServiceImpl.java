package net.risesoft.service.org.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9PersonExt;
import net.risesoft.entity.Y9Position;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9PersonExtManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.manager.org.Y9PositionManager;
import net.risesoft.manager.relation.Y9PersonsToPositionsManager;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.repository.Y9PersonRepository;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.setting.Y9SettingService;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9Assert;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9.util.signing.Y9MessageDigest;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
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
    @Transactional(readOnly = false)
    public List<Y9Person> addPersons(String parentId, List<String> personIds) {
        List<Y9Person> personList = new ArrayList<>();
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(parentId);
        for (String originalId : personIds) {
            Y9Person originalPerson = getById(originalId);
            if (StringUtils.isNotBlank(originalPerson.getOriginalId())) {
                originalId = originalPerson.getOriginalId();
                originalPerson = getById(originalId);
            }
            Optional<Y9Person> y9PersonOptional = y9PersonRepository.findByOriginalIdAndParentId(originalId, parentId);
            if (y9PersonOptional.isPresent()) {
                Y9Person oldperson = y9PersonOptional.get();
                oldperson.setDn(Y9OrgUtil.buildDn(OrgTypeEnum.PERSON, oldperson.getName(), parent.getDn()));
                oldperson.setDisabled(Boolean.FALSE);
                oldperson.setName(originalPerson.getName());
                oldperson.setLoginName(originalPerson.getLoginName());
                oldperson.setEmail(originalPerson.getEmail());
                oldperson.setMobile(originalPerson.getMobile());
                oldperson.setDescription(originalPerson.getDescription());
                oldperson = save(oldperson);
                personList.add(oldperson);
                continue;
            }
            Y9Person person = new Y9Person();
            Y9BeanUtil.copyProperties(originalPerson, person);
            person.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            person.setOriginal(Boolean.FALSE);
            person.setOriginalId(originalId);
            person.setParentId(parentId);
            person.setTabIndex(compositeOrgBaseManager.getNextSubTabIndex(parentId));
            person.setDn(Y9OrgUtil.buildDn(OrgTypeEnum.PERSON, person.getName(), parent.getDn()));

            final Y9Person savedPerson = save(person);

            Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedPerson));

            personList.add(savedPerson);
        }
        return personList;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person changeDisabled(String id) {
        final Y9Person person = this.getById(id);

        Y9Person updatedPerson = Y9ModelConvertUtil.convert(person, Y9Person.class);
        boolean disabled = !updatedPerson.getDisabled();
        updatedPerson.setDisabled(disabled);
        return y9PersonManager.saveOrUpdate(updatedPerson, null);
    }

    @Override
    public long countByGuidPathLikeAndDisabledAndDeletedFalse(String guidPath) {
        return y9PersonRepository.countByDisabledAndGuidPathContaining(Boolean.FALSE, guidPath);
    }

    @Override
    public long countByParentId(String parentId) {
        return y9PersonRepository.countByParentId(parentId);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person create(String parentId, String name, String loginName, String mobile) {
        Optional<Y9Person> y9PersonOptional = y9PersonRepository.findByLoginNameAndOriginalTrue(loginName);
        Y9Person y9Person = y9PersonOptional.orElse(new Y9Person());
        y9Person.setParentId(parentId);
        y9Person.setName(name);
        y9Person.setLoginName(loginName);
        y9Person.setMobile(mobile);
        return this.saveOrUpdate(y9Person, new Y9PersonExt());
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(List<String> ids) {
        for (String id : ids) {
            delete(id);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        Y9Person y9Person = this.getById(id);

        // 删除人员关联数据
        y9PersonsToPositionsManager.deleteByPersonId(id);
        y9PersonManager.delete(y9Person);

        // 发布事件，程序内部监听处理相关业务
        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Person));
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByParentId(String parentId) {
        List<Y9Person> personList = listByParentId(parentId, null);
        for (Y9Person person : personList) {
            delete(person.getId());
        }
    }

    @Override
    public boolean existsById(String id) {
        return y9PersonRepository.existsById(id);
    }

    @Override
    public Optional<Y9Person> findById(String id) {
        return y9PersonManager.findById(id);
    }

    @Override
    public Optional<Y9Person> findByLoginName(final String loginName) {
        return y9PersonRepository.findByLoginNameAndOriginalTrue(loginName);
    }

    @Override
    public Optional<Y9Person> findByCaId(final String caId) {
        return y9PersonRepository.findByCaidAndOriginalTrue(caId);
    }

    @Override
    public List<String> findIdByGuidPathStartingWith(String guidPath) {
        return y9PersonRepository.findIdByGuidPathStartingWith(guidPath);
    }

    @Override
    @Transactional(readOnly = true)
    public Y9Person getById(String id) {
        return y9PersonManager.getById(id);
    }

    @Override
    public Optional<Y9Person> getByLoginNameAndParentId(String loginName, String parentId) {
        return y9PersonRepository.findByLoginNameAndParentId(loginName, parentId);
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

    @Override
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
    public List<Y9Person> list(Boolean disabled) {
        if (disabled == null) {
            return y9PersonRepository.findAll();
        } else {
            return y9PersonRepository.findByDisabled(disabled);
        }
    }

    @Override
    public List<Y9Person> listAll() {
        return y9PersonRepository.findAll();
    }

    @Override
    public List<Y9Person> listByDisabledAndDeletedAndGuidPathLike(String guidPath) {
        return y9PersonRepository.findByDisabledAndGuidPathContaining(Boolean.FALSE, guidPath);
    }

    @Override
    public List<Y9Person> listByGroupId(String groupId, Boolean disabled) {
        return y9PersonManager.listByGroupId(groupId, disabled);
    }

    @Override
    public List<Y9Person> listByIdTypeAndIdNum(String idType, String idNum, Boolean disabled) {
        List<Y9PersonExt> y9PersonExtList = y9PersonExtManager.listByIdTypeAndIdNum(idType, idNum);
        List<Y9Person> y9PersonList = new ArrayList<>();
        for (Y9PersonExt ext : y9PersonExtList) {
            Optional<Y9Person> y9PersonOptional = this.findById(ext.getPersonId());
            if (y9PersonOptional.isPresent()) {
                Y9Person y9Person = y9PersonOptional.get();
                if (disabled == null || disabled.equals(y9Person.getDisabled())) {
                    y9PersonList.add(y9Person);
                }
            }
        }
        return y9PersonList;
    }

    @Override
    public List<Y9Person> listByNameLike(String name, Boolean disabled) {
        if (disabled == null) {
            return y9PersonRepository.findByNameContaining(name);
        } else {
            return y9PersonRepository.findByNameContainingAndDisabled(name, disabled);
        }
    }

    @Override
    public List<Y9Person> listByParentId(String parentId, Boolean disabled) {
        return y9PersonManager.listByParentId(parentId, disabled);
    }

    @Override
    public List<Y9Person> listByPositionId(String positionId, Boolean disabled) {
        return y9PersonManager.listByPositionId(positionId, disabled);
    }

    @Override
    public List<Y9OrgBase> listParents(String personId) {
        List<String> parentIds = this.listParentIdByPersonId(personId);
        List<Y9OrgBase> parentList = new ArrayList<>();
        for (String parentId : parentIds) {
            Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(parentId);
            parentList.add(parent);
        }
        return parentList;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person modifyPassword(String personId, String oldPassword, String newPassword) {
        Y9Person person = this.getById(personId);

        if (StringUtils.isNotBlank(oldPassword)) {
            // 兼容旧接口，无 oldPassword
            Y9Assert.isTrue(Y9MessageDigest.bcryptMatch(oldPassword, person.getPassword()),
                OrgUnitErrorCodeEnum.OLD_PASSWORD_IS_INCORRECT);
        }

        Y9Person updatedPerson = Y9ModelConvertUtil.convert(person, Y9Person.class);
        updatedPerson.setPassword(Y9MessageDigest.bcrypt(newPassword));
        return y9PersonManager.saveOrUpdate(updatedPerson, null);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person move(String personId, String parentId) {
        final Y9Person person = this.getById(personId);

        Y9Person updatedPerson = Y9ModelConvertUtil.convert(person, Y9Person.class);
        updatedPerson.setParentId(parentId);
        updatedPerson.setTabIndex(compositeOrgBaseManager.getNextSubTabIndex(parentId));
        return this.saveOrUpdate(updatedPerson, null);
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9OrgBase> order(List<String> personIds) {
        List<Y9OrgBase> personList = new ArrayList<>();

        int tabIndex = 0;
        for (String personId : personIds) {
            personList.add(y9PersonManager.updateTabIndex(personId, tabIndex++));
        }
        return personList;
    }

    @Override
    public Page<Y9Person> pageByNameLike(String name, Y9PageQuery pageQuery) {
        Pageable pageable =
            PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by(Sort.Direction.DESC, "guidPath"));
        return y9PersonRepository.findByDisabledAndNameContaining(Boolean.FALSE, name, pageable);
    }

    @Override
    public Page<Y9Person> pageByParentId(String parentId, boolean disabled, String name, Y9PageQuery pageQuery) {
        Pageable pageable =
            PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by(Sort.Direction.DESC, "tabIndex"));
        return y9PersonRepository.findByParentIdAndDisabledAndNameContaining(parentId, disabled, name, pageable);
    }

    @Override
    public Page<Y9Person> pageByParentId(String parentId, boolean disabled, Y9PageQuery pageQuery) {
        Pageable pageable =
            PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by(Sort.Direction.DESC, "tabIndex"));
        return y9PersonRepository.findByDisabledAndParentId(disabled, parentId, pageable);
    }

    @Override
    @Transactional(readOnly = false)
    public void resetDefaultPassword(String personId) {
        final Y9Person person = this.getById(personId);

        Y9Person updatedPerson = Y9ModelConvertUtil.convert(person, Y9Person.class);
        String password = y9SettingService.getTenantSetting().getUserDefaultPassword();
        updatedPerson.setPassword(Y9MessageDigest.bcrypt(password));
        y9PersonManager.saveOrUpdate(updatedPerson, null);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person save(Y9Person person) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        person.setTenantId(tenantId);
        person.setGuidPath(compositeOrgBaseManager.buildGuidPath(person));
        person.setOrderedPath(compositeOrgBaseManager.buildOrderedPath(person));

        if (person.getOriginal() == null) {
            person.setOriginal(Boolean.TRUE);
        }
        return y9PersonManager.save(person);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person saveAvator(String personId, String avatorUrl) {
        final Y9Person person = this.getById(personId);

        Y9Person updatedPerson = Y9ModelConvertUtil.convert(person, Y9Person.class);
        updatedPerson.setAvator(avatorUrl);
        return this.saveOrUpdate(updatedPerson, null);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person saveOrUpdate(Y9Person person, Y9PersonExt personExt) {
        return y9PersonManager.saveOrUpdate(person, personExt);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person saveOrUpdate(Y9Person person, Y9PersonExt ext, List<String> positionIds, List<String> jobIds) {
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

                Y9Position newPosition = y9PositionManager.saveOrUpdate(y9Position);
                newPositionIdList.add(newPosition.getId());
            }
            y9PersonsToPositionsManager.addPositions(person.getId(), newPositionIdList);
        }
        return person;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person saveProperties(String personId, String properties) {
        return y9PersonManager.saveProperties(personId, properties);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person saveWeixinId(String personId, String weixinId) {
        Y9Person person = this.getById(personId);

        Y9Person updatedPerson = Y9ModelConvertUtil.convert(person, Y9Person.class);
        updatedPerson.setWeixinId(weixinId);
        return y9PersonManager.saveOrUpdate(updatedPerson, null);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Person updateTabIndex(String id, int tabIndex) {
        return y9PersonManager.updateTabIndex(id, tabIndex);
    }

    @Override
    public Page<Y9Person> page(List<String> orgIdList, Boolean disabled, Y9PageQuery pageQuery) {
        Pageable pageable =
            PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by(Sort.Direction.ASC, "createTime"));
        Specification<Y9Person> spec = new Specification<>() {
            private static final long serialVersionUID = -6506792884620973450L;

            @Override
            public Predicate toPredicate(Root<Y9Person> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> orList = new ArrayList<>();
                for (String id : orgIdList) {
                    orList.add(cb.like(root.get("guidPath").as(String.class), "%" + id + "%"));
                }
                Predicate preOr = cb.or(orList.toArray(new Predicate[orList.size()]));

                List<Predicate> predicates = new ArrayList<>();
                if (disabled != null) {
                    predicates.add(cb.equal(root.get("disabled").as(Boolean.class), disabled));
                }
                Predicate pre_and = cb.and(predicates.toArray(new Predicate[predicates.size()]));

                return criteriaQuery.where(pre_and, preOr).getRestriction();
            }
        };
        return y9PersonRepository.findAll(spec, pageable);
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
        List<Y9Person> personList = y9PersonRepository.findByOriginalIdAndDisabled(personId, Boolean.FALSE);
        parentIdList.addAll(personList.stream().map(Y9Person::getParentId).collect(Collectors.toList()));
        return parentIdList;
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onParentDepartmentDeleted(Y9EntityDeletedEvent<Y9Department> event) {
        Y9Department parentDepartment = event.getEntity();
        // 删除部门时其下人员也要删除
        deleteByParentId(parentDepartment.getId());
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onParentOrganizationDeleted(Y9EntityDeletedEvent<Y9Organization> event) {
        Y9Organization y9Organization = event.getEntity();
        // 删除组织时其下人员也要删除
        deleteByParentId(y9Organization.getId());
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onParentUpdated(Y9EntityUpdatedEvent<? extends Y9OrgBase> event) {
        Y9OrgBase originOrgBase = event.getOriginEntity();
        Y9OrgBase updatedOrgBase = event.getUpdatedEntity();

        if (Y9OrgUtil.isCurrentOrAncestorChanged(originOrgBase, updatedOrgBase)) {
            List<Y9Person> personList = y9PersonRepository.findByParentIdOrderByTabIndex(updatedOrgBase.getId());
            for (Y9Person person : personList) {
                this.saveOrUpdate(person, null);
            }
        }
    }
}
