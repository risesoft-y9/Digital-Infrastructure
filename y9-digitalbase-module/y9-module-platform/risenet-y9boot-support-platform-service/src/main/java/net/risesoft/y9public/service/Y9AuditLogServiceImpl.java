package net.risesoft.y9public.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9public.entity.Y9AuditLog;
import net.risesoft.y9public.repository.Y9AuditLogRepository;

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
    public Page<Y9AuditLog> page(Y9PageQuery y9PageQuery) {
        return y9AuditLogRepository.findAll(
            PageRequest.of(y9PageQuery.getPage4Db(), y9PageQuery.getSize(), Sort.Direction.DESC, "createTime"));
    }
}
