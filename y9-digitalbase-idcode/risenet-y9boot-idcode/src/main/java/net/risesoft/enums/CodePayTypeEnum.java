package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 申请码类型
 *
 * @author qinman
 * @date 2024/5/23
 */
@Getter
@AllArgsConstructor
public enum CodePayTypeEnum {
    /**
     * 注册
     */
    REGISTER(2, "注册"),
    /**
     * 备案
     */
    FILING(5, "备案");

    private final Integer value;
    private final String name;
}
