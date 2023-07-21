package net.risesoft.y9public.manager.tenant.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.exception.TenantErrorCodeEnum;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
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
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9TenantManagerImpl implements Y9TenantManager {
    
    private final Y9TenantRepository y9TenantRepository;

    @Override
    public Y9Tenant getById(String id) {
        return y9TenantRepository.findById(id).orElseThrow(() -> Y9ExceptionUtil.notFoundException(TenantErrorCodeEnum.TENANT_NOT_FOUND, id));
    }
}
