package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
public enum DataSourceTypeEnum {

    /** JNDI */
    JNDI(1),

    /** Hikari */
    HIKARI(2);

    private final Integer value;
}
