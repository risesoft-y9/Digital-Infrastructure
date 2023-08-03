package net.risesoft.y9public.manager.tenant.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.repository.identity.person.Y9PersonToResourceAndAuthorityRepository;
import net.risesoft.repository.identity.position.Y9PositionToResourceAndAuthorityRepository;
import net.risesoft.repository.permission.Y9AuthorizationRepository;
import net.risesoft.y9.Y9LoginUserHolder;
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
    private final Y9AuthorizationRepository y9AuthorizationRepository;
    private final Y9PersonToResourceAndAuthorityRepository y9PersonToResourceAndAuthorityRepository;
    private final Y9PositionToResourceAndAuthorityRepository y9PositionToResourceAndAuthorityRepository;

    @Override
    @Transactional(readOnly = false)
    public void deleteByAppId(String appId) {
        List<Y9TenantApp> y9TenantAppList = y9TenantAppRepository.findByAppId(appId);
        for (Y9TenantApp y9TenantApp : y9TenantAppList) {

            Y9LoginUserHolder.setTenantId(y9TenantApp.getTenantId());

            y9AuthorizationRepository.deleteByResourceId(y9TenantApp.getAppId());
            y9PersonToResourceAndAuthorityRepository.deleteByResourceId(y9TenantApp.getAppId());
            y9PositionToResourceAndAuthorityRepository.deleteByResourceId(y9TenantApp.getAppId());

            y9TenantAppRepository.delete(y9TenantApp);
        }
    }

    @Override
    public Y9TenantApp getByTenantIdAndAppIdAndTenancy(String tenantId, String appId, Boolean tenancy) {
        return y9TenantAppRepository.findByTenantIdAndAppIdAndTenancy(tenantId, appId, tenancy);
    }
}
