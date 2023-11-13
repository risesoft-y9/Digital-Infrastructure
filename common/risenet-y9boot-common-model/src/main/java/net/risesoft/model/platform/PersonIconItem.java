package net.risesoft.model.platform;

import java.io.Serializable;

import lombok.Data;

/**
 * 个人图标缓存
 *
 * @author mengjuhua
 * @date 2022/10/21
 */
@Data
public class PersonIconItem implements Serializable {

    private static final long serialVersionUID = -6172641522877416578L;

    /** UUID字段 */
    private String id;

    /** 系统Id */
    private String systemId;

    /** 系统名称 */
    private String systemName;

    /** 应用编号 */
    private String appId;

    /** 应用名称 */
    private String appName;

    /** 图标类别 1:普通的 2:常用图标 */
    private Integer iconType = 1;

    /** 是否显示，0代表不显示，1代表显示 */
    private Boolean showAble = true;

    /** 访问地址 */
    private String url;

    /** 图标地址 */
    private String iconUrl;

    /** 图标Base64 */
    private String iconData;

    /** 是否显示数字 */
    private Boolean showNumber = false;

    /** 数字获取地址 */
    private String numberUrl;

    /** 打开方式 */
    private String opentype;

    /** 应用排序 */
    private Integer tabIndex;
}
