package net.risesoft.enums.platform.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;

import net.risesoft.enums.ValuedEnum;

/**
 * 数据目录类型
 *
 * @author shidaobang
 * @date 2024/10/09
 * @since 9.6.8
 */
@Getter
@AllArgsConstructor
public enum DataCatalogTypeEnum implements ValuedEnum<String> {
    /**
     * 数据分类目录
     */
    CLASSIFICATION("classification"),
    /**
     * 年份目录
     */
    YEAR("year"),
    /**
     * 组织节点
     */
    ORG_UNIT("orgUnit"),
    /**
     * 保存期限目录
     */
    RETENTION_PERIOD("retentionPeriod"),
    /**
     * 保密期限目录
     */
    CONFIDENTIALITY_PERIOD("confidentialityPeriod");

    private final String value;
}
