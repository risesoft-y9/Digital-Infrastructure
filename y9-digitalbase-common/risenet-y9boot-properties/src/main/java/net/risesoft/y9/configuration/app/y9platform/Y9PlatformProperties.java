package net.risesoft.y9.configuration.app.y9platform;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Y9PlatformProperties {

    public static final int MANAGER_MODIFY_PASSWORD_CYCLE_DEFAULT = 7;
    public static final int SYSTEM_MANAGER_REVIEW_LOG_CYCLE_DEFAULT = 0;
    public static final int SECURITY_MANAGER_REVIEW_LOG_CYCLE_DEFAULT = 7;
    public static final int AUDIT_MANAGER_REVIEW_LOG_CYCLE_DEFAULT = 7;
    public static final String USER_PASSWORD_DEFAULT = "Risesoft@2022";

    /**
     * 系统名称
     */
    private String systemName = "riseplatform";
    /**
     * 是否检查权限
     */
    private boolean checkPermission = false;
    /**
     * 是否启用ip地址白名单
     */
    private boolean enableIpAddressWhiteList = false;
    /**
     * 基于角色访问控制
     */
    private boolean strictRoleBasedAccessControll = true;
    /**
     * 岗位名称格式，默认格式为 职位名称（人员名称），例 总经理（张三） {0} 职位名称 {1} 人员名称
     */
    private String positionNamePattern = "{0}（{1}）";

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

    /**
     * 默认密码
     */
    private String defaultPassword = USER_PASSWORD_DEFAULT;
}
