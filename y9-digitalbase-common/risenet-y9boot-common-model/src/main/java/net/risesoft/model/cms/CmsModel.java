package net.risesoft.model.cms;

import java.io.Serializable;

import lombok.Data;

/**
 * 内容管理的模板信息
 *
 * @author mengjuhua
 *
 */
@Data
public class CmsModel implements Serializable {

    private static final long serialVersionUID = 1235912079830362056L;

    /**
     * 唯一标识
     */
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 图标
     */
    private String icon;

    /**
     * 内容页模板
     */
    private String tplDoc;

    /**
     * 打印页模板
     */
    private String tplPrint;

    /**
     * 搜索页模板
     */
    private String tplSearch;

    /**
     * 高级搜索页模板
     */
    private String tplAdvSearch;

    /**
     * 评论页模板
     */
    private String tplComment;

    /**
     * 排列顺序
     */
    private Integer priority;

    /**
     * 是否启用
     */
    private Boolean disabled;

}