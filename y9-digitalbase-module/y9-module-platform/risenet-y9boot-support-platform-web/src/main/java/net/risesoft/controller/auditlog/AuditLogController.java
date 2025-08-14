package net.risesoft.controller.auditlog;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9public.entity.auditlog.Y9AuditLog;
import net.risesoft.y9public.service.auditlog.Y9AuditLogService;
import net.risesoft.y9public.specification.query.AuditLogQuery;

/**
 * 审计日志
 */
@RestController
@RequestMapping(value = "/api/rest/auditLog", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@IsAnyManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.OPERATION_SYSTEM_MANAGER,
    ManagerLevelEnum.AUDIT_MANAGER, ManagerLevelEnum.OPERATION_AUDIT_MANAGER})
public class AuditLogController {

    private final Y9AuditLogService y9AuditLogService;

    @RiseLog(operationName = "根据资源id获取关联的组织列表 ")
    @RequestMapping(value = "/list")
    public Y9Page<Y9AuditLog> list(AuditLogQuery auditLogQuery, Y9PageQuery y9PageQuery) {
        Page<Y9AuditLog> y9AuditLogPage = y9AuditLogService.page(auditLogQuery, y9PageQuery);
        return Y9Page.success(y9PageQuery.getPage(), y9AuditLogPage.getTotalPages(), y9AuditLogPage.getTotalElements(),
            y9AuditLogPage.getContent());
    }
}
