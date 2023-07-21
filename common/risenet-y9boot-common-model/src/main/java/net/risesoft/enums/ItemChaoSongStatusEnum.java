package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事项按钮类型类型
 * 
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum ItemChaoSongStatusEnum {
    /** 未阅件 */
    TODO(0, "未阅件"),
    /** 已阅件 */
    DONE(1, "已阅件"),
    /** 发送按钮 */
    OTHER(2, "批阅件");

    private final Integer value;
    private final String name;
}
