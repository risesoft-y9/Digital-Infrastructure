package net.risesoft.listener;

import org.springframework.context.event.EventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.pubsub.constant.Y9CommonEventConst;
import net.risesoft.y9.pubsub.event.Y9EventCommon;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

/**
 * 租户数据源事件监听器
 *
 * @author shidaobang
 * @date 2024/05/13
 * @since 9.6.6
 */
@RequiredArgsConstructor
@Slf4j
public class TenantDataSourceEventListener {

    private final Y9TenantDataSourceLookup y9TenantDataSourceLookup;

    @EventListener
    public void tenantSystemRegisteredEvent(Y9EventCommon event) {
        try {
            if (Y9CommonEventConst.TENANT_DATASOURCE_SYNC.equals(event.getEventType())) {
                y9TenantDataSourceLookup.loadDataSources();

                LOGGER.info(this.y9TenantDataSourceLookup.getSystemName() + ", 同步租户数据源信息, 成功！");
            }
        } catch (Exception e) {
            LOGGER.warn("同步租户数据源信息发生异常", e);
        }
    }

}
