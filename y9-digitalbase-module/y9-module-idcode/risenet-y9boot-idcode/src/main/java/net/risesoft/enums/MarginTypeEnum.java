package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 边框样式
 *
 * @author qinman
 * @date 2024/5/28
 */
@Getter
@AllArgsConstructor
public enum MarginTypeEnum {
    /**
     * Qr
     */
    NONE(0, "无边框"),
    /**
     * 龙贝
     */
    CIRCULAR(10, "圆形边框"),
    /**
     * DM码
     */
    SQUARE(20, "方形边框");

    private final Integer value;
    private final String name;
}
