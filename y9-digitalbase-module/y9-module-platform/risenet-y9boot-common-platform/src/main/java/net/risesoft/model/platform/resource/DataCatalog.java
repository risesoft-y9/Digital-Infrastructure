package net.risesoft.model.platform.resource;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.enums.platform.resource.DataCatalogTypeEnum;

/**
 * 数据目录
 *
 * @author shidaobang
 * @date 2024/10/09
 * @since 9.6.8
 */
@Data
public class DataCatalog extends Resource {

    private static final long serialVersionUID = 6586860422866691019L;

    /**
     * 节点类型
     */
    public String nodeType;

    /**
     * 所属数据目录树类型
     */
    @NotBlank
    public String treeType;

    /**
     * 组织节点ID，只有当 type 为 ORG_UNIT 时会有值
     */
    private String orgUnitId;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 目录类型
     */
    public DataCatalogTypeEnum type;

    // @Override
    public String getAppId() {
        return InitDataConsts.APP_ID;
    }
}
