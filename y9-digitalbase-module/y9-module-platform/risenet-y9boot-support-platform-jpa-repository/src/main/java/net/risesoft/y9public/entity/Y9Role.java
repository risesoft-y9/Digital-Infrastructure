package net.risesoft.y9public.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;
import net.risesoft.consts.DefaultConsts;
import net.risesoft.consts.InitDataConsts;
import net.risesoft.consts.RoleLevelConsts;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Role;
import net.risesoft.persistence.EnumConverter;
import net.risesoft.y9.util.Y9BeanUtil;

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
@Comment("角色表")
@NoArgsConstructor
@Data
public class Y9Role extends BaseEntity implements Comparable<Y9Role> {

    private static final long serialVersionUID = 4830591654511156717L;

    /** 主键 */
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("主键")
    private String id;

    /** 应用id 公共角色时为空 */
    @Column(name = "APP_ID", length = 38)
    @Comment("应用id")
    private String appId;

    /** 系统id 公共角色时为空 */
    @Column(name = "SYSTEM_ID", length = 38)
    @Comment("系统id")
    private String systemId;

    /** 角色名称 */
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

    /** 租户id，如设置了表示是租户特有角色 */
    @Column(name = "TENANT_ID", length = 38)
    @Comment("租户id，如设置了表示是租户特有角色")
    private String tenantId;

    /** 动态角色 */
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @ColumnDefault("0")
    @Column(name = "DYNAMIC", nullable = false)
    @Comment("动态角色")
    private Boolean dynamic = false;

    /** 父节点ID，可能为系统、应用、角色 */
    @Column(name = "PARENT_ID", length = 38)
    @Comment("父节点ID，可能为系统、应用、角色")
    private String parentId;

    /** 序列号 */
    @Column(name = "TAB_INDEX", nullable = false)
    @Comment("序列号")
    private Integer tabIndex = DefaultConsts.TAB_INDEX;

    public Y9Role(Role role, Y9Role parentRole, Integer nextTabIndex, String currentTenantId) {
        Y9BeanUtil.copyProperties(role, this);

        if (StringUtils.isBlank(this.id)) {
            this.id = Y9IdGenerator.genId();
        }
        if (DefaultConsts.TAB_INDEX.equals(this.tabIndex)) {
            this.tabIndex = nextTabIndex;
        }

        if (InitDataConsts.OPERATION_TENANT_ID.equals(currentTenantId)) {
            this.tenantId = null;
        } else {
            this.tenantId = currentTenantId;
        }

        rebuildProperties(parentRole);

        // appId systemId 都不为空则当前角色属于应用
        // systemId 不为空则当前角色属于系统
        // appId systemId 都为空则当前角色属于公共角色
    }

    @Override
    public int compareTo(Y9Role o) {
        return this.tabIndex.compareTo(o.getTabIndex());
    }

    public void changeParent(Y9Role parentRole, Integer nextTabIndex) {
        this.tabIndex = nextTabIndex;
        rebuildProperties(parentRole);
    }

    public void changeProperties(String properties) {
        this.properties = properties;
    }

    public void changeTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public void update(Role role, Y9Role parentRole, String currentTenantId) {
        Y9BeanUtil.copyProperties(role, this);

        if (InitDataConsts.OPERATION_TENANT_ID.equals(currentTenantId)) {
            this.tenantId = null;
        } else {
            this.tenantId = currentTenantId;
        }

        rebuildProperties(parentRole);
    }

    private void rebuildProperties(Y9Role parentRole) {
        // FIXME dn 和 guidPath 需要调整，从系统开始？

        if (parentRole != null) {
            this.parentId = parentRole.getId();
            this.dn = RoleLevelConsts.CN + this.name + RoleLevelConsts.SEPARATOR + parentRole.getDn();
            this.guidPath = parentRole.getGuidPath() + RoleLevelConsts.SEPARATOR + this.id;
            return;
        }

        this.dn = RoleLevelConsts.CN + this.name;
        this.guidPath = this.id;
        // this.parentId = null;

        if (StringUtils.isBlank(this.appId)) {
            this.appId = null;
        }

        if (StringUtils.isBlank(this.systemId)) {
            this.systemId = null;
        }
    }
}