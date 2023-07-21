package net.risesoft.model.calendar.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 日程活动类型
 *
 * @author shidaobang
 */
@AllArgsConstructor
@Getter
public enum ActivityType {
    /** 行程 */
    TRAVEL(0, "行程"),
    /** 会议 */
    MEETING(1, "会议"),
    /** 外出 */
    GOOUT(2, "外出");

    private final Integer value;
    private final String title;
}
