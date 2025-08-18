package net.risesoft.listener;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.dao.MultiTenantDao;
import net.risesoft.init.TenantAppInitializer;
import net.risesoft.model.platform.tenant.TenantApp;
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

    private final MultiTenantDao multiTenantDao;

    private List<TenantAppInitializer> tenantAppInitializerList;

    @Autowired(required = false)
    public void setTenantAppInitializerList(List<TenantAppInitializer> tenantAppInitializerList) {
        this.tenantAppInitializerList = tenantAppInitializerList;
    }

    @EventListener
    public void tenantSystemRegisteredEvent(Y9EventCommon event) {
        try {
            if (Y9CommonEventConst.TENANT_APP_REGISTERED.equals(event.getEventType())) {
                LOGGER.info("开始处理租户应用事件");
                TenantApp tenantApp = (TenantApp)event.getEventObject();

                if (tenantAppInitializerList != null && !tenantAppInitializerList.isEmpty()) {
                    for (TenantAppInitializer tenantAppInitializer : tenantAppInitializerList) {
                        tenantAppInitializer.init(tenantApp);
                    }
                    LOGGER.info("处理租户应用事件完成");
                }

                multiTenantDao.setTenantAppInitialized(tenantApp.getId());
            }
        } catch (Exception e) {
            LOGGER.warn("处理租户应用事件发生异常", e);
        }
    }

}
