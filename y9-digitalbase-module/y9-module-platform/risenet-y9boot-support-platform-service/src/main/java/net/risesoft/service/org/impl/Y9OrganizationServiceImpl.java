package net.risesoft.service.org.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.org.Y9Organization;
import net.risesoft.enums.AuditLogEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9OrganizationManager;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.repository.org.Y9OrganizationRepository;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.util.Y9ModelConvertUtil;
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
    public Y9Organization changeDisabled(String id) {
        Y9Organization currentOrganization = y9OrganizationManager.getById(id);
        Y9Organization originalOrganization = Y9ModelConvertUtil.convert(currentOrganization, Y9Organization.class);
        Boolean disableStatusToUpdate = !currentOrganization.getDisabled();

        if (disableStatusToUpdate) {
            // 禁用时检查所有子节点是否都禁用了，只有所有子节点都禁用了，当前组织才能禁用
            compositeOrgBaseManager.checkAllDescendantsDisabled(id);
        }

        currentOrganization.setDisabled(disableStatusToUpdate);
        Y9Organization savedOrganization = y9OrganizationManager.update(currentOrganization);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.ORGANIZATION_UPDATE_DISABLED.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.ORGANIZATION_UPDATE_DISABLED.getDescription(),
                savedOrganization.getName(), disableStatusToUpdate ? "禁用" : "启用"))
            .objectId(id)
            .oldObject(originalOrganization)
            .currentObject(savedOrganization)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedOrganization;
    }

    @Override
    @Transactional
    public Y9Organization create(String organizationName, Boolean virtual) {
        Y9Organization y9Organization = new Y9Organization();
        y9Organization.setName(organizationName);
        y9Organization.setVirtual(virtual);
        return this.saveOrUpdate(y9Organization);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Y9Organization y9Organization = this.getById(id);
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
    public Optional<Y9Organization> findById(String id) {
        return y9OrganizationManager.findByIdFromCache(id);
    }

    @Override
    public Y9Organization getById(String id) {
        return y9OrganizationManager.getByIdFromCache(id);
    }

    @Override
    public List<Y9Organization> list() {
        return y9OrganizationRepository.findByOrderByTabIndexAsc();
    }

    @Override
    @Transactional(readOnly = true)
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

    @Override
    public List<Y9Organization> listByDn(String dn) {
        return y9OrganizationRepository.findByDn(dn);
    }

    @Override
    public List<Y9Organization> listByTenantId(String tenantId) {
        return y9OrganizationRepository.findByTenantIdOrderByTabIndexAsc(tenantId);
    }

    @Override
    @Transactional
    public Y9Organization saveOrUpdate(Y9Organization organization) {
        if (StringUtils.isNotBlank(organization.getId())) {
            Optional<Y9Organization> organizationOptional = y9OrganizationManager.findById(organization.getId());
            if (organizationOptional.isPresent()) {
                Y9Organization originalOrganization =
                    Y9ModelConvertUtil.convert(organizationOptional.get(), Y9Organization.class);
                Y9Organization savedOrganization = y9OrganizationManager.update(organization);

                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(AuditLogEnum.ORGANIZATION_UPDATE.getAction())
                    .description(
                        Y9StringUtil.format(AuditLogEnum.ORGANIZATION_UPDATE.getDescription(), organization.getName()))
                    .objectId(savedOrganization.getId())
                    .oldObject(originalOrganization)
                    .currentObject(savedOrganization)
                    .build();
                Y9Context.publishEvent(auditLogEvent);

                return savedOrganization;
            }
        }

        Y9Organization savedOrganization = y9OrganizationManager.insert(organization);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.ORGANIZATION_CREATE.getAction())
            .description(
                Y9StringUtil.format(AuditLogEnum.ORGANIZATION_CREATE.getDescription(), savedOrganization.getName()))
            .objectId(savedOrganization.getId())
            .oldObject(null)
            .currentObject(savedOrganization)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedOrganization;
    }

    @Override
    @Transactional
    public List<Y9Organization> saveOrder(List<String> orgIds) {
        List<Y9Organization> orgList = new ArrayList<>();

        int tabIndex = 0;
        for (String orgId : orgIds) {
            orgList.add(y9OrganizationManager.updateTabIndex(orgId, tabIndex++));
        }
        return orgList;
    }

    @Override
    @Transactional
    public Y9Organization saveProperties(String orgId, String properties) {
        Y9Organization currentOrganization = y9OrganizationManager.getById(orgId);
        Y9Organization originalOrganization = Y9ModelConvertUtil.convert(currentOrganization, Y9Organization.class);

        currentOrganization.setProperties(properties);
        Y9Organization savedOrganization = y9OrganizationManager.update(currentOrganization);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.ORGANIZATION_UPDATE_PROPERTIES.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.ORGANIZATION_UPDATE_PROPERTIES.getDescription(),
                savedOrganization.getName(), savedOrganization.getProperties()))
            .objectId(orgId)
            .oldObject(originalOrganization)
            .currentObject(savedOrganization)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedOrganization;
    }

    private List<Y9Organization> list(Boolean disabled) {
        if (disabled == null) {
            return y9OrganizationRepository.findByOrderByTabIndexAsc();
        } else {
            return y9OrganizationRepository.findByDisabledOrderByTabIndexAsc(disabled);
        }
    }

}