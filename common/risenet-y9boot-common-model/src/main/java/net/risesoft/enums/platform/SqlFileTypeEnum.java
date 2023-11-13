package net.risesoft.enums.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.risesoft.enums.ValuedEnum;

/**
 * sql 文件类型
 * 
 * @author shidaobang
 * @date 2023/09/22
 * @since 9.6.3
 */
@Getter
@AllArgsConstructor
public enum SqlFileTypeEnum implements ValuedEnum<String> {
    /** 全量 */
    ALL("all"),
    /** 增量 */
    ADD("add");

    private final String value;

}
