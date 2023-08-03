package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
public enum SexEnum {
    /** 男 */
    MALE(1, "男"),
    /** 女 */
    FEMALE(0, "女");

    private final Integer value;
    private final String description;
}
