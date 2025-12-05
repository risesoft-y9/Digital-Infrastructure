package net.risesoft.listener;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.schema.SchemaUpdater;

/**
 * 应用程序就绪事件监听器
 *
 * @author shidaobang
 * @date 2025/12/05
 */
@Slf4j
@RequiredArgsConstructor
public class ApplicationReadyEventListener {

    private final ObjectProvider<SchemaUpdater> schemaUpdater;

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        schemaUpdater.ifAvailable(SchemaUpdater::updateAllTenants);
    }
}
