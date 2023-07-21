package net.risesoft.model.subscription;

import java.io.Serializable;

import lombok.Data;

/**
 * 文章详情
 *
 * @author shidaobang
 * @date 2022/12/29
 */
@Data
public class ArticleModel implements Serializable {

    private static final long serialVersionUID = -6336732766550623975L;

    /**
     * 唯一标识
     */
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 正文
     */
    private String text;

    /**
     * 标题图片url
     */
    private String imgUrl;

    /**
     * 创建日期
     */
    private Long createDate;

    /**
     * 创建日期 yyyy-MM-dd HH:mm:ss
     */
    private String createDateStr;

    /**
     * 部门号
     */
    private String deptAccountName;

    /**
     * 部门号头像url
     */
    private String deptAccountAvatar;

    /**
     * 阅读数
     */
    private Integer readCount;

    /**
     * 是否置顶
     */
    private Boolean top = false;

    /**
     * 是否推荐
     */
    private Boolean recommended = false;

    /**
     * 所属主题
     */
    private String theme;

    /**
     * 文章链接
     */
    private String articleUrl;

    /**
     * 发布日期
     */
    private Long publishDate;

    /**
     * 发布日期 yyyy-MM-dd HH:mm:ss
     */
    private String publishDateStr;

    /**
     * 发布日期
     */
    private String publishDateDesc;

    /**
     * 分享人
     */
    private String sharePersonName;

}
