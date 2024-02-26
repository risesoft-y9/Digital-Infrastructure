package net.risesoft.y9.configuration.feature.jpa;

import lombok.Getter;
import lombok.Setter;

/**
 * jpa 配置
 *
 * @author liansen
 * @date 2022/09/26
 */
@Getter
@Setter
public class Y9JpaProperties {

    /**
     * 公共库实体包扫描路径
     */
    private String[] packagesToScanEntityPublic;

    /**
     * 公共库 Repository 包扫描路径
     */
    private String[] packagesToScanRepositoryPublic;

    /**
     * 租户库实体包扫描路径
     */
    private String[] packagesToScanEntityTenant;

    /**
     * 租户库 Repository 包扫描路径
     */
    private String[] packagesToScanRepositoryTenant;

    /**
     * 专用库实体包扫描路径
     */
    private String[] packagesToScanEntityDedicated;

    /**
     * 专用库 Repository 包扫描路径
     */
    private String[] packagesToScanRepositoryDedicated;
}
