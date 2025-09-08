package net.risesoft.y9public.entity.resource;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.enums.platform.resource.DataCatalogTypeEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.persistence.EnumConverter;

/**
 * 数据目录
 *
 * @author shidaobang
 * @date 2024/10/09
 * @since 9.6.8
 */
@Entity
@Table(name = "Y9_COMMON_DATA_CATALOG")
@DynamicUpdate
@Comment("数据目录")
@Data
@SuperBuilder
public class Y9DataCatalog extends Y9ResourceBase {

    private static final long serialVersionUID = -5070637557179341250L;

    /**
     * 父节点ID
     */
    @Comment("父节点ID")
    @Column(name = "PARENT_ID", length = 38, nullable = false)
    private String parentId;

    /**
     * 组织节点ID，只有当 type 为 ORG_UNIT 时会有值
     */
    @Comment("组织节点ID，只有当 type 为 ORG_UNIT 时会有值")
    @Column(name = "ORG_UNIT_ID", length = 38)
    private String orgUnitId;

    /**
     * 所属数据目录树类型
     */
    @Comment("所属数据目录树类型")
    @Column(name = "TREE_TYPE", length = 50, nullable = false)
    private String treeType;

    /**
     * 租户ID
     */
    @Comment("租户ID")
    @Column(name = "TENANT_ID", length = 38, nullable = false)
    private String tenantId;

    /**
     * 数据目录类型
     */
    @Column(name = "DATA_CATALOG_TYPE", nullable = false)
    @Comment("数据目录类型")
    @Convert(converter = EnumConverter.DataCatalogTypeEnumConverter.class)
    private DataCatalogTypeEnum type = DataCatalogTypeEnum.CLASSIFICATION;

    public Y9DataCatalog() {
        super.setResourceType(ResourceTypeEnum.DATA_CATALOG);
    }

    @Override
    public String getAppId() {
        return InitDataConsts.APP_ID;
    }
}
