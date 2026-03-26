package net.risesoft.y9public.entity.resource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

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
@Table(name = "Y9_COMMON_MENU")
@DynamicUpdate
@Comment("应用的菜单表")
@Data
@NoArgsConstructor
public class Y9Menu extends Y9ResourceBase {

    private static final long serialVersionUID = 7952871346132443097L;

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

    public Y9Menu(Menu menu, Y9ResourceBase parentResource, Integer nextTabIndex) {
        Y9BeanUtil.copyProperties(menu, this);

        if (StringUtils.isBlank(this.id)) {
            this.id = Y9IdGenerator.genId(IdType.SNOWFLAKE);
        }
        if (this.tabIndex == null || DefaultConsts.TAB_INDEX.equals(this.tabIndex)) {
            this.tabIndex = nextTabIndex;
        }
        rebuildGuidPath(parentResource);
    }

    @Override
    public String getAppId() {
        return this.appId;
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }

    public void update(Menu menu, Y9ResourceBase parentResource) {
        Y9BeanUtil.copyProperties(menu, this);
        rebuildGuidPath(parentResource);
    }
}
