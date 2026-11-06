package net.risesoft.y9.configuration.feature.elasticsearch;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * elasticsearch 配置
 * 
 * @author liansen
 * @date 2022/09/26
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "y9.feature.elasticsearch", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class Y9ElasticProperties {

    /**
     * 索引名前缀，包含分隔符，例如 dev_。默认为空，兼容已有索引。
     */
    private String indexNamePrefix = "";

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
