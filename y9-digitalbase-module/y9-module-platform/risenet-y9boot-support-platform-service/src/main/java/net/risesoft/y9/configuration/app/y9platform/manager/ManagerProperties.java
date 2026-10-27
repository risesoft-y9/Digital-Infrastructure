package net.risesoft.y9.configuration.app.y9platform.manager;

import lombok.Getter;
import lombok.Setter;

/**
 * 三员配置
 */
@Getter
@Setter
public class ManagerProperties {

    public static final int MANAGER_MODIFY_PASSWORD_CYCLE_DEFAULT = 7;
    public static final int SYSTEM_MANAGER_REVIEW_LOG_CYCLE_DEFAULT = 0;
    public static final int SECURITY_MANAGER_REVIEW_LOG_CYCLE_DEFAULT = 7;
    public static final int AUDIT_MANAGER_REVIEW_LOG_CYCLE_DEFAULT = 7;

    /**
     * 三员修改密码周期（天）
     */
    private int managerModifyPasswordCycle = MANAGER_MODIFY_PASSWORD_CYCLE_DEFAULT;

    /**
     * 系统管理员审查日志周期（天）
     */
    private int systemManagerReviewLogCycle = SYSTEM_MANAGER_REVIEW_LOG_CYCLE_DEFAULT;

    /**
     * 安全保密员审查日志周期（天）
     */
    private int securityManagerReviewLogCycle = SECURITY_MANAGER_REVIEW_LOG_CYCLE_DEFAULT;

    /**
     * 安全审计员审查日志周期（天）
     */
    private int auditManagerReviewLogCycle = AUDIT_MANAGER_REVIEW_LOG_CYCLE_DEFAULT;
}
