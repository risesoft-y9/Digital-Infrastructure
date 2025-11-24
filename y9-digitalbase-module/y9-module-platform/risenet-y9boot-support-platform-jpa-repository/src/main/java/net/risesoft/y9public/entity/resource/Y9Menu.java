package net.risesoft.y9public.entity.resource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import net.risesoft.enums.platform.resource.ResourceTypeEnum;

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
@SuperBuilder
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

    @Override
    public String getAppId() {
        return this.appId;
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }
}
