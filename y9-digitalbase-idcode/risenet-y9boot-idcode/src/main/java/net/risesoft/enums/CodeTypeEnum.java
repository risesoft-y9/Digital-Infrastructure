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
public enum CodeTypeEnum {
    /**
     * Qr
     */
    QR(1, "Qr"),
    /**
     * 龙贝
     */
    LB(2, "龙贝"),
    /**
     * DM码
     */
    DM(3, "DM码"),
    /**
     * 汉信码
     */
    HX(4, "汉信码"),
    /**
     * GM码
     */
    GM(5, "GM码");

    private final Integer value;
    private final String name;
}
