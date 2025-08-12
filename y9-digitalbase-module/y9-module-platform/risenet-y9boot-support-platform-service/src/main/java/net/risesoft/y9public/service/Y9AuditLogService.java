package net.risesoft.y9public.service;

import org.springframework.data.domain.Page;

import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9public.entity.Y9AuditLog;

/**
 * 审计日志 Service
 *
 * @author shidaobang
 * @date 2025/08/12
 */
public interface Y9AuditLogService {

    Page<Y9AuditLog> page(Y9PageQuery y9PageQuery);

}
