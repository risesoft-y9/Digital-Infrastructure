package net.risesoft.controller;

import net.risesoft.y9.Y9Context;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.model.platform.AuditLog;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.query.platform.AuditLogQuery;
import net.risesoft.y9public.service.auditlog.Y9AuditLogService;

/**
 * 审计日志
 */
@RestController
@RequestMapping(value = "/api/rest/auditLog", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuditLogController {

    private final Y9AuditLogService y9AuditLogService;

    @RequestMapping(value = "/list")
    public Y9Page<AuditLog> list(AuditLogQuery auditLogQuery, Y9PageQuery y9PageQuery) {
        auditLogQuery.setSystemName(Y9Context.getSystemName());
        return y9AuditLogService.page(auditLogQuery, y9PageQuery);
    }
}
