package net.risesoft.model.calendar.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 分享类型
 *
 * @author shidaobang
 */
@AllArgsConstructor
@Getter
public enum ShareType {
    /** 分享 */
    SHARE(0, "分享"),
    /** 代录 */
    LEADER(1, "代录"),
    /** 会议 */
    MEETING(2, "会议");

    private final Integer value;
    private final String title;
}
