package net.risesoft.service.org.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Organization;
import net.risesoft.enums.platform.AuthorizationPrincipalTypeEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9OrganizationManager;
import net.risesoft.repository.Y9OrganizationRepository;
import net.risesoft.repository.permission.Y9AuthorizationRepository;
import net.risesoft.repository.relation.Y9OrgBasesToRolesRepository;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.util.Y9ModelConvertUtil;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class Y9OrganizationServiceImpl implements Y9OrganizationService {

    private final Y9OrganizationRepository y9OrganizationRepository;
    private final Y9OrgBasesToRolesRepository y9OrgBasesToRolesRepository;
    private final Y9AuthorizationRepository y9AuthorizationRepository;

    private final Y9OrganizationManager y9OrganizationManager;
    private final CompositeOrgBaseManager compositeOrgBaseManager;

    @Override
    @Transactional(readOnly = false)
    public Y9Organization changeDisabled(String id) {
        Y9Organization y9Organization = y9OrganizationManager.getByIdNotCache(id);
        Boolean isDisabled = !y9Organization.getDisabled();

        if (isDisabled) {
            // 禁用时检查所有子节点是否都禁用了，只有所有子节点都禁用了，当前组织才能禁用
            compositeOrgBaseManager.checkAllDescendantsDisabled(id);
        }

        Y9Organization updatedOrganization = Y9ModelConvertUtil.convert(y9Organization, Y9Organization.class);
        updatedOrganization.setDisabled(isDisabled);
        return y9OrganizationManager.saveOrUpdate(updatedOrganization);
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

    @Override
    public List<Y9Organization> listByDn(String dn) {
        return y9OrganizationRepository.findByDn(dn);
    }

    @Override
    public List<Y9Organization> listByTenantId(String tenantId) {
        return y9OrganizationRepository.findByTenantIdOrderByTabIndexAsc(tenantId);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Organization saveOrUpdate(Y9Organization organization) {
        return y9OrganizationManager.saveOrUpdate(organization);
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9Organization> saveOrder(List<String> orgIds) {
        List<Y9Organization> orgList = new ArrayList<>();

        int tabIndex = 0;
        for (String orgId : orgIds) {
            orgList.add(y9OrganizationManager.updateTabIndex(orgId, tabIndex++));
        }
        return orgList;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Organization saveProperties(String orgId, String properties) {
        return y9OrganizationManager.saveProperties(orgId, properties);
    }

    private List<Y9Organization> list(Boolean disabled) {
        if (disabled == null) {
            return y9OrganizationRepository.findByOrderByTabIndexAsc();
        } else {
            return y9OrganizationRepository.findByDisabledOrderByTabIndexAsc(disabled);
        }
    }

}