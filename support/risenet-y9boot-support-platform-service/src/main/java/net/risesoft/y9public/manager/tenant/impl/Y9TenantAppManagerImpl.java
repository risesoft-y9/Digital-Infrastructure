package net.risesoft.y9public.manager.tenant.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9public.entity.tenant.Y9TenantApp;
import net.risesoft.y9public.manager.tenant.Y9TenantAppManager;
import net.risesoft.y9public.repository.tenant.Y9TenantAppRepository;

/**
 * 租户应用 Manager 实现类
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
@Service
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9TenantAppManagerImpl implements Y9TenantAppManager {

    private final Y9TenantAppRepository y9TenantAppRepository;

    @Override
    @Transactional(readOnly = false)
    public void deleteByAppId(String appId) {
        List<Y9TenantApp> y9TenantAppList = y9TenantAppRepository.findByAppId(appId);
        for (Y9TenantApp y9TenantApp : y9TenantAppList) {
            delete(y9TenantApp);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(Y9TenantApp y9TenantApp) {
        y9TenantAppRepository.delete(y9TenantApp);

        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9TenantApp));
    }

    @Override
    public Optional<Y9TenantApp> getByTenantIdAndAppIdAndTenancy(String tenantId, String appId, Boolean tenancy) {
        return y9TenantAppRepository.findByTenantIdAndAppIdAndTenancy(tenantId, appId, tenancy);
    }

    @Override
    public List<Y9TenantApp> listByAppIdAndTenancy(String appId, Boolean tenancy) {
        return y9TenantAppRepository.findByAppIdAndTenancy(appId, tenancy);
    }
}
