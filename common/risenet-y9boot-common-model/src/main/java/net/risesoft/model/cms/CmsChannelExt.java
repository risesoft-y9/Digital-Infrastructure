package net.risesoft.model.cms;

import java.io.Serializable;

import lombok.Data;

/**
 * 内容管理 的栏目扩展信息表
 *
 * @author mengjuhua
 */
@Data
public class CmsChannelExt implements Serializable {

    private static final long serialVersionUID = 3728961631547861517L;

    /**
     * 唯一标识
     */
    private Integer id;

    /**
     * 外部链接地址
     */
    private String link;

    /**
     * 栏目页模板
     */
    private String tplChannel;

    /**
     * 是否栏目静态页
     */
    private Boolean staticChannel;

    /**
     * 是否文章静态页
     */
    private Boolean staticDoc;

    /**
     * 是否开启评论控制
     */
    private Boolean commentControl;

    /**
     * 是否开启顶踩控制
     */
    private Boolean updownControl;

    /**
     * 打开方式
     */
    private Boolean blank;

    /**
     * 是否签收
     */
    private Boolean sign;

    /**
     * 标题
     */
    private String title;

    /**
     * 关键词
     */
    private String keywords;

    /**
     * 描述
     */
    private String description;

    /**
     * 栏目图标路径
     */
    private String imgSrc;
}