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
public enum ItemWordTypeEnum {
    /** 未套红文件 */
    WORD("0", "未套红文件"),
    /** 红头文件 */
    REDHEADWORD("1", "红头文件"),
    /** jsp */
    PDF("2", "流式文件"),
    // FIXME
    /** quikui */
    PDF1("3", "PDF1"),
    /** freemarker */
    PDF2("4", "PDF1");

    private final String value;
    private final String name;
}
