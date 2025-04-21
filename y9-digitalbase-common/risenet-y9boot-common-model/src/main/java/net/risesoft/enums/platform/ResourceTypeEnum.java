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
    APP(0, "应用"),
    /** 菜单 */
    MENU(1, "菜单"),
    /** 按钮 */
    OPERATION(2, "按钮"),
    /** 数据目录 */
    DATA_CATALOG(3, "数据目录"),;

    private final Integer value;
    private final String name;
}
