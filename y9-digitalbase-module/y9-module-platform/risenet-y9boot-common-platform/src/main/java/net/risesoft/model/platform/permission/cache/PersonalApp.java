package net.risesoft.model.platform.permission.cache;

import java.io.Serializable;

import lombok.Data;

import net.risesoft.enums.platform.resource.AppOpenTypeEnum;

/**
 * 个人应用缓存
 *
 * @author mengjuhua
 * @date 2022/10/21
 */
@Data
public class PersonalApp implements Serializable {

    private static final long serialVersionUID = -6172641522877416578L;

    /** 应用所属分类（资源）id */
    private String resourceId;

    /** 应用所属分类（资源）名称 */
    private String resourceName;

    /** 应用编号 */
    private String appId;

    /** 应用名称 */
    private String appName;

    /** 应用是否标星 */
    private Boolean star;

    /** 访问地址 */
    private String url;

    /** 图标地址 */
    private String iconUrl;

    /** 图标Base64 */
    private String iconData;

    /** 是否显示数字 */
    private Boolean showNumber;

    /** 数字获取地址 */
    private String numberUrl;

    /** 打开方式 */
    private AppOpenTypeEnum opentype;

    /** 应用排序 */
    private Integer tabIndex;
}
