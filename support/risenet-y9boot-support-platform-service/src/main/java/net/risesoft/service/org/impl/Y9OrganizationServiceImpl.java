package net.risesoft.service.org.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.consts.OrgLevelConsts;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Organization;
import net.risesoft.enums.AuthorizationPrincipalTypeEnum;
import net.risesoft.enums.OrgTypeEnum;
import net.risesoft.exception.OrganizationErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.Y9OrgBaseManager;
import net.risesoft.model.Organization;
import net.risesoft.repository.Y9OrganizationRepository;
import net.risesoft.repository.permission.Y9AuthorizationRepository;
import net.risesoft.repository.relation.Y9OrgBasesToRolesRepository;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.util.Y9PublishServiceUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
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
@CacheConfig(cacheNames = CacheNameConsts.ORG_ORGANIZATION)
@Service
public class Y9OrganizationServiceImpl implements Y9OrganizationService {

    private final Y9OrganizationRepository y9OrganizationRepository;
    private final Y9OrgBasesToRolesRepository y9OrgBasesToRolesRepository;
    private final Y9AuthorizationRepository y9AuthorizationRepository;

    private final EntityManagerFactory entityManagerFactory;

    private final Y9OrgBaseManager y9OrgBaseManager;

    public Y9OrganizationServiceImpl(Y9OrganizationRepository y9OrganizationRepository, @Qualifier("rsTenantEntityManagerFactory") EntityManagerFactory entityManagerFactory, Y9OrgBaseManager y9OrgBaseManager, Y9OrgBasesToRolesRepository y9OrgBasesToRolesRepository,
        Y9AuthorizationRepository y9AuthorizationRepository) {
        this.y9OrganizationRepository = y9OrganizationRepository;
        this.entityManagerFactory = entityManagerFactory;
        this.y9OrgBaseManager = y9OrgBaseManager;
        this.y9OrgBasesToRolesRepository = y9OrgBasesToRolesRepository;
        this.y9AuthorizationRepository = y9AuthorizationRepository;
    }

    @Override
    @Transactional(readOnly = false)
    public void createOrganization(String tenantId, String organizationId, String organizationName, Boolean virtual) {
        if (!this.existsById(organizationId)) {
            Y9Organization y9Organization = new Y9Organization();
            y9Organization.setId(organizationId);
            y9Organization.setTenantId(tenantId);
            y9Organization.setName(organizationName);
            y9Organization.setEnName("org");
            y9Organization.setDn("o=" + organizationName);
            y9Organization.setOrgType(OrgTypeEnum.ORGANIZATION.getEnName());
            y9Organization.setGuidPath(organizationId);
            y9Organization.setTabIndex(10000);
            y9Organization.setVirtual(virtual);
            saveOrUpdate(y9Organization);
        }
    }

    @Override
    @CacheEvict(key = "#id")
    @Transactional(readOnly = false)
    public void delete(String id) {

        Y9Organization org = this.getById(id);
        // 发布事件，程序内部监听处理相关业务
        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(org));

        Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(org, Organization.class), Y9OrgEventConst.RISEORGEVENT_TYPE_DELETE_ORGANIZATION, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "删除组织机构", "删除 " + org.getName());

        y9OrganizationRepository.delete(org);

        // 删除组织关联数据
        y9OrgBasesToRolesRepository.deleteByOrgId(org.getId());
        y9AuthorizationRepository.deleteByPrincipalIdAndPrincipalType(org.getId(), AuthorizationPrincipalTypeEnum.DEPARTMENT.getValue());
    }

    @Override
    public boolean existsById(String id) {
        return y9OrganizationRepository.existsById(id);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Organization findById(String id) {
        return y9OrganizationRepository.findById(id).orElse(null);
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Organization getById(String id) {
        return y9OrganizationRepository.findById(id).orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrganizationErrorCodeEnum.ORGANIZATION_NOT_FOUND, id));
    }

    @Override
    public Y9Organization getByOrgBaseId(String orgBaseId) {
        Y9OrgBase y9OrgBase = y9OrgBaseManager.getOrgBase(orgBaseId);
        if (y9OrgBase.getOrgType().equals(OrgTypeEnum.ORGANIZATION.getEnName())) {
            return this.getById(orgBaseId);
        } else {
            do {
                y9OrgBase = y9OrgBaseManager.getOrgBase(y9OrgBase.getParentId());
            } while (!y9OrgBase.getOrgType().equals(OrgTypeEnum.ORGANIZATION.getEnName()));
            return this.getById(y9OrgBase.getId());
        }
    }

    @Override
    public Integer getMaxTabIndex() {
        Y9Organization org = y9OrganizationRepository.findTopByOrderByTabIndexDesc();
        if (org != null) {
            return org.getTabIndex();
        }
        return 0;
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

    @CacheEvict(key = "#orgId")
    @Transactional(readOnly = false)
    public Y9Organization saveOrder(String orgId, int tabIndex) {
        Y9Organization org = this.getById(orgId);
        org.setTabIndex(tabIndex);
        org = y9OrganizationRepository.save(org);

        Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(org, Organization.class), Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_ORGANIZATION, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.publishMessageOrg(msg);

        return org;
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9Organization> saveOrder(String[] orgIds) {
        List<Y9Organization> orgList = new ArrayList<>();
        for (int i = 0; i < orgIds.length; i++) {
            Y9Organization newOrganization = saveOrder(orgIds[i], i);
            orgList.add(newOrganization);
        }
        return orgList;
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#org.id", condition = "#org.id!=null")
    public Y9Organization saveOrUpdate(Y9Organization org) {
        if (StringUtils.isNotEmpty(org.getId())) {
            Y9Organization oldOrg = y9OrganizationRepository.findById(org.getId()).orElse(null);
            if (oldOrg != null) {
                // 是否需要递归DN
                boolean recursionDn = !org.getName().equals(oldOrg.getName());

                Y9BeanUtil.copyProperties(org, oldOrg);
                oldOrg.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.ORGANIZATION) + org.getName());
                oldOrg.setGuidPath(org.getId());
                oldOrg.setTenantId(Y9LoginUserHolder.getTenantId());
                oldOrg = y9OrganizationRepository.save(oldOrg);
                if (recursionDn) {
                    y9OrgBaseManager.recursivelyUpdateProperties(oldOrg);
                }

                Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(oldOrg, Organization.class), Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_ORGANIZATION, Y9LoginUserHolder.getTenantId());
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
                org = y9OrganizationRepository.save(org);

                Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(org, Organization.class), Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_ORGANIZATION, Y9LoginUserHolder.getTenantId());
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
        org = y9OrganizationRepository.save(org);

        Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(org, Organization.class), Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_ORGANIZATION, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "新增组织机构", "新增" + org.getName());

        return org;
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#orgId")
    public Y9Organization saveProperties(String orgId, String properties) {
        Y9Organization org = this.getById(orgId);
        org.setProperties(properties);
        org = y9OrganizationRepository.save(org);

        Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(org, Organization.class), Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_ORGANIZATION, Y9LoginUserHolder.getTenantId());
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
            query = em.createNativeQuery("SELECT * FROM Y9_ORG_ORGANIZATION WHERE " + whereClause, Y9Organization.class);
        }
        return query.getResultList();
    }

}