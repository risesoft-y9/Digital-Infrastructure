package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 工单处理类型
 * 
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum WorkOrderHandleTypeEnum {
    /** 未处理 */
    UNHANDLE("1", "未处理");

    private final String value;
    private final String name;
}
