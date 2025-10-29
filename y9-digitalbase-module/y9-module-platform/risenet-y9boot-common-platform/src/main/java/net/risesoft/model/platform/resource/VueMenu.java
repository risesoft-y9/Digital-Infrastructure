package net.risesoft.model.platform.resource;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * vue 的菜单
 *
 * @author shidaobang
 * @date 2022/09/05
 */
@Data
public class VueMenu implements Serializable {
    private static final long serialVersionUID = 6129308144423022536L;

    /** 名称 */
    private String name;

    /** 路径 */
    private String path;

    /** 重定向 */
    private String redirect;

    /** 菜单部件 */
    private String component;

    /** 元信息 */
    private String meta;

    /** 打开位置 */
    private String target;

    /** 子菜单 */
    private List<VueMenu> children;

    /** 子按钮 */
    private List<VueButton> buttons;

}
