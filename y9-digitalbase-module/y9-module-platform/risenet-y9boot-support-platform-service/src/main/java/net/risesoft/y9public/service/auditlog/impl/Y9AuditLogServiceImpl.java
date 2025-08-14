package net.risesoft.y9public.service.auditlog.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.auditlog.Y9AuditLog;
import net.risesoft.y9public.repository.auditlog.Y9AuditLogRepository;
import net.risesoft.y9public.service.auditlog.Y9AuditLogService;
import net.risesoft.y9public.specification.Y9AuditLogSpecification;
import net.risesoft.y9public.specification.query.AuditLogQuery;

/**
 * 审计日志 Service 实现类
 *
 * @author shidaobang
 * @date 2025/08/12
 */
@Service
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9AuditLogServiceImpl implements Y9AuditLogService {

    private final Y9AuditLogRepository y9AuditLogRepository;

    @Override
    public Page<Y9AuditLog> page(AuditLogQuery auditLogQuery, Y9PageQuery y9PageQuery) {
        auditLogQuery.setTenantId(Y9LoginUserHolder.getTenantId());
        return y9AuditLogRepository.findAll(new Y9AuditLogSpecification(auditLogQuery),
            PageRequest.of(y9PageQuery.getPage4Db(), y9PageQuery.getSize(), Sort.Direction.DESC, "createTime"));
    }

}
