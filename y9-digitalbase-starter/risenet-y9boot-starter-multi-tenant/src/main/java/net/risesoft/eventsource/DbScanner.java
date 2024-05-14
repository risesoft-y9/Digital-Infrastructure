package net.risesoft.eventsource;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.dao.MultiTenantDao;
import net.risesoft.model.platform.TenantApp;
import net.risesoft.model.platform.TenantSystem;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.pubsub.constant.Y9CommonEventConst;
import net.risesoft.y9.pubsub.event.Y9EventCommon;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

/**
 * 数据库扫表
 *
 * @author shidaobang
 * @date 2024/05/13
 */
@Slf4j
@RequiredArgsConstructor
public class DbScanner {

    private final Y9TenantDataSourceLookup y9TenantDataSourceLookup;
    private final MultiTenantDao multiTenantDao;

    private Date lastCheckTime = new Date();

    public void scan() {
        produceTenantSystemRelatedEvent();
        produceTenantAppRelatedEvent();
        produceDataSourceRelatedEvent();

        lastCheckTime = new Date();
    }

    private void produceDataSourceRelatedEvent() {
        List<String> dataSourceIdList = multiTenantDao.getDataSourceIdList(this.lastCheckTime);
        for (String id : dataSourceIdList) {

            Integer count = multiTenantDao.countTenantSystem(y9TenantDataSourceLookup.getSystemName(), id);
            if (count > 0) {
                Y9EventCommon tenantDataSourceSyncEvent = new Y9EventCommon();
                tenantDataSourceSyncEvent.setEventType(Y9CommonEventConst.TENANT_DATASOURCE_SYNC);
                tenantDataSourceSyncEvent.setEventObject(Y9CommonEventConst.TENANT_DATASOURCE_SYNC);
                Y9Context.publishEvent(tenantDataSourceSyncEvent);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("产生租户数据源同步事件：{}", tenantDataSourceSyncEvent);
                }
            }
        }
    }

    private void produceTenantAppRelatedEvent() {
        // 获取新加的租户应用发送事件
        List<TenantApp> tenantAppList =
            multiTenantDao.getUninitializedTenantAppList(y9TenantDataSourceLookup.getSystemId());
        for (TenantApp tenantApp : tenantAppList) {
            Y9EventCommon tenantSystemRegisteredEvent = new Y9EventCommon();
            tenantSystemRegisteredEvent.setEventObject(tenantApp);
            tenantSystemRegisteredEvent.setTarget(y9TenantDataSourceLookup.getSystemName());
            tenantSystemRegisteredEvent.setEventType(Y9CommonEventConst.TENANT_APP_REGISTERED);
            Y9Context.publishEvent(tenantSystemRegisteredEvent);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("产生租户应用租用事件：{}", tenantSystemRegisteredEvent);
            }
        }
    }

    private void produceTenantSystemRelatedEvent() {
        Set<String> loadedTenantIdSet = y9TenantDataSourceLookup.getDataSources().keySet();
        List<TenantSystem> tenantSystemList =
            multiTenantDao.getTenantSystemList(y9TenantDataSourceLookup.getSystemId());

        // 获取新的系统租用发送事件
        Set<String> unInitialiedTenantIdSet =
            tenantSystemList.stream().filter(tenantSystem -> Boolean.FALSE.equals(tenantSystem.getInitialized()))
                .map(TenantSystem::getTenantId).collect(Collectors.toSet());
        Collection<String> newTenantIds = CollectionUtils.subtract(unInitialiedTenantIdSet, loadedTenantIdSet);
        List<TenantSystem> newTenantSystems =
            tenantSystemList.stream().filter(p -> newTenantIds.contains(p.getTenantId())).collect(Collectors.toList());
        for (TenantSystem tenantSystem : newTenantSystems) {
            Y9EventCommon tenantSystemRegisteredEvent = new Y9EventCommon();
            tenantSystemRegisteredEvent.setEventType(Y9CommonEventConst.TENANT_SYSTEM_REGISTERED);
            tenantSystemRegisteredEvent.setEventObject(tenantSystem);
            Y9Context.publishEvent(tenantSystemRegisteredEvent);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("产生租户租用系统事件：{}", tenantSystemRegisteredEvent);
            }
        }

        // 获取已移除的系统租用租户发送事件
        Set<String> allTenantIdSet =
            tenantSystemList.stream().map(TenantSystem::getTenantId).collect(Collectors.toSet());
        Collection<String> removedTenantIds = CollectionUtils.subtract(loadedTenantIdSet, allTenantIdSet);
        if (!removedTenantIds.isEmpty()) {
            Y9EventCommon tenantDataSourceSyncEvent = new Y9EventCommon();
            tenantDataSourceSyncEvent.setEventType(Y9CommonEventConst.TENANT_DATASOURCE_SYNC);
            tenantDataSourceSyncEvent.setEventObject(Y9CommonEventConst.TENANT_DATASOURCE_SYNC);
            Y9Context.publishEvent(tenantDataSourceSyncEvent);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("产生租户数据源同步事件：{}", tenantDataSourceSyncEvent);
            }
        }
    }
}
