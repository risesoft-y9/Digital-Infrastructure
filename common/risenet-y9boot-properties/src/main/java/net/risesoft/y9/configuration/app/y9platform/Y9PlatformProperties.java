package net.risesoft.y9.configuration.app.y9platform;

import java.time.Period;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Y9PlatformProperties {

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

    /** 三员修改密码周期 */
    private Period managerModifyPasswordCycle = Period.ofDays(7);

    /** 系统管理员审查日志周期 */
    private Period systemManagerReviewLogCycle = Period.ofDays(0);

    /** 安全保密员审查日志周期 */
    private Period securityManagerReviewLogCycle = Period.ofDays(7);

    /** 安全审计员审查日志周期 */
    private Period AuditManagerReviewLogCycle = Period.ofDays(7);
}
