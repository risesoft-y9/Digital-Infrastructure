package net.risesoft.y9public.manager;

import net.risesoft.pojo.AuditLogEvent;

/**
 * 审计日志 Manager
 *
 * @author shidaobang
 * @date 2025/08/06
 */
public interface Y9AuditLogManager {

    void save(AuditLogEvent auditLogEvent);

}
