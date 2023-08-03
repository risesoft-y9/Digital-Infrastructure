package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息提醒类型
 * 
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum ItemRemindTypeEnum {
    /** 流程办结 */
    PROCESSCOMPLETE("processComplete", "流程办结"),
    /** 任务完成 */
    TASKCOMPLETE("taskComplete", "任务完成"),
    /** 节点到达 */
    NODEARRIVE("nodeArrive", "节点到达"),
    /** 节点完成 */
    NODECOMPLETE("nodeComplete", "节点完成");

    private final String value;
    private final String name;
}
