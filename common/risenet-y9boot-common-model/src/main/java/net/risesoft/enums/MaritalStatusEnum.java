package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 婚姻状况
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Getter
@AllArgsConstructor
public enum MaritalStatusEnum {
    /** 保密 */
    SECRET(0),
    /** 已婚 */
    MARRIED(1),
    /** 未婚 */
    UNMARRIED(2);

    private final Integer value;
}