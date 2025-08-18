package net.risesoft.y9public.manager.tenant.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.exception.TenantErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.tenant.TenantSystem;
import net.risesoft.util.Y9PublishServiceUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.constant.Y9CommonEventConst;
import net.risesoft.y9.pubsub.event.Y9EventCommon;
import net.risesoft.y9.pubsub.message.Y9MessageCommon;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.entity.tenant.Y9DataSource;
import net.risesoft.y9public.entity.tenant.Y9Tenant;
import net.risesoft.y9public.entity.tenant.Y9TenantSystem;
import net.risesoft.y9public.manager.resource.Y9SystemManager;
import net.risesoft.y9public.manager.tenant.Y9DataSourceManager;
import net.risesoft.y9public.manager.tenant.Y9TenantManager;
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
@Slf4j
public class Y9TenantSystemManagerImpl implements Y9TenantSystemManager {

    private final Y9TenantSystemRepository y9TenantSystemRepository;

    private final Y9SystemManager y9SystemManager;
    private final Y9TenantManager y9TenantManager;
    private final Y9DataSourceManager y9DataSourceManager;

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
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    // 移除系统租用后，对应系统重新加载数据源
                    Optional<Y9System> y9SystemOptional = y9SystemManager.findById(y9TenantSystem.getSystemId());
                    if (y9SystemOptional.isPresent()) {
                        Y9MessageCommon syncDataSourceEvent = new Y9MessageCommon();
                        syncDataSourceEvent.setEventTarget(y9SystemOptional.get().getName());
                        syncDataSourceEvent.setEventObject(Y9CommonEventConst.TENANT_DATASOURCE_SYNC);
                        syncDataSourceEvent.setEventType(Y9CommonEventConst.TENANT_DATASOURCE_SYNC);
                        Y9PublishServiceUtil.publishMessageCommon(syncDataSourceEvent);

                        LOGGER.debug("移除租户系统后发送租户数据源同步事件：{}", syncDataSourceEvent);
                    }
                }
            });
        }
    }

    @Override
    public String getDataSourceIdByTenantIdAndSystemId(String tenantId, String systemId) {
        return y9TenantSystemRepository.findByTenantIdAndSystemId(tenantId, systemId)
            .map(Y9TenantSystem::getTenantDataSource)
            .orElse(null);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9TenantSystem save(Y9TenantSystem y9TenantSystem) {
        if (StringUtils.isBlank(y9TenantSystem.getId())) {
            y9TenantSystem.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        } else {
            // 如果数据源切换则需重新初始化
            Optional<Y9TenantSystem> y9TenantSystemOptional = y9TenantSystemRepository.findById(y9TenantSystem.getId());
            if (y9TenantSystemOptional.isPresent() && !Objects.equals(y9TenantSystem.getTenantDataSource(),
                y9TenantSystemOptional.get().getTenantDataSource())) {
                y9TenantSystem.setInitialized(false);
            }
        }

        y9TenantSystem = y9TenantSystemRepository.save(y9TenantSystem);

        Y9System y9System = y9SystemManager.getById(y9TenantSystem.getSystemId());
        TenantSystem tenantSystem = Y9ModelConvertUtil.convert(y9TenantSystem, TenantSystem.class);

        if (Objects.equals(Y9Context.getSystemName(), y9System.getName())) {
            // 对于租用数字底座的，立即发送租用事件，用于集成测试
            Y9EventCommon tenantSystemRegisteredEvent = new Y9EventCommon();
            tenantSystemRegisteredEvent.setEventObject(tenantSystem);
            tenantSystemRegisteredEvent.setTarget(y9System.getName());
            tenantSystemRegisteredEvent.setEventType(Y9CommonEventConst.TENANT_SYSTEM_REGISTERED);
            Y9Context.publishEvent(tenantSystemRegisteredEvent);
        }

        // 注册事务同步器，在事务提交后做某些操作
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    // 租户租用系统事件，应用可监听做对应租户的初始化的工作
                    Y9MessageCommon tenantSystemRegisteredEvent = new Y9MessageCommon();
                    tenantSystemRegisteredEvent.setEventObject(tenantSystem);
                    tenantSystemRegisteredEvent.setEventTarget(y9System.getName());
                    tenantSystemRegisteredEvent.setEventType(Y9CommonEventConst.TENANT_SYSTEM_REGISTERED);
                    Y9PublishServiceUtil.publishMessageCommon(tenantSystemRegisteredEvent);

                    LOGGER.debug("添加租户系统后发送租户租用系统事件：{}", tenantSystemRegisteredEvent);
                }
            });
        }

        return y9TenantSystem;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9TenantSystem saveTenantSystem(String systemId, String tenantId) {
        Y9Tenant tenant = y9TenantManager.getById(tenantId);
        Y9System y9System = y9SystemManager.getById(systemId);

        Optional<Y9TenantSystem> y9TenantSystemOptional =
            y9TenantSystemRepository.findByTenantIdAndSystemId(tenantId, systemId);
        if (y9TenantSystemOptional.isPresent()) {
            return y9TenantSystemOptional.get();
        }

        Y9TenantSystem y9TenantSystem = new Y9TenantSystem();
        y9TenantSystem.setTenantId(tenantId);
        y9TenantSystem.setTenantDataSource(tenant.getDefaultDataSourceId());
        y9TenantSystem.setSystemId(systemId);
        y9TenantSystem.setInitialized(false);
        if (Boolean.TRUE.equals(y9System.getSingleDatasource())) {
            String datasoureId = tenant.getDefaultDataSourceId();
            try {
                Y9DataSource y9DataSource =
                    y9DataSourceManager.createTenantDefaultDataSource(tenant.getShortName(), y9System.getName());
                datasoureId = y9DataSource.getId();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
            y9TenantSystem.setTenantDataSource(datasoureId);
        }
        return this.save(y9TenantSystem);
    }
}
