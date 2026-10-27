package net.risesoft.y9.configuration.app.y9platform.datacatalog;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 数据目录属性
 */
@Getter
@Setter
public class DataCatalogProperties {

    /**
     * 档案保留期限
     */
    private List<String> retentionPeriods = List.of("永久", "30", "10");

    /**
     * 档案保密期限
     */
    private List<String> confidentialityPeriods = List.of("10", "20", "30");

}
