package net.risesoft.listener;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.y9public.manager.Y9AuditLogManager;

/**
 * 审计日志事件监听器
 *
 * @author shidaobang
 * @date 2025/08/13
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "y9.app.platform.audit-log-enabled", havingValue = "true", matchIfMissing = true)
public class AuditLogEventListener {

    private final Y9AuditLogManager y9AuditLogManager;

    @TransactionalEventListener
    public void auditLog(AuditLogEvent event) {
        y9AuditLogManager.save(event);
    }

}
