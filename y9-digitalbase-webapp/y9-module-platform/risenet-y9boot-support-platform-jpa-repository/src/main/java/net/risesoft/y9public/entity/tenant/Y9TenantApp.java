package net.risesoft.y9public.entity.tenant;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;

/**
 * 租户应用信息表
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_COMMON_TENANT_APP")
@org.hibernate.annotations.Table(comment = "租户应用信息表", appliesTo = "Y9_COMMON_TENANT_APP")
@NoArgsConstructor
@Data
public class Y9TenantApp extends BaseEntity {

    private static final long serialVersionUID = -7321097572177388533L;

    /** 主键id */
    @Id
    @Column(name = "ID", length = 38)
    @Comment("主键id")
    private String id;

    /** 租户id */
    @Column(name = "TENANT_ID", length = 38, nullable = false)
    @Comment("租户id")
    private String tenantId;

    /** 租户名称 */
    @Column(name = "TENANT_NAME", length = 200)
    @Comment("租户名称")
    private String tenantName;

    /** 系统Id */
    @Column(name = "SYSTEM_ID", length = 38, nullable = false)
    @Comment("系统Id")
    private String systemId;

    /** 应用id */
    @Column(name = "APP_ID", length = 38, nullable = false)
    @Comment("应用id")
    private String appId;

    /** 应用名称 */
    @Column(name = "APP_NAME", length = 200)
    @Comment("应用名称")
    private String appName;

    /** 申请人 */
    @Column(name = "APPLY_NAME", length = 200)
    @Comment("申请人")
    private String applyName;

    /** 申请人Id */
    @Column(name = "APPLY_ID", length = 38)
    @Comment("申请人Id")
    private String applyId;

    /** 申请理由 */
    @Column(name = "APPLY_REASON", length = 355)
    @Comment("申请理由")
    private String applyReason;

    /** 审核人 */
    @Column(name = "VERIFY_USER_NAME", length = 200)
    @Comment("审核人")
    private String verifyUserName;

    /** 审核时间 */
    @Column(name = "VERIFY_TIME", length = 50)
    @Comment("审核时间")
    private String verifyTime;

    /** 审核状态，0：未审核；1：通过 */
    @Column(name = "VERIFY_STATUS")
    @Comment("审核状态，0：未审核；1：通过")
    private Boolean verify;

    /** 未通过缘由 */
    @Column(name = "REASON", length = 255)
    @Comment("未通过缘由")
    private String reason;

    /** 租户是否租用状态。用于判断有效或失效的状态 */
    @Type(type = "numeric_boolean")
    @Column(name = "TENANCY", nullable = false)
    @Comment("租户是否租用状态。用于判断有效或失效的状态")
    @ColumnDefault("0")
    private Boolean tenancy = false;

    /** 删除人 */
    @Column(name = "DELETED_NAME")
    @Comment("删除人")
    private String deletedName;

    /** 删除时间 */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DELETED_TIME")
    @Comment("删除时间")
    private Date deletedTime;

    /** 租户应用数据是否已经初始化 */
    @Type(type = "numeric_boolean")
    @Column(name = "INITIALIZED", nullable = false)
    @Comment("租户应用数据是否已经初始化")
    @ColumnDefault("0")
    private Boolean initialized = false;
}
