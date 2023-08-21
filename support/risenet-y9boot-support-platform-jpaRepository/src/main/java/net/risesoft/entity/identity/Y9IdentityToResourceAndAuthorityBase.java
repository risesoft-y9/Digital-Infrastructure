package net.risesoft.entity.identity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;
import net.risesoft.enums.AuthorityEnum;
import net.risesoft.enums.ResourceTypeEnum;

/**
 * 人或岗位与（资源、权限）关系表基类
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/2/10
 */
@MappedSuperclass
@NoArgsConstructor
@Data
public abstract class Y9IdentityToResourceAndAuthorityBase extends BaseEntity
    implements Comparable<Y9IdentityToResourceAndAuthorityBase> {

    private static final long serialVersionUID = 5073573498005834150L;

    /** 主键id */
    @Id
    @GenericGenerator(name = "PersonsToResources", strategy = "native")
    @GeneratedValue(generator = "PersonsToResources")
    @Column(name = "ID")
    @Comment("主键id")
    protected Integer id;

    /** 租户id */
    @Column(name = "TENANT_ID", length = 38)
    @Comment("租户id")
    protected String tenantId;

    /** 权限配置id 方便找到权限来源及权限缓存的处理 */
    @Column(name = "AUTHORIZATION_ID", length = 38)
    @Comment("权限配置id")
    protected String authorizationId;

    /**
     * 权限类型
     *
     * {@link AuthorityEnum}
     */
    @Column(name = "AUTHORITY", nullable = false)
    @Comment("权限类型")
    protected Integer authority;

    /** 资源id */
    @Column(name = "RESOURCE_ID", length = 38, nullable = false)
    @Comment("资源id")
    protected String resourceId;

    /** 资源id */
    @Column(name = "RESOURCE_CUSTOM_ID", length = 38)
    @Comment("资源自定义id")
    protected String resourceCustomId;

    /** 资源名称 */
    @Column(name = "RESOURCE_NAME", length = 255)
    @Comment("资源名称")
    protected String resourceName;

    /** 资源是否为继承上级节点的权限。冗余字段，纯显示用 */
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Column(name = "INHERIT", nullable = false)
    @Comment("资源是否为继承上级节点的权限。冗余字段，纯显示用")
    private Boolean inherit;

    /**
     * 资源类型：0=应用，1=菜单，2=操作
     *
     * {@link ResourceTypeEnum}
     */
    @Column(name = "RESOURCE_TYPE", nullable = false)
    @Comment("资源类型：0=应用，1=菜单，2=操作")
    protected Integer resourceType;

    /** 父资源id */
    @Comment("父资源id")
    @Column(name = "PARENT_RESOURCE_ID", length = 38)
    protected String parentResourceId;

    /** 资源url */
    @Column(name = "RESOURCE_URL", length = 255)
    @Comment("资源URL")
    protected String resourceUrl;

    /** 描述 */
    @Column(name = "RESOURCE_DESCRIPTION", length = 255)
    @Comment("描述")
    protected String resourceDescription;

    /** 排序 */
    @Column(name = "RESOURCE_TAB_INDEX", nullable = false)
    @Comment("排序")
    protected Integer resourceTabIndex = 0;

    /** 应用id */
    @Column(name = "APP_ID", length = 38, nullable = false)
    @Comment("应用id")
    protected String appId;

    /** 应用名称 */
    @Column(name = "APP_NAME", length = 255)
    @Comment("应用名称")
    protected String appName;

    /** 系统id */
    @Column(name = "SYSTEM_ID", length = 38, nullable = false)
    @Comment("系统id")
    protected String systemId;

    /** 系统英文名称 */
    @Column(name = "SYSTEM_NAME", length = 255)
    @Comment("系统英文名称")
    protected String systemName;

    /** 系统中文名称 */
    @Column(name = "SYSTEM_CN_NAME", length = 255)
    @Comment("系统中文名称")
    protected String systemCnName;

    @Override
    public int compareTo(Y9IdentityToResourceAndAuthorityBase o) {
        int value = this.systemId.compareTo(o.systemId);
        return value == 0 ? this.appId.compareTo(o.appId) : value;
    }
}
