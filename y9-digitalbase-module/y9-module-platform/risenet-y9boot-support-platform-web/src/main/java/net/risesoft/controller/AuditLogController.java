package net.risesoft.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.platform.AuditLog;
import net.risesoft.permission.annotation.IsAnyManager;
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
@IsAnyManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.OPERATION_SYSTEM_MANAGER,
    ManagerLevelEnum.AUDIT_MANAGER, ManagerLevelEnum.OPERATION_AUDIT_MANAGER,
    ManagerLevelEnum.OPERATION_SECURITY_MANAGER, ManagerLevelEnum.SECURITY_MANAGER})
public class AuditLogController {

    private final Y9AuditLogService y9AuditLogService;

    @RiseLog(operationName = "根据资源id获取关联的组织列表 ")
    @RequestMapping(value = "/list")
    public Y9Page<AuditLog> list(AuditLogQuery auditLogQuery, Y9PageQuery y9PageQuery) {
        return y9AuditLogService.page(auditLogQuery, y9PageQuery);
    }
}
