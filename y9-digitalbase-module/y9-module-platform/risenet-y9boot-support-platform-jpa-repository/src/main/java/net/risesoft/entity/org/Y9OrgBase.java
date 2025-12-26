package net.risesoft.entity.org;

import java.util.Comparator;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Type;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import net.risesoft.base.BaseTenantEntity;
import net.risesoft.consts.DefaultConsts;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.persistence.EnumConverter;

/**
 * 组织基类
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
public abstract class Y9OrgBase extends BaseTenantEntity implements Comparable<Y9OrgBase> {

    private static final long serialVersionUID = 4564661506322616943L;

    /** 唯一id */
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("UUID字段")
    protected String id;

    /** 自定义id */
    @Column(name = "CUSTOM_ID", length = 255)
    @Comment("自定义id")
    protected String customId;

    /** 用来支持乐观锁，可以不用手动设置 */
    @Column(name = "VERSION")
    @Comment("用来支持乐观锁，可以不用手动设置")
    @Version
    protected Integer version;

    /** 是否禁用 */
    @Type(type = "numeric_boolean")
    @Column(name = "DISABLED", nullable = false)
    @Comment("是否禁用")
    @ColumnDefault("0")
    @Builder.Default
    protected Boolean disabled = false;

    /** 描述 */
    @Column(name = "DESCRIPTION", length = 255)
    @Comment("描述")
    protected String description;

    /** 由name组成的父子关系列表(倒序)，之间用逗号分隔 */
    @Column(name = "DN", length = 2000)
    @Comment("由name组成的父子关系列表(倒序)，之间用逗号分隔")
    protected String dn;

    /** 名称 */
    @Column(name = "NAME", length = 255, nullable = false)
    @Comment("名称")
    protected String name;

    /** 组织类型 */
    @Column(name = "ORG_TYPE", length = 255, nullable = false)
    @Comment("组织类型")
    @Convert(converter = EnumConverter.OrgTypeEnumConverter.class)
    protected OrgTypeEnum orgType;

    /** 扩展属性 */
    @Column(name = "PROPERTIES", length = 500)
    @Comment("扩展属性")
    protected String properties;

    /** 排序号 */
    @Column(name = "TAB_INDEX", nullable = false)
    @Comment("排序号")
    @Builder.Default
    protected Integer tabIndex = DefaultConsts.TAB_INDEX;

    /** 由ID组成的父子关系列表(正序)，之间用逗号分隔 */
    @Column(name = "GUID_PATH", unique = true, length = 400)
    @Comment("由ID组成的父子关系列表(正序)，之间用逗号分隔")
    protected String guidPath;

    @Override
    public int compareTo(Y9OrgBase o) {
        return Comparator.comparing(Y9OrgBase::getParentId, Comparator.nullsFirst(String::compareTo))
            .thenComparing(Y9OrgBase::getTabIndex)
            .compare(this, o);
    }

    public abstract String getParentId();
}