package net.risesoft.y9public.entity.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.consts.DefaultConsts;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.resource.Menu;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 应用的菜单表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_COMMON_MENU",
    uniqueConstraints = {@UniqueConstraint(name = Y9Menu.UK_CUSTOM_ID, columnNames = {"CUSTOM_ID"}),
        @UniqueConstraint(name = Y9Menu.UK_GUID_PATH, columnNames = {"GUID_PATH"})})
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "应用的菜单表", appliesTo = "Y9_COMMON_MENU")
@Data
@NoArgsConstructor
public class Y9Menu extends Y9ResourceBase {

    private static final long serialVersionUID = 7952871346132443097L;

    public static final String UK_CUSTOM_ID = "UK_MENU_CUSTOM_ID";

    public static final String UK_GUID_PATH = "UK_MENU_GUID_PATH";

    {
        super.setResourceType(ResourceTypeEnum.MENU);
    }

    /** 应用id */
    @Column(name = "APP_ID", length = 38, nullable = false)
    @Comment("应用id")
    private String appId;

    /** 父节点ID */
    @Comment("父节点ID")
    @Column(name = "PARENT_ID", length = 38, nullable = false)
    private String parentId;

    /** 打开模式 */
    @Column(name = "TARGET")
    @Comment("打开模式")
    private String target;

    /** 菜单部件 */
    @Column(name = "COMPONENT", length = 50)
    @Comment("菜单部件")
    private String component;

    /** 重定向 */
    @Column(name = "REDIRECT", length = 50)
    @Comment("重定向")
    private String redirect;

    /** 元信息 */
    @Column(name = "META", length = 500)
    @Comment("元信息")
    private String meta;

    @Override
    public String getAppId() {
        return this.appId;
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }

    @Override
    public String getCustomId() {
        if (StringUtils.equals(this.customId, this.id)) {
            // 对上层隐藏“非必填”的 customId 字段唯一索引的实现细节
            return null;
        }
        return this.customId;
    }

    public Y9Menu(Menu menu, Y9ResourceBase parentResource, Integer nextTabIndex) {
        Y9BeanUtil.copyProperties(menu, this);

        if (StringUtils.isBlank(this.id)) {
            this.id = Y9IdGenerator.genId(IdType.SNOWFLAKE);
        }
        if (StringUtils.isBlank(this.customId)) {
            // customId 字段有唯一约束，不同数据库对唯一约束的允许不一致，此处保证 customId 列始终有值，所有数据库通用
            this.customId = this.id;
        }
        if (this.tabIndex == null || DefaultConsts.TAB_INDEX.equals(this.tabIndex)) {
            this.tabIndex = nextTabIndex;
        }
        rebuildGuidPath(parentResource);
    }

    public void update(Menu menu, Y9ResourceBase parentResource) {
        Y9BeanUtil.copyProperties(menu, this);
        if (StringUtils.isBlank(this.customId)) {
            // customId 字段有唯一约束，不同数据库对唯一约束的允许不一致，此处保证 customId 列始终有值，所有数据库通用
            this.customId = this.id;
        }
        rebuildGuidPath(parentResource);
    }
}
