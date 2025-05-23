package net.risesoft.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;
import net.risesoft.consts.DefaultConsts;

/**
 * 自定义群组
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_CUSTOM_GROUP")
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "自定义群组", appliesTo = "Y9_ORG_CUSTOM_GROUP")
@NoArgsConstructor
@Data
public class Y9CustomGroup extends BaseEntity {

    private static final long serialVersionUID = -1149700156942236281L;

    /** 主键 */
    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    private String id;

    /** 自定义id */
    @Comment("自定义id")
    @Column(name = "CUSTOM_ID")
    private String customId;

    /** 群组名称 */
    @Comment("群组名称")
    @Column(name = "GROUP_NAME", length = 50, nullable = false)
    private String groupName;

    /** 用户id */
    @Comment("用户id")
    @Column(name = "USER_ID", length = 38)
    private String personId;

    /** 租户id */
    @Comment("租户id")
    @Column(name = "TENANT_ID", length = 38)
    private String tenantId;

    /** 分享人Id */
    @Comment("分享人Id")
    @Column(name = "SHARE_ID", length = 38)
    private String shareId;

    /** 排序字段 */
    @Comment("排序字段")
    @Column(name = "TAB_INDEX", nullable = false)
    private Integer tabIndex = DefaultConsts.TAB_INDEX;

}
