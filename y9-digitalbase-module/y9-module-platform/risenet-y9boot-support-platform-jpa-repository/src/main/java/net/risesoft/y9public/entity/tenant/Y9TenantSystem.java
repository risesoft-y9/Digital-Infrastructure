package net.risesoft.y9public.entity.tenant;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

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
@Comment("租户系统表")
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
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Column(name = "INITIALIZED", nullable = false)
    @Comment("租户数据已经初始化")
    @ColumnDefault("0")
    private Boolean initialized = false;

}
