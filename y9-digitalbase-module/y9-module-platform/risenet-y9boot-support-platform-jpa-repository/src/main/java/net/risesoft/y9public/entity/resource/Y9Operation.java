package net.risesoft.y9public.entity.resource;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.consts.DefaultConsts;
import net.risesoft.enums.platform.resource.OperationDisplayTypeEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.resource.Operation;
import net.risesoft.persistence.EnumConverter;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 页面按钮操作表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_COMMON_OPERATION",
    uniqueConstraints = {@UniqueConstraint(name = Y9Operation.UK_CUSTOM_ID, columnNames = {"CUSTOM_ID"}),
        @UniqueConstraint(name = Y9Operation.UK_GUID_PATH, columnNames = {"GUID_PATH"})})
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "页面按钮操作表", appliesTo = "Y9_COMMON_OPERATION")
@Data
@NoArgsConstructor
public class Y9Operation extends Y9ResourceBase {

    private static final long serialVersionUID = -138076174688809730L;

    public static final String UK_CUSTOM_ID = "UK_OPERATION_CUSTOM_ID";

    public static final String UK_GUID_PATH = "UK_OPERATION_GUID_PATH";

    {
        super.setResourceType(ResourceTypeEnum.OPERATION);
    }

    /** 应用id */
    @Column(name = "APP_ID", length = 38, nullable = false)
    @Comment("应用id")
    private String appId;

    /** 父节点ID */
    @Comment("父节点ID")
    @Column(name = "PARENT_ID", length = 38, nullable = false)
    private String parentId;

    /** 按钮展示方式 */
    @ColumnDefault("0")
    @Column(name = "DISPLAY_TYPE", nullable = false)
    @Comment("按钮展示方式")
    @Convert(converter = EnumConverter.OperationDisplayTypeEnumConverter.class)
    private OperationDisplayTypeEnum displayType = OperationDisplayTypeEnum.ICON_TEXT;

    /** 按钮事件 */
    @Column(name = "EVENT_NAME", length = 50)
    @Comment("按钮事件")
    private String eventName;

    @Override
    public String getAppId() {
        return this.appId;
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }

    @Override
    public String getCustomId() {
        if (StringUtils.equals(this.customId, this.id)) {
            // 对上层隐藏“非必填”的 customId 字段唯一索引的实现细节
            return null;
        }
        return this.customId;
    }

    public Y9Operation(Operation operation, Y9ResourceBase parentResource, Integer nextTabIndex) {
        Y9BeanUtil.copyProperties(operation, this);

        if (StringUtils.isBlank(this.id)) {
            this.id = Y9IdGenerator.genId(IdType.SNOWFLAKE);
        }
        if (StringUtils.isBlank(this.customId)) {
            // customId 字段有唯一约束，不同数据库对唯一约束的允许不一致，此处保证 customId 列始终有值，所有数据库通用
            this.customId = this.id;
        }
        if (this.tabIndex == null || DefaultConsts.TAB_INDEX.equals(this.tabIndex)) {
            this.tabIndex = nextTabIndex;
        }
        rebuildGuidPath(parentResource);
    }

    public void update(Operation operation, Y9ResourceBase parentResource) {
        Y9BeanUtil.copyProperties(operation, this);
        if (StringUtils.isBlank(this.customId)) {
            // customId 字段有唯一约束，不同数据库对唯一约束的允许不一致，此处保证 customId 列始终有值，所有数据库通用
            this.customId = this.id;
        }
        rebuildGuidPath(parentResource);
    }
}
