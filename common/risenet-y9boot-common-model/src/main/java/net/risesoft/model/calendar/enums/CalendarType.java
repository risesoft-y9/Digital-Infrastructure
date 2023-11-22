package net.risesoft.model.calendar.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 日程类型
 *
 * @author shidaobang
 */
@AllArgsConstructor
@Getter
public enum CalendarType {
    /** 自己创建 */
    OWN(0, "自己创建", "#177cb0"),
    /** 他人分享 */
    SHARED(1, "他人分享", "GREEN"),;

    public static CalendarType getByValue(Integer value) {
        for (CalendarType type : CalendarType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }

    public static String getTitleByValue(Integer value) {
        for (CalendarType type : CalendarType.values()) {
            if (type.getValue().equals(value)) {
                return type.getTitle();
            }
        }
        return null;
    }

    private final Integer value;
    private final String title;
    private final String color;
}
