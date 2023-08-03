package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
public enum AppOpenTypeEnum {
    /** 桌面窗口 */
    DESKTOP(0, "桌面窗口"),
    /** 新浏览器窗口 */
    BROWSE(1, "新浏览器窗口");

    private final Integer value;
    private final String name;
}
