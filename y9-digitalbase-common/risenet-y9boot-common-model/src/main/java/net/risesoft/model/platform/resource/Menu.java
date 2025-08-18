package net.risesoft.model.platform.resource;

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
    private String appId;

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
}
