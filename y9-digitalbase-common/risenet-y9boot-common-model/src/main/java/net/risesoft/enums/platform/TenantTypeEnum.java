package net.risesoft.enums.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

import net.risesoft.enums.ValuedEnum;

/**
 * 租户类型
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@AllArgsConstructor
@Getter
public enum TenantTypeEnum implements ValuedEnum<Integer> {
    /** 租户 */
    TENANT("租户", 3);

    private final String name;
    private final Integer value;

}
