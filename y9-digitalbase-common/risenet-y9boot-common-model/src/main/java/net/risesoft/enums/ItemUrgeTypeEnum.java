package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 催办类型
 * 
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum ItemUrgeTypeEnum {
    /** 流程办结 */
    SMS("1", "短信"),
    /** 任务完成 */
    EMAIL("2", "邮件"),
    /** 节点到达 */
    SITEINFO("3", "站内信"),
    /** 节点完成 */
    TODOINFO("4", "待办列表中");

    private final String value;
    private final String name;
}
