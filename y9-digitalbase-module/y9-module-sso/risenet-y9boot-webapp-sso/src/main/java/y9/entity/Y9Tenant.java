package y9.entity;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

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
@DynamicUpdate
@Comment("租户信息表")
@NoArgsConstructor
@Data
public class Y9Tenant implements Serializable {

    private static final long serialVersionUID = 2987678891147576268L;

    /** 主键id */
    @Id
    @Column(name = "ID", length = 38)
    @Comment("主键id")
    private String id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Comment("创建时间")
    @CreationTimestamp
    @Column(name = "CREATE_TIME", updatable = false)
    private Instant createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Comment("更新时间")
    @UpdateTimestamp
    @Column(name = "UPDATE_TIME")
    private Instant updateTime;

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
