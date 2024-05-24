package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 区划级别
 *
 * @author qinman
 * @date 2024/5/24
 */
@Getter
@AllArgsConstructor
public enum AddressLevelEnum {
    /**
     * 省级行政区
     */
    PROVINCE(1, "省级行政区"),
    /**
     *地级行政区
     */
    PREFECTURE(2, "地级行政区"),

    /**
     * 县级行政区
     */
    COUNTY(3, "县级行政区");

    private final Integer value;
    private final String name;
}
