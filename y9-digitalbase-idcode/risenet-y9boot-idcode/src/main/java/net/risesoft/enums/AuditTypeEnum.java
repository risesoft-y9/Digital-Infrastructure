package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审核类型
 *
 * @author qinman
 * @date 2024/5/23
 */
@Getter
@AllArgsConstructor
public enum AuditTypeEnum {
    /**
     * 单位注册审核
     */
    UNIT_REGISTER(1, "单位注册审核"),
    /**
     * 解析地址审核
     */
    RESOLVE_ADDRESS(2, "解析地址审核");

    private final Integer value;
    private final String name;
}
