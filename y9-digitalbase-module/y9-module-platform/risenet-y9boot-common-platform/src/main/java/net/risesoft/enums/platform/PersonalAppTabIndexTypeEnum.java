package net.risesoft.enums.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

import net.risesoft.enums.ValuedEnum;

/**
 * 人员应用排序号类型
 *
 * @author shidaobang
 * @date 2026/05/20
 */
@Getter
@AllArgsConstructor
public enum PersonalAppTabIndexTypeEnum implements ValuedEnum<Integer> {
    DEFAULT(1, "配置的默认排序"),
    PERSONAL(2, "个人排序");

    private final Integer value;
    private final String name;
}
