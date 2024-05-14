package net.risesoft.listener;

import net.risesoft.dao.MultiTenantDao;
import org.springframework.context.event.EventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.init.TenantAppInitializer;
import net.risesoft.model.platform.TenantApp;
import net.risesoft.y9.pubsub.constant.Y9CommonEventConst;
import net.risesoft.y9.pubsub.event.Y9EventCommon;

/**
 * 租户应用事件监听器
 *
 * @author shidaobang
 * @date 2024/03/22
 */
@Slf4j
@RequiredArgsConstructor
public class TenantAppEventListener {

    private final TenantAppInitializer tenantAppInitializer;
    private final MultiTenantDao multiTenantDao;

    @EventListener
    public void tenantSystemRegisteredEvent(Y9EventCommon event) {
        try {
            if (Y9CommonEventConst.TENANT_APP_REGISTERED.equals(event.getEventType())) {
                LOGGER.info("开始处理租户应用事件");
                TenantApp tenantApp = (TenantApp)event.getEventObject();

                if (tenantAppInitializer != null) {
                    tenantAppInitializer.init(tenantApp);
                    LOGGER.info("处理租户应用事件完成");
                }
                
                multiTenantDao.tenantAppInitialized(tenantApp.getId());
            }
        } catch (Exception e) {
            LOGGER.warn("处理租户应用事件发生异常", e);
        }
    }

}
