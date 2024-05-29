package net.risesoft.entity.relation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;

/**
 * 人员用户组关联表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_PERSONS_GROUPS",
    indexes = {@Index(columnList = "GROUP_ID,PERSON_ID", unique = true),
        @Index(columnList = "PERSON_ID,GROUP_ID", unique = true)})
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "人员用户组关联表", appliesTo = "Y9_ORG_PERSONS_GROUPS")
@NoArgsConstructor
@Data
public class Y9PersonsToGroups extends BaseEntity {

    private static final long serialVersionUID = 2899191697180789119L;

    /** 主键 */
    @Id
    @Column(name = "ID")
    @Comment("主键")
    private String id;

    /** 用户组id */
    @Column(name = "GROUP_ID", length = 38, nullable = false)
    @Comment("用户组id")
    private String groupId;

    /** 人员ID */
    @Column(name = "PERSON_ID", length = 38, nullable = false)
    @Comment("人员ID")
    private String personId;

    /** 用户组排序号 */
    @Column(name = "GROUP_ORDER", nullable = false)
    @Comment("用户组排序号")
    private Integer groupOrder = 0;

    /** 人员排序号 */
    @Column(name = "PERSON_ORDER", nullable = false)
    @Comment("人员排序号")
    private Integer personOrder = 0;

}