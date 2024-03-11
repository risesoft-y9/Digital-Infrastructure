package net.risesoft.enums.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

import net.risesoft.enums.ValuedEnum;

/**
 * 数据源类型
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Getter
@AllArgsConstructor
public enum DataSourceTypeEnum implements ValuedEnum<Integer> {
    /** JNDI */
    JNDI(1),

    /** Hikari */
    HIKARI(2);

    private final Integer value;
}
