package net.risesoft.query.platform;

import lombok.Builder;
import lombok.Data;

/**
 * 租户app查询
 *
 * @author mengjuhua
 * @date 2025/10/24
 */
@Data
@Builder
public class TenantAppQuery {

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 租户名称
     */
    private String appName;

    /**
     * 系统ids
     */
    private String systemIds;

    /**
     * 审核状态，0：未审核；1：通过
     */
    private Boolean verify;

    /**
     * 租户是否租用状态。用于判断有效或失效的状态
     */
    private Boolean tenancy;
    /**
     * 审核查询 开始时间
     */
    private String createTime;

    /**
     * 审核查询 结束时间
     */
    private String verifyTime;

}
