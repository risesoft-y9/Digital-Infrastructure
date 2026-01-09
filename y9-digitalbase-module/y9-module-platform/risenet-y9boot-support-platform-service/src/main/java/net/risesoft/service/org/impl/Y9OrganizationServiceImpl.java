package net.risesoft.service.org.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.entity.org.Y9Organization;
import net.risesoft.enums.AuditLogEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9OrganizationManager;
import net.risesoft.model.platform.org.Organization;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.repository.org.Y9OrganizationRepository;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.util.Y9StringUtil;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
public class Y9OrganizationServiceImpl implements Y9OrganizationService {

    private final Y9OrganizationRepository y9OrganizationRepository;

    private final Y9OrganizationManager y9OrganizationManager;
    private final CompositeOrgBaseManager compositeOrgBaseManager;

    @Override
    @Transactional
    public Organization changeDisabled(String id) {
        Y9Organization currentOrganization = y9OrganizationManager.getById(id);
        Y9Organization originalOrganization =
            PlatformModelConvertUtil.convert(currentOrganization, Y9Organization.class);

        boolean allDescendantsDisabled = compositeOrgBaseManager.isAllDescendantsDisabled(id);
        Boolean disableStatus = currentOrganization.changeDisabled(allDescendantsDisabled);
        Y9Organization savedOrganization = y9OrganizationManager.update(currentOrganization, originalOrganization);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.ORGANIZATION_UPDATE_DISABLED.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.ORGANIZATION_UPDATE_DISABLED.getDescription(),
                savedOrganization.getName(), disableStatus ? "禁用" : "启用"))
            .objectId(id)
            .oldObject(originalOrganization)
            .currentObject(savedOrganization)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return PlatformModelConvertUtil.y9OrganizationToOrganization(savedOrganization);
    }

    @Override
    @Transactional
    public Organization create(String organizationName, Boolean virtual) {
        Organization y9Organization = new Organization();
        y9Organization.setName(organizationName);
        y9Organization.setVirtual(virtual);
        return this.saveOrUpdate(y9Organization);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Y9Organization y9Organization = y9OrganizationManager.getById(id);
        y9OrganizationManager.delete(y9Organization);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.ORGANIZATION_DELETE.getAction())
            .description(
                Y9StringUtil.format(AuditLogEnum.ORGANIZATION_DELETE.getDescription(), y9Organization.getName()))
            .objectId(id)
            .oldObject(y9Organization)
            .currentObject(null)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        // 发布事件，程序内部监听处理相关业务
        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Organization));
    }

    @Override
    public boolean existsById(String id) {
        return y9OrganizationRepository.existsById(id);
    }

    @Override
    public Optional<Organization> findById(String id) {
        return y9OrganizationManager.findByIdFromCache(id).map(PlatformModelConvertUtil::y9OrganizationToOrganization);
    }

    @Override
    public Organization getById(String id) {
        return PlatformModelConvertUtil.y9OrganizationToOrganization(y9OrganizationManager.getByIdFromCache(id));
    }

    @Override
    public List<Organization> list() {
        List<Y9Organization> y9OrganizationList = y9OrganizationRepository.findByOrderByTabIndexAsc();
        return PlatformModelConvertUtil.y9OrganizationToOrganization(y9OrganizationList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Organization> list(Boolean virtual, Boolean disabled) {
        List<Y9Organization> y9OrganizationList;

        if (virtual == null && disabled == null) {
            y9OrganizationList = y9OrganizationRepository.findByOrderByTabIndexAsc();
        } else if (virtual == null) {
            y9OrganizationList = y9OrganizationRepository.findByDisabledOrderByTabIndexAsc(disabled);
        } else if (disabled == null) {
            y9OrganizationList = y9OrganizationRepository.findByVirtualOrderByTabIndexAsc(virtual);
        } else {
            y9OrganizationList = y9OrganizationRepository.findByVirtualAndDisabledOrderByTabIndexAsc(virtual, disabled);
        }

        return PlatformModelConvertUtil.y9OrganizationToOrganization(y9OrganizationList);
    }

    @Override
    public List<Organization> listByDn(String dn) {
        List<Y9Organization> y9OrganizationList = y9OrganizationRepository.findByDn(dn);
        return PlatformModelConvertUtil.y9OrganizationToOrganization(y9OrganizationList);
    }

    @Override
    @Transactional
    public Organization saveOrUpdate(Organization organization) {
        if (StringUtils.isNotBlank(organization.getId())) {
            Optional<Y9Organization> organizationOptional = y9OrganizationManager.findById(organization.getId());
            if (organizationOptional.isPresent()) {
                Y9Organization originalOrganization =
                    PlatformModelConvertUtil.convert(organizationOptional.get(), Y9Organization.class);
                Y9Organization y9Organization = organizationOptional.get();

                y9Organization.update(organization);
                Y9Organization savedOrganization = y9OrganizationManager.update(y9Organization, originalOrganization);

                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(AuditLogEnum.ORGANIZATION_UPDATE.getAction())
                    .description(Y9StringUtil.format(AuditLogEnum.ORGANIZATION_UPDATE.getDescription(),
                        y9Organization.getName()))
                    .objectId(savedOrganization.getId())
                    .oldObject(originalOrganization)
                    .currentObject(savedOrganization)
                    .build();
                Y9Context.publishEvent(auditLogEvent);

                return PlatformModelConvertUtil.y9OrganizationToOrganization(savedOrganization);
            }
        }

        Y9Organization y9Organization = new Y9Organization(organization, getNextTabIndex());
        Y9Organization savedOrganization = y9OrganizationManager.insert(y9Organization);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.ORGANIZATION_CREATE.getAction())
            .description(
                Y9StringUtil.format(AuditLogEnum.ORGANIZATION_CREATE.getDescription(), savedOrganization.getName()))
            .objectId(savedOrganization.getId())
            .oldObject(null)
            .currentObject(savedOrganization)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return PlatformModelConvertUtil.y9OrganizationToOrganization(savedOrganization);
    }

    @Override
    @Transactional
    public List<Organization> saveOrder(List<String> orgIds) {
        List<Y9Organization> y9OrganizationList = new ArrayList<>();

        int tabIndex = 0;
        for (String orgId : orgIds) {
            Y9Organization organization = y9OrganizationManager.getById(orgId);
            Y9Organization originalOrganization = PlatformModelConvertUtil.convert(organization, Y9Organization.class);

            organization.changeTabIndex(tabIndex);
            y9OrganizationList.add(y9OrganizationManager.update(organization, originalOrganization));
        }
        return PlatformModelConvertUtil.y9OrganizationToOrganization(y9OrganizationList);
    }

    @Override
    @Transactional
    public Organization saveProperties(String orgId, String properties) {
        Y9Organization currentOrganization = y9OrganizationManager.getById(orgId);
        Y9Organization originalOrganization =
            PlatformModelConvertUtil.convert(currentOrganization, Y9Organization.class);

        currentOrganization.changeProperties(properties);
        Y9Organization savedOrganization = y9OrganizationManager.update(currentOrganization, originalOrganization);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.ORGANIZATION_UPDATE_PROPERTIES.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.ORGANIZATION_UPDATE_PROPERTIES.getDescription(),
                savedOrganization.getName(), savedOrganization.getProperties()))
            .objectId(orgId)
            .oldObject(originalOrganization)
            .currentObject(savedOrganization)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return PlatformModelConvertUtil.y9OrganizationToOrganization(savedOrganization);
    }

    private Integer getNextTabIndex() {
        return y9OrganizationRepository.findTopByOrderByTabIndexDesc().map(Y9OrgBase::getTabIndex).orElse(-1) + 1;
    }

}