package net.risesoft.enums.platform.org;

import lombok.AllArgsConstructor;
import lombok.Getter;

import net.risesoft.enums.ValuedEnum;

/**
 * 性别
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Getter
@AllArgsConstructor
public enum SexEnum implements ValuedEnum<Integer> {
    /** 男 */
    MALE(1, "男"),
    /** 女 */
    FEMALE(0, "女");

    private final Integer value;
    private final String description;
}
