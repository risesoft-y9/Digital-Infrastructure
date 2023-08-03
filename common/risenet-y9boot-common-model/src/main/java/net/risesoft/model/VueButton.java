package net.risesoft.model;

import java.io.Serializable;

import lombok.Data;

/**
 * vue 按钮
 *
 * @author shidaobang
 * @date 2022/09/16
 */
@Data
public class VueButton implements Serializable {
    private static final long serialVersionUID = -2080919486450511351L;

    /** 名称 */
    private String name;

    /** 图标 */
    private String icon;

    /** 按钮自定义id */
    private String buttonId;

    /** 链接地址 */
    private String url;

    /** 事件名称 */
    private String eventName;

    /**
     * 展示方式
     * 
     * {@link net.risesoft.enums.OperationDisplayTypeEnum}
     */
    private Integer displayType;
}
