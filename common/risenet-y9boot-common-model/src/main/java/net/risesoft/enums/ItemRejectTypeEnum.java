package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 拒绝类型
 * 
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum ItemRejectTypeEnum {
    /** 退回 */
    ROLLBACK(1, "退回"),
    /** 收回 */
    TAKEBACK(2, "收回"),
    /** 重定向 */
    REPOSITION(3, "重定向");

    private final Integer value;
    private final String name;
}
