package net.risesoft.y9public.entity.resource;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import net.risesoft.enums.platform.resource.OperationDisplayTypeEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.persistence.EnumConverter;

/**
 * 页面按钮操作表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_COMMON_OPERATION")
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "页面按钮操作表", appliesTo = "Y9_COMMON_OPERATION")
@Data
@SuperBuilder
public class Y9Operation extends Y9ResourceBase {

    private static final long serialVersionUID = -138076174688809730L;

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

    public Y9Operation() {
        super.setResourceType(ResourceTypeEnum.OPERATION);
    }

    @Override
    public String getAppId() {
        return this.appId;
    }
}
