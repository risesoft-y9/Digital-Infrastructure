package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流程状态
 * 
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum ItemProcessStateTypeEnum {
    /** 激活状态 */
    ACTIVE("ative", "激活状态"),
    /** 文本 */
    SUSPEND("suspend", "挂起状态");

    private final String value;
    private final String name;
}
