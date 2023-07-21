package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
public enum AppTypeEnum {
    /** 业务协同 */
    BUSINESS_COLLABORATION(1, "业务协同"),
    /** 事项办理 */
    WORKFLOW(2, "事项办理"),
    /** 数据服务 */
    DATA_SERVICE(3, "数据服务");

    private final Integer value;
    private final String name;
}
