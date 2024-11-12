package net.risesoft.y9.configuration.app.y9platform;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "y9.app.platform", ignoreInvalidFields = true, ignoreUnknownFields = true)
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
     * 岗位名称格式，默认格式为：${jobName}（${personNames}），变量 jobName 会替换为职位名 ，变量 personNames 会替换为人员名称 <br>
     * 最终的岗位名称例子：总经理（张三）
     */
    private String positionNamePattern = "${jobName}（${personNames}）";

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
    private String userDefaultPassword = USER_PASSWORD_DEFAULT;

    /**
     * 初始化租户名称
     */
    private String initTenantName = "default";

    /**
     * 初始化租户数据库 schema
     */
    private String initTenantSchema = "y9_default";

    /**
     * 新建的表空间存储目录 不指定一般会存储在数据库的默认路径下 例子：/u01/app/oracle/oradata/orcl/
     */
    private String newTableSpacePath = "";

    /**
     * 数据目录档案保留期限
     */
    private List<String> dataCatalogRetentionPeriods = List.of("永久", "30", "10");

    /**
     * 数据目录档案保密期限
     */
    private List<String> dataCatalogConfidentialityPeriods = List.of("10", "20", "30");

}
