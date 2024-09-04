package net.risesoft.y9public.entity.role;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.persistence.EnumConverter;

/**
 * 角色表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_ROLE")
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "角色表", appliesTo = "Y9_ORG_ROLE")
@NoArgsConstructor
@Data
public class Y9Role extends BaseEntity implements Comparable<Y9Role> {

    private static final long serialVersionUID = 4830591654511156717L;

    /** 主键 */
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("主键")
    private String id;

    /** 应用id */
    @NotBlank
    @Column(name = "APP_ID", length = 38, nullable = false)
    @Comment("应用id")
    private String appId;

    /** 角色名称 */
    @NotBlank
    @Column(name = "NAME", length = 255, nullable = false)
    @Comment("角色名称")
    private String name;

    /** 描述 */
    @Column(name = "DESCRIPTION", length = 255)
    @Comment("描述")
    private String description;

    /** 自定义id */
    @Column(name = "CUSTOM_ID", length = 255)
    @Comment("customId")
    private String customId;

    /** 名称组成的父子关系列表，之间用逗号分隔 */
    @Column(name = "DN", length = 2000)
    @Comment("名称组成的父子关系列表，之间用逗号分隔")
    private String dn;

    /** 由ID组成的父子关系列表，之间用逗号分隔 */
    @Column(name = "GUID_PATH", length = 1200)
    @Comment("由ID组成的父子关系列表，之间用逗号分隔")
    private String guidPath;

    /** 扩展属性 */
    @Column(name = "PROPERTIES", length = 500)
    @Comment("扩展属性")
    private String properties;

    /** 节点类型 */
    @ColumnDefault("'role'")
    @Column(name = "TYPE", length = 255, nullable = false)
    @Comment("类型：role、folder")
    @Convert(converter = EnumConverter.RoleTypeEnumConverter.class)
    private RoleTypeEnum type = RoleTypeEnum.ROLE;

    /** 应用中文名称，冗余字段，仅用于显示 */
    @Column(name = "APP_CN_NAME", length = 255, nullable = false)
    @Comment("应用中文名称，冗余字段，仅用于显示")
    private String appCnName;

    /** 系统名称，冗余字段，仅用于显示 */
    @Column(name = "SYSTEM_NAME", length = 255, nullable = false)
    @Comment("系统名称，冗余字段，仅用于显示")
    private String systemName;

    /** 系统中文名称，冗余字段，仅用于显示 */
    @Column(name = "SYSTEM_CN_NAME", length = 255, nullable = false)
    @Comment("系统中文名称，冗余字段，仅用于显示")
    private String systemCnName;

    /** 租户id，如设置了表示是租户特有角色 */
    @Column(name = "TENANT_ID", length = 255)
    @Comment("租户id，如设置了表示是租户特有角色")
    private String tenantId;

    /** 动态角色 */
    @Type(type = "numeric_boolean")
    @ColumnDefault("0")
    @Column(name = "DYNAMIC", nullable = false)
    @Comment("动态角色")
    private Boolean dynamic = false;

    /** 父节点ID */
    @Column(name = "PARENT_ID", length = 38)
    @Comment("父节点ID")
    private String parentId;

    /** 序列号 */
    @Column(name = "TAB_INDEX", nullable = false)
    @Comment("序列号")
    private Integer tabIndex = 0;

    @Override
    public int compareTo(Y9Role o) {
        return this.tabIndex.compareTo(o.getTabIndex());
    }

}