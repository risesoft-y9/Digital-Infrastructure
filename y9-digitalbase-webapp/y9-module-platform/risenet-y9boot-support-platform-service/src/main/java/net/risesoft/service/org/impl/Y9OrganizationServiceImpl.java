package net.risesoft.service.org.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.consts.OrgLevelConsts;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Organization;
import net.risesoft.enums.platform.AuthorizationPrincipalTypeEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9OrganizationManager;
import net.risesoft.model.platform.Organization;
import net.risesoft.repository.Y9OrganizationRepository;
import net.risesoft.repository.permission.Y9AuthorizationRepository;
import net.risesoft.repository.relation.Y9OrgBasesToRolesRepository;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.util.Y9PublishServiceUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.constant.Y9OrgEventTypeConst;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
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

    private final Y9OrganizationManager y9OrganizationManager;
    private final CompositeOrgBaseManager compositeOrgBaseManager;

    public Y9OrganizationServiceImpl(Y9OrganizationRepository y9OrganizationRepository,
        @Qualifier("rsTenantEntityManagerFactory") EntityManagerFactory entityManagerFactory,
        Y9OrgBasesToRolesRepository y9OrgBasesToRolesRepository, Y9AuthorizationRepository y9AuthorizationRepository,
        Y9OrganizationManager y9OrganizationManager, CompositeOrgBaseManager compositeOrgBaseManager) {
        this.y9OrganizationRepository = y9OrganizationRepository;
        this.entityManagerFactory = entityManagerFactory;
        this.y9OrgBasesToRolesRepository = y9OrgBasesToRolesRepository;
        this.y9AuthorizationRepository = y9AuthorizationRepository;
        this.y9OrganizationManager = y9OrganizationManager;
        this.compositeOrgBaseManager = compositeOrgBaseManager;
    }

    @Override
    public Y9Organization changeDisabled(String id) {

        // 检查所有子节点是否都禁用了，只有所有子节点都禁用了，当前部门才能禁用
        compositeOrgBaseManager.checkAllDecendantsDisabled(id);

        Y9Organization y9Group = this.getById(id);
        Boolean isDisabled = !y9Group.getDisabled();
        y9Group.setDisabled(isDisabled);
        final Y9Organization savedOrganization = y9OrganizationManager.save(y9Group);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                String event = isDisabled ? "禁用" : "启用";
                Y9MessageOrg<Organization> msg =
                    new Y9MessageOrg<>(Y9ModelConvertUtil.convert(savedOrganization, Organization.class),
                        Y9OrgEventTypeConst.ORGANIZATION_UPDATE, Y9LoginUserHolder.getTenantId());
                Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, event + "组织机构",
                    event + savedOrganization.getName());
            }
        });
        return savedOrganization;
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

        // 删除组织关联数据
        y9OrgBasesToRolesRepository.deleteByOrgId(org.getId());
        y9AuthorizationRepository.deleteByPrincipalIdAndPrincipalType(org.getId(),
            AuthorizationPrincipalTypeEnum.DEPARTMENT);

        y9OrganizationManager.delete(org);

        // 发布事件，程序内部监听处理相关业务
        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(org));

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                Y9MessageOrg<Organization> msg = new Y9MessageOrg<>(Y9ModelConvertUtil.convert(org, Organization.class),
                    Y9OrgEventTypeConst.ORGANIZATION_DELETE, Y9LoginUserHolder.getTenantId());
                Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "删除组织机构", "删除 " + org.getName());
            }
        });
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
    public List<Y9Organization> list(Boolean virtual, Boolean disabled) {
        if (null == virtual) {
            return list(disabled);
        }
        if (null == disabled) {
            return y9OrganizationRepository.findByVirtualOrderByTabIndexAsc(virtual);
        } else {
            return y9OrganizationRepository.findByVirtualAndDisabledOrderByTabIndexAsc(virtual, disabled);
        }
    }

    private List<Y9Organization> list(Boolean disabled) {
        if (disabled == null) {
            return y9OrganizationRepository.findByOrderByTabIndexAsc();
        } else {
            return y9OrganizationRepository.findByDisabledOrderByTabIndexAsc(disabled);
        }
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
        final Y9Organization savedOrganization = y9OrganizationManager.save(org);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                Y9MessageOrg<Organization> msg =
                    new Y9MessageOrg<>(Y9ModelConvertUtil.convert(savedOrganization, Organization.class),
                        Y9OrgEventTypeConst.ORGANIZATION_UPDATE, Y9LoginUserHolder.getTenantId());
                Y9PublishServiceUtil.publishMessageOrg(msg);
            }
        });

        return savedOrganization;
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
    public Y9Organization saveOrUpdate(Y9Organization organization) {
        if (StringUtils.isNotEmpty(organization.getId())) {
            Optional<Y9Organization> y9OrganizationOptional = y9OrganizationManager.findById(organization.getId());
            if (y9OrganizationOptional.isPresent()) {
                Y9Organization originY9Organization = new Y9Organization();
                Y9Organization updatedY9Organization = y9OrganizationOptional.get();
                Y9BeanUtil.copyProperties(updatedY9Organization, originY9Organization);

                Y9BeanUtil.copyProperties(organization, updatedY9Organization);

                updatedY9Organization
                    .setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.ORGANIZATION) + updatedY9Organization.getName());
                updatedY9Organization.setGuidPath(updatedY9Organization.getId());
                updatedY9Organization.setTenantId(Y9LoginUserHolder.getTenantId());
                final Y9Organization savedOrganization = y9OrganizationManager.save(updatedY9Organization);

                Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originY9Organization, savedOrganization));

                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        Y9MessageOrg<Organization> msg =
                            new Y9MessageOrg<>(Y9ModelConvertUtil.convert(savedOrganization, Organization.class),
                                Y9OrgEventTypeConst.ORGANIZATION_UPDATE, Y9LoginUserHolder.getTenantId());
                        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "更新组织机构",
                            "更新" + originY9Organization.getName());
                    }
                });

                return originY9Organization;
            }
        } else {
            organization.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }

        Integer maxTabIndex = getMaxTabIndex();
        organization.setTabIndex(maxTabIndex != null ? maxTabIndex + 1 : 0);
        organization.setVersion(InitDataConsts.Y9_VERSION);
        organization.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.ORGANIZATION) + organization.getName());
        organization.setGuidPath(organization.getId());
        organization.setTenantId(Y9LoginUserHolder.getTenantId());
        final Y9Organization savedOrganization = y9OrganizationManager.save(organization);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                Y9MessageOrg<Organization> msg =
                    new Y9MessageOrg<>(Y9ModelConvertUtil.convert(savedOrganization, Organization.class),
                        Y9OrgEventTypeConst.ORGANIZATION_ADD, Y9LoginUserHolder.getTenantId());
                Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "新增组织机构", "新增" + savedOrganization.getName());
            }
        });

        return savedOrganization;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Organization saveProperties(String orgId, String properties) {
        Y9Organization org = this.getById(orgId);
        org.setProperties(properties);
        final Y9Organization savedOrganization = y9OrganizationManager.save(org);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                Y9MessageOrg<Organization> msg =
                    new Y9MessageOrg<>(Y9ModelConvertUtil.convert(savedOrganization, Organization.class),
                        Y9OrgEventTypeConst.ORGANIZATION_UPDATE, Y9LoginUserHolder.getTenantId());
                Y9PublishServiceUtil.publishMessageOrg(msg);
            }
        });

        return savedOrganization;
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