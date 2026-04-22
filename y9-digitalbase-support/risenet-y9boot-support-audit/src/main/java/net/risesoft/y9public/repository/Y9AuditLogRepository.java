package net.risesoft.y9public.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import net.risesoft.y9public.entity.Y9AuditLog;

/**
 * 审计日志 Repository
 *
 * @author shidaobang
 * @date 2025/08/06
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
public interface Y9AuditLogRepository extends JpaRepository<Y9AuditLog, String>, JpaSpecificationExecutor<Y9AuditLog> {

}
