package net.risesoft.model.cms;

import java.io.Serializable;

import lombok.Data;

/**
 * 内容管理的栏目实体类
 *
 * @author mengjuhua
 */
@Data
public class CmsChannel implements Serializable {

    private static final long serialVersionUID = -3006469356001136707L;

    /**
     * 唯一标识
     */
    private Integer id;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 父节点id
     */
    private Integer parentId;

    /**
     * 栏目名称
     */
    private String name;

    /**
     * 栏目路径
     */
    private String path;

    /**
     * 栏目序号 例如：-id-
     */
    private String number;

    /**
     * 排列顺序
     */
    private Integer priority;

    /**
     * 是否单页
     */
    private Boolean alone;

    /**
     * 是否首页显示
     */
    private Boolean show;

    /**
     * tag标签
     */
    private String tag;

    /**
     * 记录应用的id（与平台整合，即平台的应用id）
     */
    private String customId;

    /**
     * 附件路径
     */
    private String attPath;

    /**
     * 附件名称
     */
    private String attName;

    /**
     * 本栏目能够使用的文档类型id列表，id之间以逗号分隔
     */
    private String articleTypeIds;

}