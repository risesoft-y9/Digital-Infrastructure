package net.risesoft.enums.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

import net.risesoft.enums.ValuedEnum;

/**
 * 应用打开方式
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/6/06
 */
@Getter
@AllArgsConstructor
public enum AppOpenTypeEnum implements ValuedEnum<Integer> {
    /** 桌面窗口 */
    DESKTOP(0, "桌面窗口"),
    /** 新浏览器窗口 */
    BROWSE(1, "新浏览器窗口");

    private final Integer value;
    private final String name;
}
