package net.risesoft.listener;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.dao.MultiTenantDao;
import net.risesoft.init.TenantDataInitializer;
import net.risesoft.model.platform.TenantSystem;
import net.risesoft.schema.SchemaUpdater;
import net.risesoft.y9.pubsub.constant.Y9CommonEventConst;
import net.risesoft.y9.pubsub.event.Y9EventCommon;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

/**
 * 租户租用系统事件监听器
 *
 * @author shidaobang
 * @date 2024/03/22
 */
@Slf4j
@RequiredArgsConstructor
public class TenantSystemRegisteredEventListener {

    private final Y9TenantDataSourceLookup y9TenantDataSourceLookup;
    private final SchemaUpdater schemaUpdater;
    private final MultiTenantDao multiTenantDao;

    private List<TenantDataInitializer> tenantDataInitializerList;

    @Autowired(required = false)
    public void setTenantDataInitializerList(List<TenantDataInitializer> tenantDataInitializerList) {
        this.tenantDataInitializerList = tenantDataInitializerList;
    }

    @EventListener
    public void tenantSystemRegisteredEvent(Y9EventCommon event) {
        try {
            if (Y9CommonEventConst.TENANT_SYSTEM_REGISTERED.equals(event.getEventType())) {
                LOGGER.info("开始处理租户数据结构更新");
                TenantSystem tenantSystem = (TenantSystem)event.getEventObject();

                y9TenantDataSourceLookup.loadDataSources();

                schemaUpdater.doUpdate(tenantSystem.getTenantId());
                LOGGER.info("租户数据结构更新完成");

                if (tenantDataInitializerList != null && !tenantDataInitializerList.isEmpty()) {
                    for (TenantDataInitializer tenantDataInitializer : tenantDataInitializerList) {
                        tenantDataInitializer.init(tenantSystem.getTenantId());
                    }
                    LOGGER.info("租户数据初始化完成");
                }

                multiTenantDao.setTenantSystemInitialized(tenantSystem.getId());

            }
        } catch (Exception e) {
            LOGGER.warn("租户数据初始化发生异常", e);
        }
    }
}
