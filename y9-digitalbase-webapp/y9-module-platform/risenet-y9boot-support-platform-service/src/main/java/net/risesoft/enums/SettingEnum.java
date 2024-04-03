package net.risesoft.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import net.risesoft.y9.configuration.app.y9platform.Y9PlatformProperties;

/**
 * 默认设置枚举
 *
 * @author shidaobang
 * @date 2024/03/28
 */
@Getter
@RequiredArgsConstructor
public enum SettingEnum {
    /** 三员修改密码周期 */
    MANAGER_MODIFY_PASSWORD_CYCLE("managerModifyPasswordCycle", Integer.class,
        Y9PlatformProperties.MANAGER_MODIFY_PASSWORD_CYCLE_DEFAULT),
    /** 系统管理员审查日志周期 */
    SYSTEM_MANAGER_REVIEW_LOG_CYCLE("systemManagerReviewLogCycle", Integer.class,
        Y9PlatformProperties.SYSTEM_MANAGER_REVIEW_LOG_CYCLE_DEFAULT),
    /** 安全保密员审查日志周期 */
    SECURITY_MANAGER_REVIEW_LOG_CYCLE("securityManagerReviewLogCycle", Integer.class,
        Y9PlatformProperties.SECURITY_MANAGER_REVIEW_LOG_CYCLE_DEFAULT),
    /** 安全审计员审查日志周期 */
    AUDIT_MANAGER_REVIEW_LOG_CYCLE("auditManagerReviewLogCycle", Integer.class,
        Y9PlatformProperties.AUDIT_MANAGER_REVIEW_LOG_CYCLE_DEFAULT),
    /** 用户默认密码 */
    USER_DEFAULT_PASSWORD("userDefaultPassword", String.class, Y9PlatformProperties.USER_PASSWORD_DEFAULT);

    private final String key;
    private final Class<?> clazz;
    private final Object defaultValue;

    public static SettingEnum getByKey(String key) {
        for (SettingEnum settingEnum : SettingEnum.values()) {
            if (settingEnum.getKey().equals(key)) {
                return settingEnum;
            }
        }
        return null;
    }
}
