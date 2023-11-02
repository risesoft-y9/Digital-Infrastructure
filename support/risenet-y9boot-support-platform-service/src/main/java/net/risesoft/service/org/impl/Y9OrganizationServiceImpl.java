package net.risesoft.service.org.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.risesoft.consts.OrgLevelConsts;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Organization;
import net.risesoft.enums.AuthorizationPrincipalTypeEnum;
import net.risesoft.enums.OrgTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9OrganizationManager;
import net.risesoft.model.Organization;
import net.risesoft.repository.Y9OrganizationRepository;
import net.risesoft.repository.permission.Y9AuthorizationRepository;
import net.risesoft.repository.relation.Y9OrgBasesToRolesRepository;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.util.Y9PublishServiceUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
public class Y9OrganizationServiceImpl implements Y9OrganizationService {

    private final Y9OrganizationRepository y9OrganizationRepository;
    private final Y9OrgBasesToRolesRepository y9OrgBasesToRolesRepository;
    private final Y9AuthorizationRepository y9AuthorizationRepository;

    private final EntityManagerFactory entityManagerFactory;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9OrganizationManager y9OrganizationManager;

    public Y9OrganizationServiceImpl(Y9OrganizationRepository y9OrganizationRepository,
        @Qualifier("rsTenantEntityManagerFactory") EntityManagerFactory entityManagerFactory,
        CompositeOrgBaseManager compositeOrgBaseManager, Y9OrgBasesToRolesRepository y9OrgBasesToRolesRepository,
        Y9AuthorizationRepository y9AuthorizationRepository, Y9OrganizationManager y9OrganizationManager) {
        this.y9OrganizationRepository = y9OrganizationRepository;
        this.entityManagerFactory = entityManagerFactory;
        this.compositeOrgBaseManager = compositeOrgBaseManager;
        this.y9OrgBasesToRolesRepository = y9OrgBasesToRolesRepository;
        this.y9AuthorizationRepository = y9AuthorizationRepository;
        this.y9OrganizationManager = y9OrganizationManager;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Organization create(String organizationName, Boolean virtual) {
        Y9Organization y9Organization = new Y9Organization();
        y9Organization.setName(organizationName);
        y9Organization.setVirtual(virtual);
        return this.saveOrUpdate(y9Organization);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {

        Y9Organization org = this.getById(id);

        Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(org, Organization.class),
            Y9OrgEventConst.RISEORGEVENT_TYPE_DELETE_ORGANIZATION, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "删除组织机构", "删除 " + org.getName());

        y9OrganizationManager.delete(org);

        // 删除组织关联数据
        y9OrgBasesToRolesRepository.deleteByOrgId(org.getId());
        y9AuthorizationRepository.deleteByPrincipalIdAndPrincipalType(org.getId(),
            AuthorizationPrincipalTypeEnum.DEPARTMENT.getValue());

        // 发布事件，程序内部监听处理相关业务
        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(org));
    }

    @Override
    public boolean existsById(String id) {
        return y9OrganizationRepository.existsById(id);
    }

    @Override
    public Optional<Y9Organization> findById(String id) {
        return y9OrganizationManager.findById(id);
    }

    @Override
    public Y9Organization getById(String id) {
        return y9OrganizationManager.getById(id);
    }

    @Override
    public Integer getMaxTabIndex() {
        return y9OrganizationRepository.findTopByOrderByTabIndexDesc().map(Y9OrgBase::getTabIndex).orElse(0);
    }

    @Override
    public List<Y9Organization> list() {
        return y9OrganizationRepository.findByOrderByTabIndexAsc();
    }

    @Override
    public List<Y9Organization> list(Boolean virtual) {
        if (null == virtual) {
            return list();
        }
        return y9OrganizationRepository.findByVirtualOrderByTabIndexAsc(virtual);
    }

    @Override
    public List<Y9Organization> listByDn(String dn) {
        return y9OrganizationRepository.findByDn(dn);
    }

    @Override
    public List<Y9Organization> listByTenantId(String tenantId) {
        return y9OrganizationRepository.findByTenantIdOrderByTabIndexAsc(tenantId);
    }

    @Transactional(readOnly = false)
    public Y9Organization saveOrder(String orgId, int tabIndex) {
        Y9Organization org = this.getById(orgId);
        org.setTabIndex(tabIndex);
        org = y9OrganizationManager.save(org);

        Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(org, Organization.class),
            Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_ORGANIZATION, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.publishMessageOrg(msg);

        return org;
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9Organization> saveOrder(List<String> orgIds) {
        List<Y9Organization> orgList = new ArrayList<>();

        int tabIndex = 0;
        for (String orgId : orgIds) {
            orgList.add(saveOrder(orgId, tabIndex++));
        }

        return orgList;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Organization saveOrUpdate(Y9Organization org) {
        if (StringUtils.isNotEmpty(org.getId())) {
            Optional<Y9Organization> y9OrganizationOptional = y9OrganizationManager.findById(org.getId());
            if (y9OrganizationOptional.isPresent()) {
                Y9Organization oldOrg = y9OrganizationOptional.get();

                boolean renamed = Y9OrgUtil.isRenamed(org, oldOrg);
                Y9BeanUtil.copyProperties(org, oldOrg);
                oldOrg.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.ORGANIZATION) + org.getName());
                oldOrg.setGuidPath(org.getId());
                oldOrg.setTenantId(Y9LoginUserHolder.getTenantId());
                oldOrg = y9OrganizationManager.save(oldOrg);

                if (renamed) {
                    // 是否需要递归DN
                    compositeOrgBaseManager.recursivelyUpdateProperties(oldOrg);
                }

                Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(oldOrg, Organization.class),
                    Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_ORGANIZATION, Y9LoginUserHolder.getTenantId());
                Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "更新组织机构", "更新" + org.getName());

                return oldOrg;
            } else {
                Integer maxTabIndex = getMaxTabIndex();
                org.setTabIndex(maxTabIndex != null ? maxTabIndex + 1 : 0);
                org.setOrgType(OrgTypeEnum.ORGANIZATION.getEnName());
                org.setVersion(OrgTypeEnum.Y9_VERSION);
                org.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.ORGANIZATION) + org.getName());
                org.setGuidPath(org.getId());
                org.setTenantId(Y9LoginUserHolder.getTenantId());
                org = y9OrganizationManager.save(org);

                Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(org, Organization.class),
                    Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_ORGANIZATION, Y9LoginUserHolder.getTenantId());
                Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "新增组织机构", "新增" + org.getName());

                return org;
            }
        }

        org.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        Integer maxTabIndex = getMaxTabIndex();
        org.setTabIndex(maxTabIndex != null ? maxTabIndex + 1 : 0);
        org.setOrgType(OrgTypeEnum.ORGANIZATION.getEnName());
        org.setVersion(OrgTypeEnum.Y9_VERSION);
        org.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.ORGANIZATION) + org.getName());
        org.setGuidPath(org.getId());
        org.setTenantId(Y9LoginUserHolder.getTenantId());
        org = y9OrganizationManager.save(org);

        Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(org, Organization.class),
            Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_ORGANIZATION, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "新增组织机构", "新增" + org.getName());

        return org;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Organization saveProperties(String orgId, String properties) {
        Y9Organization org = this.getById(orgId);
        org.setProperties(properties);
        org = y9OrganizationManager.save(org);

        Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(org, Organization.class),
            Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_ORGANIZATION, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.publishMessageOrg(msg);

        return org;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Y9Organization> search(String whereClause) {
        EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);
        Assert.notNull(em, "EntityManager must not be null");
        Query query = null;
        if (whereClause == null || "".equals(whereClause.trim())) {
            query = em.createNativeQuery("SELECT * FROM Y9_ORG_ORGANIZATION ", Y9Organization.class);

        } else {
            query =
                em.createNativeQuery("SELECT * FROM Y9_ORG_ORGANIZATION WHERE " + whereClause, Y9Organization.class);
        }
        return query.getResultList();
    }

}