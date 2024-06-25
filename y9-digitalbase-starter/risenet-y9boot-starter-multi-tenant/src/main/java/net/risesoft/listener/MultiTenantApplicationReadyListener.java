package net.risesoft.listener;

import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.dao.MultiTenantDao;
import net.risesoft.model.platform.TenantApp;
import net.risesoft.model.platform.TenantSystem;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.pubsub.constant.Y9CommonEventConst;
import net.risesoft.y9.pubsub.event.Y9EventCommon;

@Slf4j
@RequiredArgsConstructor
public class MultiTenantApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

    private final MultiTenantDao multiTenantDao;
    private final Y9Properties y9Properties;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String systemId = multiTenantDao.getSystemId(y9Properties.getSystemName());
        if (systemId != null) {
            List<TenantSystem> uninitializedTenantSystems = multiTenantDao.getUninitializedTenantSystemList(systemId);
            for (TenantSystem tenantSystem : uninitializedTenantSystems) {
                Y9EventCommon tenantSystemRegisteredEvent = new Y9EventCommon();
                tenantSystemRegisteredEvent.setEventType(Y9CommonEventConst.TENANT_SYSTEM_REGISTERED);
                tenantSystemRegisteredEvent.setEventObject(tenantSystem);
                Y9Context.publishEvent(tenantSystemRegisteredEvent);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("产生租户租用系统事件：{}", tenantSystemRegisteredEvent);
                }
            }

            List<TenantApp> tenantAppList = multiTenantDao.getUninitializedTenantAppList(systemId);
            for (TenantApp tenantApp : tenantAppList) {
                Y9EventCommon tenantSystemRegisteredEvent = new Y9EventCommon();
                tenantSystemRegisteredEvent.setEventObject(tenantApp);
                tenantSystemRegisteredEvent.setTarget(y9Properties.getSystemName());
                tenantSystemRegisteredEvent.setEventType(Y9CommonEventConst.TENANT_APP_REGISTERED);
                Y9Context.publishEvent(tenantSystemRegisteredEvent);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("产生租户应用租用事件：{}", tenantSystemRegisteredEvent);
                }
            }
        }
    }

}
