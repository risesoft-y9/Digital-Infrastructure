package net.risesoft.y9public.entity.resource;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.consts.DefaultConsts;
import net.risesoft.consts.InitDataConsts;
import net.risesoft.enums.platform.resource.DataCatalogTypeEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.resource.DataCatalog;
import net.risesoft.persistence.EnumConverter;
import net.risesoft.y9.util.Y9BeanUtil;

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
    private DataCatalogTypeEnum type = DataCatalogTypeEnum.CLASSIFICATION;

    public Y9DataCatalog(DataCatalog dataCatalog, Y9DataCatalog parent, Integer nextTabIndex, String currentTenantId) {
        Y9BeanUtil.copyProperties(dataCatalog, this);

        if (StringUtils.isBlank(this.id)) {
            this.id = Y9IdGenerator.genId();
        }
        this.tenantId = currentTenantId;
        this.systemId = InitDataConsts.SYSTEM_ID;
        this.inherit = Boolean.TRUE;
        if (this.tabIndex == null || DefaultConsts.TAB_INDEX.equals(this.tabIndex)) {
            this.tabIndex = nextTabIndex;
        }
        rebuildProperties(parent);
    }

    @Override
    public String getAppId() {
        return InitDataConsts.APP_ID;
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }

    public void update(DataCatalog dataCatalog, Y9DataCatalog parent) {
        Y9BeanUtil.copyProperties(dataCatalog, this);
        rebuildProperties(parent);
    }

    private void rebuildProperties(Y9DataCatalog parent) {
        if (parent != null) {
            this.parentId = parent.getId();
            rebuildGuidPath(parent);
            return;
        }

        this.parentId = null;
        rebuildGuidPath(null);
    }

}
