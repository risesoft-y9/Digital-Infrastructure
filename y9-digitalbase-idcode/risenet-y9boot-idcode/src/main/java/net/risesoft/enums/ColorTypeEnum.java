package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 码制
 *
 * @author qinman
 * @date 2024/5/28
 */
@Getter
@AllArgsConstructor
public enum ColorTypeEnum {
    /**
     * Qr
     */
    COLOR(0, "彩色"),
    /**
     * 龙贝
     */
    BLACK(1, "黑色");

    private final Integer value;
    private final String name;
}
