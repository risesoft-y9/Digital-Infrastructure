package net.risesoft.enums.platform.org;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 证件类型
 *
 * @author shidaobang
 * @date 2026/07/16
 */
@Getter
@AllArgsConstructor
public enum IdTypeEnum {
    ID_CARD("10", "身份证"),
    PASSPORT("11", "护照"),
    HOUSEHOLD_REGISTER("12", "户口簿"),
    MILITARY_ID("13", "军人证");

    private final String value;
    private final String name;
}
