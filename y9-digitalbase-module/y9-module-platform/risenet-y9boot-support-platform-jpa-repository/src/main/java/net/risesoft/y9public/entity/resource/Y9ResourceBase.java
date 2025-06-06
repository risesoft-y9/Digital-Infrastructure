package net.risesoft.y9public.entity.resource;

import java.util.Comparator;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import net.risesoft.base.BaseEntity;
import net.risesoft.consts.DefaultConsts;
import net.risesoft.enums.platform.ResourceTypeEnum;
import net.risesoft.persistence.EnumConverter;

/**
 * 资源基类
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@MappedSuperclass
@NoArgsConstructor
@Data
@SuperBuilder
public abstract class Y9ResourceBase extends BaseEntity implements Comparable<Y9ResourceBase> {

    private static final long serialVersionUID = -1618076945780899968L;

    /** 主键 */
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("主键")
    protected String id;

    /** 自定义id */
    @Column(name = "CUSTOM_ID", length = 500)
    @Comment("自定义id")
    protected String customId;

    /** 系统id */
    @NotBlank
    @Column(name = "SYSTEM_ID", length = 38, nullable = false)
    @Comment("系统id")
    protected String systemId;

    /** 名称 */
    @NotBlank
    @Column(name = "NAME", length = 255, nullable = false)
    @Comment("名称")
    protected String name;

    /** 描述 */
    @Column(name = "DESCRIPTION", length = 255)
    @Comment("描述")
    protected String description;

    /** 是否启用:1=启用,0=禁用 */
    @Type(type = "numeric_boolean")
    @ColumnDefault("1")
    @Column(name = "ENABLED", nullable = false)
    @Comment("是否启用:1=启用,0=禁用")
    protected Boolean enabled = Boolean.TRUE;

    /** 是否隐藏:1=隐藏,0=显示 */
    @Type(type = "numeric_boolean")
    @ColumnDefault("0")
    @Column(name = "HIDDEN", nullable = false)
    @Comment("是否隐藏:1=隐藏,0=显示")
    protected Boolean hidden = Boolean.FALSE;

    /** 图标地址 */
    @Column(name = "ICON_URL", length = 400)
    @Comment("图标地址")
    protected String iconUrl;

    /** 链接地址 */
    @Column(name = "URL", length = 400)
    @Comment("链接地址")
    protected String url;

    /** 链接地址2 */
    @Column(name = "URL2", length = 400)
    @Comment("链接地址2")
    protected String url2;

    /** 资源类型：0=应用，1=菜单，2=操作 */
    @ColumnDefault("0")
    @Column(name = "RESOURCE_TYPE", nullable = false)
    @Comment("资源类型：0=应用，1=菜单，2=操作")
    @Convert(converter = EnumConverter.ResourceTypeEnumConverter.class)
    protected ResourceTypeEnum resourceType = ResourceTypeEnum.APP;

    /** 是否为继承上级节点的权限:1=继承,0=不继承 */
    @Type(type = "numeric_boolean")
    @ColumnDefault("0")
    @Column(name = "INHERIT", nullable = false)
    @Comment("是否为继承上级节点的权限:1=继承,0=不继承")
    protected Boolean inherit = Boolean.FALSE;

    /** 排序 */
    @Column(name = "TAB_INDEX", nullable = false)
    @Comment("排序")
    protected Integer tabIndex = DefaultConsts.TAB_INDEX;

    /** 由ID组成的父子关系列表(正序)，之间用逗号分隔 */
    @Column(name = "GUID_PATH", unique = true, length = 400)
    @Comment("由ID组成的父子关系列表(正序)，之间用逗号分隔")
    protected String guidPath;

    @Override
    public int compareTo(Y9ResourceBase y9ResourceBase) {
        // 排序时能保证同系统中同一层级（parentId 相同）的资源能按 tabIndex 升序排列
        return Comparator.comparing(Y9ResourceBase::getSystemId)
            .thenComparing(Y9ResourceBase::getParentId, Comparator.nullsFirst(String::compareTo))
            .thenComparing(Y9ResourceBase::getTabIndex).compare(this, y9ResourceBase);
    }

    public abstract String getAppId();

    public abstract String getParentId();
}