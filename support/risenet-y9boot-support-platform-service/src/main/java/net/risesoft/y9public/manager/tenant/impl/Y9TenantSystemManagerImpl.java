package net.risesoft.y9public.manager.tenant.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;

import net.risesoft.exception.TenantErrorCodeEnum;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.Y9PublishService;
import net.risesoft.y9.pubsub.constant.Y9CommonEventConst;
import net.risesoft.y9.pubsub.message.Y9MessageCommon;
import net.risesoft.y9public.entity.tenant.Y9TenantSystem;
import net.risesoft.y9public.manager.resource.Y9SystemManager;
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

    private final Y9SystemManager y9SystemManager;

    private final Y9PublishService y9PublishService;

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
        Y9TenantSystem y9TenantSystem = y9TenantSystemRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(TenantErrorCodeEnum.TENANT_SYSTEM_NOT_EXISTS, id));
        y9TenantSystemRepository.delete(y9TenantSystem);

        // 注册事务同步器，在事务提交后做某些操作
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // 移除系统租用后，对应系统重新加载数据源
                Y9MessageCommon syncDataSourceEvent = new Y9MessageCommon();
                syncDataSourceEvent.setTarget(y9SystemManager.getById(y9TenantSystem.getSystemId()).getName());
                syncDataSourceEvent.setEventObject(Y9CommonEventConst.TENANT_DATASOURCE_SYNC);
                syncDataSourceEvent.setEventType(Y9CommonEventConst.TENANT_DATASOURCE_SYNC);
                y9PublishService.publishMessageCommon(syncDataSourceEvent);
            }
        });
    }

    @Override
    public String getDataSourceIdByTenantIdAndSystemId(String tenantId, String systemId) {
        return y9TenantSystemRepository.findByTenantIdAndSystemId(tenantId, systemId)
            .map(Y9TenantSystem::getTenantDataSource).orElse(null);
    }
}
