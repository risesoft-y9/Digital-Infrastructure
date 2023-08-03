package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 租户类型
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Getter
@AllArgsConstructor
public enum TenantTypeEnum {
    /** 超级用户 */
    SUPER("超级用户", 0),
    /** 运维 */
    OPERATION("运维", 1),
    /** 开发商 */
    ISV("开发商", 2),
    /** 租户 */
    TENANT("租户", 3);

    private final String name;
    private final Integer value;
}
