package net.risesoft.y9public.service.auditlog;

import net.risesoft.model.platform.AuditLog;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.query.platform.AuditLogQuery;

/**
 * 审计日志 Service
 *
 * @author shidaobang
 * @date 2025/08/12
 */
public interface Y9AuditLogService {

    Y9Page<AuditLog> page(AuditLogQuery auditLogQuery, Y9PageQuery y9PageQuery);

}
