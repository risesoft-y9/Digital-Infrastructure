package net.risesoft.y9public.entity.resource;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@org.hibernate.annotations.Table(comment = "数据目录", appliesTo = "Y9_COMMON_DATA_CATALOG")
@Data
@SuperBuilder
@NoArgsConstructor
public class Y9DataCatalog extends Y9ResourceBase {

    private static final long serialVersionUID = -5070637557179341250L;

    {
        super.setResourceType(ResourceTypeEnum.DATA_CATALOG);
    }

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
    @Builder.Default
    private DataCatalogTypeEnum type = DataCatalogTypeEnum.CLASSIFICATION;

    @Override
    public String getAppId() {
        return InitDataConsts.APP_ID;
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }

}
