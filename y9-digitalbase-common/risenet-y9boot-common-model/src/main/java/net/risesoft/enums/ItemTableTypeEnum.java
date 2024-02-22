package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事项表类型
 * 
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum ItemTableTypeEnum {
    /** 主表 */
    MAIN(1, "主表"),
    /** 子表 */
    SUB(2, "子表"),
    /** 字典表 */
    OPTION(3, "字典表");

    private final Integer value;
    private final String name;
}
