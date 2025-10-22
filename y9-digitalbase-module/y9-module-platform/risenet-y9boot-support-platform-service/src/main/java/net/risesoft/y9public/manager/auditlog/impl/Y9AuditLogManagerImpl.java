package net.risesoft.y9public.manager.auditlog.impl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.entity.auditlog.Y9AuditLog;
import net.risesoft.y9public.manager.auditlog.Y9AuditLogManager;
import net.risesoft.y9public.repository.auditlog.Y9AuditLogRepository;

/**
 * 审核日志 Manager 实现类
 *
 * @author shidaobang
 * @date 2025/08/06
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class Y9AuditLogManagerImpl implements Y9AuditLogManager {

    private final Y9AuditLogRepository y9AuditLogRepository;

    @Override
    public void save(AuditLogEvent auditLogEvent) {
        Y9AuditLog y9AuditLog = new Y9AuditLog();
        y9AuditLog.setId(Y9IdGenerator.genId());
        y9AuditLog.setAction(auditLogEvent.getAction());
        y9AuditLog.setDescription(auditLogEvent.getDescription());
        y9AuditLog.setTenantId(Y9LoginUserHolder.getTenantId());
        y9AuditLog.setObjectId(auditLogEvent.getObjectId());
        if (auditLogEvent.getOldObject() != null) {
            y9AuditLog.setOldObjectJson(Y9JsonUtil.writeValueAsString(auditLogEvent.getOldObject()));
        }
        if (auditLogEvent.getCurrentObject() != null) {
            y9AuditLog.setCurrentObjectJson(Y9JsonUtil.writeValueAsString(auditLogEvent.getCurrentObject()));
        }
        ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (sra != null) {
            HttpServletRequest request = sra.getRequest();
            y9AuditLog.setUserAgent(request.getHeader("User-Agent"));
            y9AuditLog.setUserIp(Y9Context.getIpAddr(request));
        }
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        if (userInfo != null) {
            y9AuditLog.setUserName(userInfo.getName());
            y9AuditLog.setUserId(userInfo.getPersonId());
        }
        y9AuditLogRepository.save(y9AuditLog);
    }
}
