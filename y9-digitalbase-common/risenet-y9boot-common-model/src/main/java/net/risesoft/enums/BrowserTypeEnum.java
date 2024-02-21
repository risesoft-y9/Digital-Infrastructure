package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 浏览器类型
 * 
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum BrowserTypeEnum {
    /** IE */
    IE("MSIE", "IE"),
    /** IE6 */
    IE6("MSIE 6.0", "IE6"),
    /** IE7 */
    IE7("MSIE 7.0", "IE7"),
    /** IE8 */
    IE8("MSIE 8.0", "IE8"),
    /** 在办 */
    FIREFOX("Firefox", "火狐"),
    /** 办结 */
    DONE("done", "办结"),
    /** 监控在办 */
    MONITORDOING("monitorDoing", "监控在办"),
    /** 监控办结 */
    MONITORDONE("monitorDone", "监控办结"),
    /** 监控回收站 */
    MONITORRECYCLE("monitorRecycle", "监控回收站"),
    /** 阅件 */
    YUEJIAN("yuejian", "阅件");

    private final String value;
    private final String name;
}
