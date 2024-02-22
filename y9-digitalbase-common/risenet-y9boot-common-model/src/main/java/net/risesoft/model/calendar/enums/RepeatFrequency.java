package net.risesoft.model.calendar.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 重复频率
 *
 * @author shidaobang
 */
@AllArgsConstructor
@Getter
public enum RepeatFrequency {
    /** 不重复 */
    NOREPEAT(0, "不重复"),
    /** 每天 */
    DAILY(1, "每天"),
    /** 每周 */
    WEEKLY(2, "每周"),
    /** 每月 */
    MONTHLY(3, "每月"),
    /** 每年 */
    YEARLY(4, "每年");

    private final Integer value;
    private final String title;

}
