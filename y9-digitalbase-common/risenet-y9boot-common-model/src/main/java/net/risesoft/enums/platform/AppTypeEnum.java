package net.risesoft.enums.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.risesoft.enums.ValuedEnum;

/**
 * 应用类型
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Getter
@AllArgsConstructor
public enum AppTypeEnum implements ValuedEnum<Integer> {
    /** 信息服务 */
    INFORMATION(0, "信息服务"),
    /** 业务协同 */
    BUSINESS_COLLABORATION(1, "业务协同"),
    /** 事项办理 */
    WORKFLOW(2, "事项办理"),
    /** 数据服务 */
    DATA_SERVICE(3, "数据服务");

    private final Integer value;
    private final String name;
}
