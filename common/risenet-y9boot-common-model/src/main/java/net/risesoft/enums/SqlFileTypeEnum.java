package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * sql 文件类型
 * 
 * @author shidaobang
 * @date 2023/09/22
 * @since 9.6.3
 */
@Getter
@AllArgsConstructor
public enum SqlFileTypeEnum {
    /** 全量 */
    ALL("all"),
    /** 增量 */
    ADD("add");

    private final String value;

}
