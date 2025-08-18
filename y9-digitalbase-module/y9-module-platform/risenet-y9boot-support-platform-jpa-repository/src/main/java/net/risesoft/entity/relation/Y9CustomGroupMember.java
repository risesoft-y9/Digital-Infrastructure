package net.risesoft.entity.relation;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;
import net.risesoft.consts.DefaultConsts;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.enums.platform.org.SexEnum;
import net.risesoft.persistence.EnumConverter;

/**
 * 自定义群组成员表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_CUSTOM_GROUP_MEMBER")
@DynamicUpdate
@Comment("自定义群组成员表")
@NoArgsConstructor
@Data
public class Y9CustomGroupMember extends BaseEntity {

    private static final long serialVersionUID = 4266888031261592065L;

    /** 主键 */
    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    private String id;

    /** 成员名称 */
    @Comment("成员名称")
    @Column(name = "MEMBER_NAME", length = 255, nullable = false)
    private String memberName;

    /** 成员id */
    @Comment("成员id")
    @Column(name = "MEMBER_ID", length = 38, nullable = false)
    private String memberId;

    /**
     * 性别
     * 
     * {@link SexEnum}
     */
    @Comment("性别")
    @Column(name = "SEX")
    private Integer sex;

    /** 所在群组id */
    @Comment("所在群组id")
    @Column(name = "GROUP_ID", length = 38)
    private String groupId;

    /** 所在组织架构父节点id */
    @Column(name = "PARENT_ID", length = 38)
    @Comment("所在组织架构父节点id")
    private String parentId;

    /** 成员类型 */
    @Column(name = "MEMBER_TYPE", length = 255, nullable = false)
    @Comment("成员类型")
    @Convert(converter = EnumConverter.OrgTypeEnumConverter.class)
    private OrgTypeEnum memberType;

    /** 排序 */
    @Comment("排序")
    @Column(name = "TAB_INDEX", nullable = false)
    private Integer tabIndex = DefaultConsts.TAB_INDEX;

}
