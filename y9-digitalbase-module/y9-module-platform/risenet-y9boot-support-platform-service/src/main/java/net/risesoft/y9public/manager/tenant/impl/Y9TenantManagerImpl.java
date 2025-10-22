package net.risesoft.y9public.manager.tenant.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.OrgLevelConsts;
import net.risesoft.exception.TenantErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.entity.tenant.Y9Tenant;
import net.risesoft.y9public.manager.tenant.Y9TenantManager;
import net.risesoft.y9public.repository.tenant.Y9TenantRepository;

/**
 * 租户 Manager 实现类
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
@Service
@RequiredArgsConstructor
public class Y9TenantManagerImpl implements Y9TenantManager {

    private final Y9TenantRepository y9TenantRepository;

    @Override
    public Y9Tenant getById(String id) {
        return y9TenantRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(TenantErrorCodeEnum.TENANT_NOT_FOUND, id));
    }

    @Override
    public Y9Tenant insert(Y9Tenant y9Tenant) {
        if (StringUtils.isBlank(y9Tenant.getId())) {
            y9Tenant.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }

        String parentId = y9Tenant.getParentId();
        if (StringUtils.isNotBlank(parentId)) {
            Y9Tenant parent = this.getById(parentId);
            y9Tenant.setParentId(parentId);
            y9Tenant.setNamePath(parent.getNamePath() + OrgLevelConsts.SEPARATOR + y9Tenant.getShortName());
            y9Tenant.setGuidPath(parent.getGuidPath() + OrgLevelConsts.SEPARATOR + y9Tenant.getId());
        } else {
            y9Tenant.setParentId(null);
            y9Tenant.setNamePath(y9Tenant.getShortName());
            y9Tenant.setGuidPath(y9Tenant.getId());
        }
        y9Tenant.setTabIndex(this.getNextTabIndex());

        Y9Tenant savedY9Tenant = y9TenantRepository.save(y9Tenant);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedY9Tenant));

        return savedY9Tenant;
    }

    @Override
    public Y9Tenant update(Y9Tenant y9Tenant) {
        String parentId = y9Tenant.getParentId();
        if (StringUtils.isNotBlank(parentId)) {
            Y9Tenant parent = this.getById(parentId);
            y9Tenant.setParentId(parentId);
            y9Tenant.setNamePath(parent.getNamePath() + OrgLevelConsts.SEPARATOR + y9Tenant.getShortName());
            y9Tenant.setGuidPath(parent.getGuidPath() + OrgLevelConsts.SEPARATOR + y9Tenant.getId());
        } else {
            y9Tenant.setParentId(null);
            y9Tenant.setNamePath(y9Tenant.getShortName());
            y9Tenant.setGuidPath(y9Tenant.getId());
        }

        Y9Tenant currentTenant = this.getById(y9Tenant.getId());
        Y9Tenant originalTenant = new Y9Tenant();
        Y9BeanUtil.copyProperties(currentTenant, originalTenant);
        Y9BeanUtil.copyProperties(y9Tenant, currentTenant);

        Y9Tenant savedTenant = y9TenantRepository.save(currentTenant);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originalTenant, savedTenant));

        return savedTenant;
    }

    private Integer getNextTabIndex() {
        return y9TenantRepository.findTopByOrderByTabIndexDesc().map(y9Tenant -> y9Tenant.getTabIndex() + 1).orElse(0);
    }
}
