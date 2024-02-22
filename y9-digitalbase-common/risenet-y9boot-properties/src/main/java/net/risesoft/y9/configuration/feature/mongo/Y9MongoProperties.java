package net.risesoft.y9.configuration.feature.mongo;

import lombok.Getter;
import lombok.Setter;

/**
 * mongo 配置
 *
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
public class Y9MongoProperties {

    /**
     * 公共库实体包扫描路径
     */
    private String packagesToScanEntityPublic;

    /**
     * 公共库 Repository 包扫描路径
     */
    private String packagesToScanRepositoryPublic;

    /**
     * 租户库实体包扫描路径
     */
    private String packagesToScanEntityTenant;

    /**
     * 租户库 Repository 包扫描路径
     */
    private String packagesToScanRepositoryTenant;

}
