package net.risesoft.model.cms;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 内容管理的文章实体类
 *
 * @author mengjuhua
 */
@Data
public class CmsArticle implements Serializable {

    private static final long serialVersionUID = 4523009894244014575L;

    /**
     * 唯一标识
     */
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 短标题
     */
    private String shortTitle;

    /**
     * 发布时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date releaseDate;

    /**
     * 标题颜色
     */
    private String titleColor;

    /**
     * 是否加粗
     */
    private Boolean bold;

    /**
     * 是否置顶
     */
    private Boolean top;

    /**
     * 是否推荐
     */
    private Boolean recommend;

    /**
     * 审核状态，-1：退回中，1：审核中,2：已审核，3：回收站，4：已删除
     */
    private Integer status;

    /**
     * 风格
     */
    private String style;

    /**
     * 新增临时存放所在局名称
     */
    private String tempDepartName;

    /**
     * 是否有评论
     */
    private Boolean hasLeaderComment;

    /**
     * 审核人
     */
    private String authors;

    /**
     * true :部门所在委办局私有 false: 为区级所有
     */
    private Boolean privated;

    /**
     * 委办局的GUID，与平台做整合
     */
    private String bureauId;

    /**
     * 访问路径
     */
    private String url;

    /**
     * 文章缩略图
     */
    private String imgUrl;

    /**
     * 录入员
     */
    private String reporter;

    /**
     * 描述
     */
    private String description;

}
