package net.risesoft.enums.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

import net.risesoft.enums.ValuedEnum;

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
public enum PositionTypeEnum implements ValuedEnum<String> {
    /** 岗位 */
    POSITION("position"),
    /** 节点 */
    NODE("node");

    private final String value;
}
