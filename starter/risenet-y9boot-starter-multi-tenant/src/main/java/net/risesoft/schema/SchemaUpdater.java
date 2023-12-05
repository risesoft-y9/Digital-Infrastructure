package net.risesoft.schema;

import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.init.TenantDataInitializer;
import net.risesoft.model.platform.TenantSystem;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9CommonEventConst;
import net.risesoft.y9.pubsub.constant.Y9TopicConst;
import net.risesoft.y9.pubsub.event.Y9EventCommon;
import net.risesoft.y9.pubsub.message.Y9MessageCommon;
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
public abstract class SchemaUpdater {

    private final Y9TenantDataSourceLookup y9TenantDataSourceLookup;
    private final TenantDataInitializer tenantDataInitializer;
    private final KafkaTemplate<String, String> y9KafkaTemplate;

    @EventListener
    public void tenantSystemRegisteredEvent(Y9EventCommon event) {
        try {
            if (Y9CommonEventConst.TENANT_SYSTEM_REGISTERED.equals(event.getEventType())) {
                LOGGER.info("开始处理租户数据结构更新");
                TenantSystem tenantSystem = (TenantSystem)event.getEventObject();

                y9TenantDataSourceLookup.loadDataSources();

                doUpdate(tenantSystem.getTenantId());
                LOGGER.info("租户数据结构更新完成");

                if (tenantDataInitializer != null) {
                    tenantDataInitializer.init(tenantSystem.getTenantId());
                    LOGGER.info("租户数据初始化完成");
                }

                Y9MessageCommon tenantSystemInitializedEvent = new Y9MessageCommon();
                tenantSystemInitializedEvent.setEventObject(tenantSystem);
                tenantSystemInitializedEvent.setEventTarget("riseplatform");
                tenantSystemInitializedEvent.setEventType(Y9CommonEventConst.TENANT_SYSTEM_INITIALIZED);
                String jsonString = Y9JsonUtil.writeValueAsString(tenantSystemInitializedEvent);

                y9KafkaTemplate.send(Y9TopicConst.Y9_COMMON_EVENT, jsonString);
            }
        } catch (Exception e) {
            LOGGER.warn("租户数据初始化发生异常", e);
        }
    }

    protected abstract void doUpdate(String tenantId) throws Exception;

}
