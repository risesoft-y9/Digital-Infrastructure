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
public enum ItemFormTypeEnum {
    /** jeasyui */
    JEASYUI(1, "jeasyui"),
    /** layui */
    LAYUI(2, "layui"),
    /** jsp */
    JSP(3, "jsp"),
    /** quikui */
    QUIKUI(4, "quikui"),
    /** freemarker */
    FREEMARKER(5, "freemarker"),
    /** thymeleaf */
    THYMELEAF(6, "thymeleaf");

    private final Integer value;
    private final String name;
}
