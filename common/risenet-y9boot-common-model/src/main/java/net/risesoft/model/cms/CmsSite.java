package net.risesoft.model.cms;

import java.io.Serializable;

import lombok.Data;

/**
 * 内容管理的站点信息表
 *
 * @author mengjuhua
 *
 */
@Data
public class CmsSite implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 站点id
     */
    private Integer id;

    /**
     * 域名
     */
    private String domain;

    /**
     * 访问路径
     */
    private String path;

    /**
     * 名称
     */
    private String name;

    /**
     * 站点简称
     */
    private String shortName;

    /**
     * 上下文路径
     */
    private String contextPath;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 站点风格
     */
    private String tplStyle;

    /**
     * 站点标题
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
     * 是否栏目静态页
     */
    private Integer staticChannel;

    /**
     * 是否文档静态页
     */
    private Integer staticDoc;

    /**
     * 是否有静态页后缀
     */
    private Boolean staticSuffix;

    /**
     * 更新时间
     */
    private java.util.Date updateTime;

    /**
     * 主页模板
     */
    private String tplIndex;

    /**
     * 是否是终点
     */
    private Boolean terminus;

}