package net.risesoft.y9public.service.auditlog;

import org.springframework.data.domain.Page;

import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9public.entity.auditlog.Y9AuditLog;
import net.risesoft.y9public.specification.query.AuditLogQuery;

/**
 * 审计日志 Service
 *
 * @author shidaobang
 * @date 2025/08/12
 */
public interface Y9AuditLogService {

    Page<Y9AuditLog> page(AuditLogQuery auditLogQuery, Y9PageQuery y9PageQuery);

}
