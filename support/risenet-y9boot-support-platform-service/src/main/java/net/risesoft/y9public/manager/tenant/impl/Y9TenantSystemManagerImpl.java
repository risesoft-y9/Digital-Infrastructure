package net.risesoft.y9public.manager.tenant.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.y9public.entity.tenant.Y9TenantSystem;
import net.risesoft.y9public.manager.tenant.Y9TenantSystemManager;
import net.risesoft.y9public.repository.tenant.Y9TenantSystemRepository;

/**
 * 租户系统 Manager 实现类
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
@Service
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9TenantSystemManagerImpl implements Y9TenantSystemManager {
    
    private final Y9TenantSystemRepository y9TenantSystemRepository;

    @Override
    @Transactional(readOnly = false)
    public void deleteBySystemId(String systemId) {
        List<Y9TenantSystem> y9TenantSystemList = y9TenantSystemRepository.findBySystemId(systemId);
        for (Y9TenantSystem t : y9TenantSystemList) {
            delete(t.getId());
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        y9TenantSystemRepository.deleteById(id);
    }
    
    @Override
    public String getDataSourceIdByTenantIdAndSystemId(String tenantId, String systemId) {
        Y9TenantSystem y9TenantSystem = y9TenantSystemRepository.findByTenantIdAndSystemId(tenantId, systemId);
        if (null != y9TenantSystem) {
            return y9TenantSystem.getTenantDataSource();
        }
        return null;
    }
}
