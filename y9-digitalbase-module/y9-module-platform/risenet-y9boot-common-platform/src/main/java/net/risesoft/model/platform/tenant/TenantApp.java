package net.risesoft.model.platform.tenant;

import java.io.Serializable;
import java.util.Date;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

/**
 * 租户应用
 * 
 * @author shidaobang
 * @date 2023/08/22
 * @since 9.6.3
 */
@Data
public class TenantApp implements Serializable {

    private static final long serialVersionUID = -8865793827133987927L;

    /** 主键id */
    private String id;

    /** 租户id */
    @NotBlank
    private String tenantId;

    /** 租户名称 */
    private String tenantName;

    /** 系统Id */
    private String systemId;

    /** 应用id */
    @NotBlank
    private String appId;

    /** 应用名称 */
    private String appName;

    /** 申请人 */
    private String applyName;

    /** 申请人Id */
    private String applyId;

    /** 申请理由 */
    private String applyReason;

    /** 审核人 */
    private String verifyUserName;

    /** 审核时间 */
    private String verifyTime;

    /** 审核状态，0：未审核；1：通过 */
    private Boolean verify;

    /** 未通过缘由 */
    private String reason;

    /** 租户是否租用状态。用于判断有效或失效的状态 */
    private Boolean tenancy;

    /** 删除人 */
    private String deletedName;

    /** 删除时间 */
    private Date deletedTime;

    /** 租户应用数据是否已经初始化 */
    private Boolean initialized;

}
