package net.risesoft.entity.permission;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;
import net.risesoft.consts.DefaultConsts;

/**
 * 数据列权限表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_PERMISSION_DATAFIELD")
@DynamicUpdate
@Comment("数据列权限表")
@NoArgsConstructor
@Data
public class Y9DataFieldPermission extends BaseEntity {

    private static final long serialVersionUID = 8456154957926701479L;

    /** 主键 */
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("主键")
    private String id;

    /** 应用id */
    @Column(name = "APP_ID", length = 38)
    @Comment("应用id")
    private String appId;

    /** 名称 */
    @Column(name = "NAME", length = 255)
    @Comment("名称")
    private String name;

    /** 描述 */
    @Column(name = "DESCRIPTION", length = 255)
    @Comment("描述")
    private String description;

    /** 只读字段列表（之间用逗号隔开） */
    @Column(name = "READONLY_FIELDS", length = 1000)
    @Comment("只读字段列表（之间用逗号隔开）")
    private String readOnlyFields;

    /** 不可见字段列表（之间用逗号隔开） */
    @Column(name = "HIDDEN_FIELDS", length = 1000)
    @Comment("不可见字段列表（之间用逗号隔开）")
    private String hiddenFields;

    /** 排序号 */
    @Column(name = "TAB_INDEX", nullable = false)
    @Comment("排序号")
    private Integer tabIndex = DefaultConsts.TAB_INDEX;

}
