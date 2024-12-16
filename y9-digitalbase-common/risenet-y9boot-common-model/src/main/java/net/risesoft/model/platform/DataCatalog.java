package net.risesoft.model.platform;

import java.io.Serializable;

import lombok.Data;

import net.risesoft.enums.platform.DataCatalogTypeEnum;

/**
 * 数据目录
 *
 * @author shidaobang
 * @date 2024/10/09
 * @since 9.6.8
 */
@Data
public class DataCatalog implements Serializable {

    private static final long serialVersionUID = 6586860422866691019L;

    /**
     * ID
     */
    public String id;

    /**
     * 名称
     */
    public String name;

    /**
     * 父节点 id
     */
    public String parentId;

    /**
     * 排列序号
     */
    public int tabIndex;

    /**
     * 节点类型
     */
    public String nodeType;

    /**
     * 所属数据目录树类型
     */
    public String treeType;

    /**
     * 目录类型
     */
    public DataCatalogTypeEnum type;

    /**
     * 是否启用
     */
    public Boolean enabled;

    /**
     * 自定义 ID
     */
    public String customId;

    /**
     * 是否继承父权限
     */
    private Boolean inherit;
}
