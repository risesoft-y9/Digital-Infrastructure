package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事项表单类型
 * 
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum ItemFieldTypeEnum {
    /** 文本 */
    VARCHAR2("VARCHAR2", "文本"),
    /** 文本 */
    VARCHAR("VARCHAR", "文本"),
    /** 整数 */
    INT("INT", "整数"),
    /** 浮点 */
    FLOAT("FLOAT", "浮点"),
    /** 长时间 */
    LONGDATE("LONGDATE", "长时间"),
    /** 时间 */
    DATETIME("DATETIME", "时间"),
    /** 时间 */
    DATE("DATE", "时间");

    private final String value;
    private final String name;
}
