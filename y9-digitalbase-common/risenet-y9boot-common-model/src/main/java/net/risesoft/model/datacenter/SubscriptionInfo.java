package net.risesoft.model.datacenter;

import java.io.Serializable;

import lombok.Data;

/**
 * 部门号发布信息
 *
 */
@Data
public class SubscriptionInfo implements Serializable {

    private static final long serialVersionUID = -4008212373800853754L;

    /**
     * 主键
     */
    private String id;

    /**
     * 部门发布的新闻id
     */
    private String articleId;

    /**
     * 系统英文名称
     */
    private String systemName;

    /**
     * 系统中文名称
     */
    private String systemCnName;

    /**
     * 应用中文名称
     */
    private String appCnName;

    /**
     * 摘要
     */
    private String zy;

    /**
     * 正文
     */
    private String text;

    /**
     * 标题
     */
    private String title;

    /**
     * 标题图片路径
     */
    private String imgSrc;

    /**
     * 作者
     */
    private String from;

    /**
     * 作者头像路径
     */
    private String fromPic;

    /**
     * 发布日期
     */
    private String date;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 文章链接
     */
    private String articleUrl;

    /**
     * 信息公开 1-公开 0-不公开
     */
    private String disabled;

}
