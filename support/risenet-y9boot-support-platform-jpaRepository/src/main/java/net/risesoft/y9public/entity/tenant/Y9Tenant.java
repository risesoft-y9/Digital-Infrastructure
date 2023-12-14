package net.risesoft.y9public.entity.tenant;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;
import net.risesoft.enums.platform.TenantTypeEnum;
import net.risesoft.persistence.EnumConverter;

/**
 * 租户信息表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_COMMON_TENANT")
@Comment("租户信息表")
@NoArgsConstructor
@Data
public class Y9Tenant extends BaseEntity {

    private static final long serialVersionUID = 2987678891147576268L;

    /** 主键id */
    @Id
    @Column(name = "ID", length = 38)
    @Comment("主键id")
    private String id;

    /** 父节点id */
    @Column(name = "PARENT_ID", length = 38)
    @Comment("父节点id")
    private String parentId;

    /** 租户英文名称 */
    @Column(name = "SHORT_NAME", length = 200, nullable = false, unique = true)
    @Comment("租户英文名称")
    private String shortName;

    /** 租户中文名称 */
    @Column(name = "NAME", length = 200, nullable = false, unique = true)
    @Comment("租户中文名称")
    private String name;

    /** 描述 */
    @Column(name = "DESCRIPTION", length = 255)
    @Comment("描述")
    private String description;

    /** 是否启用 */
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @ColumnDefault("1")
    @Column(name = "ENABLED", nullable = false)
    @Comment("是否启用")
    private Boolean enabled = Boolean.TRUE;

    /** 租户类型： 0=超级用户，1=运维团队，2=开发商，3=普通租户 */
    @Column(name = "TENANT_TYPE", nullable = false)
    @Convert(converter = EnumConverter.TenantTypeConverter.class)
    @Comment("租户类型： 0=超级用户，1=运维团队，2=开发商，3=普通租户")
    private TenantTypeEnum tenantType;

    /** 排序号 */
    @Column(name = "TAB_INDEX", nullable = false)
    @Comment("排序号")
    private Integer tabIndex = 0;

    /** 标志图标(保存url地址) */
    @Column(name = "LOGO_ICON", length = 255)
    @Comment("租户logo")
    private String logoIcon;

    /** 门户页尾显示信息 */
    @Column(name = "FOOTER", length = 150)
    @Comment("门户页尾显示信息")
    private String footer;

    /** 默认的租户数据源id，对应Y9_COMMON_DATASOURCE表的id字段 */
    @Column(name = "DEFAULT_DATA_SOURCE_ID", length = 38)
    @Comment("默认的租户数据源id，对应Y9_COMMON_DATASOURCE表的id字段")
    private String defaultDataSourceId;

    /** 由ID组成的父子关系列表，之间用逗号分隔 */
    @Column(name = "GUID_PATH", length = 800)
    @Comment("由ID组成的父子关系列表，之间用逗号分隔")
    private String guidPath;

    /** 由shortName组成的父子关系列表，之间用逗号分隔 */
    @Column(name = "NAME_PATH", length = 800)
    @Comment("由shortName组成的父子关系列表，之间用逗号分隔")
    private String namePath;

}
