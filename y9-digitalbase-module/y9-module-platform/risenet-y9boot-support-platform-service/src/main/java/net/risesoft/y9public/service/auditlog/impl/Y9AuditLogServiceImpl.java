package net.risesoft.y9public.service.auditlog.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.model.platform.AuditLog;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.query.platform.AuditLogQuery;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9AuditLog;
import net.risesoft.y9public.repository.Y9AuditLogRepository;
import net.risesoft.y9public.service.auditlog.Y9AuditLogService;
import net.risesoft.y9public.specification.Y9AuditLogSpecification;

/**
 * 审计日志 Service 实现类
 *
 * @author shidaobang
 * @date 2025/08/12
 */
@Service
@RequiredArgsConstructor
public class Y9AuditLogServiceImpl implements Y9AuditLogService {

    private final Y9AuditLogRepository y9AuditLogRepository;

    private static List<AuditLog> entityToModel(Page<Y9AuditLog> y9AuditLogPage) {
        return PlatformModelConvertUtil.convert(y9AuditLogPage.getContent(), AuditLog.class);
    }

    @Override
    public Y9Page<AuditLog> page(AuditLogQuery auditLogQuery, Y9PageQuery y9PageQuery) {
        auditLogQuery.setTenantId(Y9LoginUserHolder.getTenantId());
        Page<Y9AuditLog> y9AuditLogPage = y9AuditLogRepository.findAll(new Y9AuditLogSpecification(auditLogQuery),
            PageRequest.of(y9PageQuery.getPage4Db(), y9PageQuery.getSize(), Sort.Direction.DESC, "createTime"));
        return Y9Page.success(y9PageQuery.getPage(), y9AuditLogPage.getTotalPages(), y9AuditLogPage.getTotalElements(),
            entityToModel(y9AuditLogPage));
    }

}
