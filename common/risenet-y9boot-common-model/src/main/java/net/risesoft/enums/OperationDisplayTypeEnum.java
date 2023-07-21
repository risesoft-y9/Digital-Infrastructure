package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作按钮展示类型
 *
 * @author shidaobang
 * @date 2022/09/19
 */
@Getter
@AllArgsConstructor
public enum OperationDisplayTypeEnum {
    /** 图标文本都展示 */
    ICON_TEXT(0),
    /** 只展示图标 */
    ICON(1),
    /** 只展示文本 */
    TEXT(2);

    private final Integer value;
}
