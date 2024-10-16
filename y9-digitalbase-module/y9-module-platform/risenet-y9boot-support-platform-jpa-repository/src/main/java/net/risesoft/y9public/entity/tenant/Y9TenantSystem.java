package net.risesoft.y9public.entity.tenant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;

/**
 * 租户系统表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_COMMON_TENANT_SYSTEM")
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "租户系统表", appliesTo = "Y9_COMMON_TENANT_SYSTEM")
@NoArgsConstructor
@Data
public class Y9TenantSystem extends BaseEntity {

    private static final long serialVersionUID = 2518632210460709909L;

    /** 主键id */
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("主键id")
    private String id;

    /** 租户id */
    @Column(name = "TENANT_ID", length = 38, nullable = false)
    @Comment("租户id")
    private String tenantId;

    /** 系统id */
    @Column(name = "SYSTEM_ID", length = 38, nullable = false)
    @Comment("系统id")
    private String systemId;

    /** 数据源id */
    @Column(name = "TENANT_DATA_SOURCE", length = 38, nullable = false)
    @Comment("数据源id")
    private String tenantDataSource;

    /** 租户数据已经初始化 */
    @Type(type = "numeric_boolean")
    @Column(name = "INITIALIZED", nullable = false)
    @Comment("租户数据已经初始化")
    @ColumnDefault("0")
    private Boolean initialized = false;

    /** 系统名称 */
    @Transient
    private String systemName;

    /** 系统中文名称 */
    @Transient
    private String systemCnName;

    /** 租户数据源名称 */
    @Transient
    private String tenantDataSourceName;

}
