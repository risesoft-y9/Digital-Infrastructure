package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
public enum ResourceTypeEnum {
    /** 应用 */
    APP(0),
    /** 菜单 */
    MENU(1),
    /** 操作 */
    OPERATION(2);

    private final Integer value;
}
