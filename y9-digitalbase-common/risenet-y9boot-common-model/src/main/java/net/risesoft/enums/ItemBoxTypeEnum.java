package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 办件列表类型
 * 
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum ItemBoxTypeEnum {
    /** 起草 */
    ADD("add", "起草"),
    /** 草稿 */
    DRAFT("draft", "草稿"),
    /** 待办 */
    TODO("todo", "待办"),
    /** 在办 */
    DOING("doing", "在办"),
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
