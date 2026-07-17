package net.risesoft.vo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 应用分类 vo
 *
 * @author shidaobang
 * @date 2026/05/19
 */
@Getter
@Setter
public class AppCategoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    private String id;

    /** 应用所属分类id */
    private String categoryId;

    /** 应用id */
    private String appId;

    /** 应用名 */
    private String appName;

    /** 应用 URL */
    private String appUrl;

    /** 排序号 */
    private Integer tabIndex;
}
