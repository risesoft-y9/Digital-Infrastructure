package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 岗位类型
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Getter
@AllArgsConstructor
public enum PositionTypeEnum {
    /** 岗位 */
    POSITION("position"),
    /** 节点 */
    NODE("node");

    private final String name;
}
