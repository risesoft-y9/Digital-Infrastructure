package net.risesoft.model.platform.resource;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * 菜单资源
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
public class Menu extends Resource {

    private static final long serialVersionUID = -6176260086899514125L;

    /**
     * 应用id
     */
    @NotBlank
    private String appId;

    /**
     * 父节点ID
     */
    private String parentId;

    /**
     * 打开模式
     */
    private String target;

    /**
     * 菜单部件
     */
    private String component;

    /**
     * 元信息
     */
    private String meta;

    /**
     * 重定向
     */
    private String redirect;

    @Override
    public String getAppId() {
        return this.appId;
    }

    @Override
    public String getParentId() {
        return parentId;
    }
}
