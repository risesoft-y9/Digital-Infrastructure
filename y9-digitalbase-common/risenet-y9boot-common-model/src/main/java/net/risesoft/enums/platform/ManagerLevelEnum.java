package net.risesoft.enums.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.risesoft.enums.ValuedEnum;

/**
 * 管理员类型
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Getter
@AllArgsConstructor
public enum ManagerLevelEnum implements ValuedEnum<Integer> {
    /** 一般用户 */
    GENERAL_USER(0, "一般用户"),
    /** 系统管理员 */
    SYSTEM_MANAGER(1, "系统管理员"),
    /** 安全保密员 */
    SECURITY_MANAGER(2, "安全保密员"),
    /** 安全审计员 */
    AUDIT_MANAGER(3, "安全审计员"),
    /** 云系统管理员 */
    OPERATION_SYSTEM_MANAGER(4, "云系统管理员"),
    /** 云安全保密员 */
    OPERATION_SECURITY_MANAGER(5, "云安全保密员"),
    /** 云安全审计员 */
    OPERATION_AUDIT_MANAGER(6, "云安全审计员");

    private final Integer value;
    private final String name;

    public boolean isGeneralUser() {
        return value == 0;
    }

    public boolean isTenantManager() {
        return value >= 1 && value <= 3;
    }

    public boolean isOperationManager() {
        return value >= 4 && value <= 6;
    }
}
