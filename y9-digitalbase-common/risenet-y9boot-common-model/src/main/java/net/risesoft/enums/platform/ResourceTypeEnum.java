package net.risesoft.enums.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

import net.risesoft.enums.ValuedEnum;

/**
 * 资源类型
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Getter
@AllArgsConstructor
public enum ResourceTypeEnum implements ValuedEnum<Integer> {
    /** 应用 */
    APP(0),
    /** 菜单 */
    MENU(1),
    /** 操作 */
    OPERATION(2);

    private final Integer value;
}
