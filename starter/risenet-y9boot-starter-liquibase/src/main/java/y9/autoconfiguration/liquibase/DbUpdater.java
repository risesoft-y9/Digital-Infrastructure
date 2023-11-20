package y9.autoconfiguration.liquibase;

import org.springframework.context.event.EventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.model.platform.TenantSystem;
import net.risesoft.y9.pubsub.constant.Y9CommonEventConst;
import net.risesoft.y9.pubsub.event.Y9EventCommon;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

/**
 * 租户数据结构更新
 *
 * @author shidaobang
 * @date 2023/11/20
 * @since 9.6.3
 */
@RequiredArgsConstructor
@Slf4j
public abstract class DbUpdater {

    private final Y9TenantDataSourceLookup y9TenantDataSourceLookup;
    private final TenantDataInitializer tenantDataInitializer;

    @EventListener
    public void tenantSystemRegisteredEvent(Y9EventCommon event) {
        if (Y9CommonEventConst.TENANT_SYSTEM_REGISTERED.equals(event.getEventType())) {
            LOGGER.info("开始处理租户数据结构更新");
            TenantSystem tenantSystem = (TenantSystem)event.getEventObject();

            y9TenantDataSourceLookup.loadDataSources();

            doUpdate(tenantSystem);
            LOGGER.info("租户数据结构更新完成");

            if (tenantDataInitializer != null) {
                try {
                    tenantDataInitializer.init(tenantSystem);
                } catch (Exception e) {
                    LOGGER.warn("租户数据初始化发生异常", e);
                }
                LOGGER.info("租户数据初始化完成");
            }
        }
    }

    protected abstract void doUpdate(TenantSystem tenantSystem);

}
