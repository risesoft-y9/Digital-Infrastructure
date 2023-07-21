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
public enum ItemFormTemplateTypeEnum {
    /** html */
    HTML(1, "html"),
    /** word */
    WORD(2, "word"),
    /** excel */
    EXCEL(3, "excel"),
    /** pdf */
    PDF(4, "pdf");

    private final Integer value;
    private final String name;
}
