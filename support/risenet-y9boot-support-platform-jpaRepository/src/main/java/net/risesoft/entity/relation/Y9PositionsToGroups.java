package net.risesoft.entity.relation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;

/**
 * 岗位与岗位组关联表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_POSITIONS_GROUPS", indexes = {@Index(columnList = "GROUP_ID,POSITION_ID", unique = true), @Index(columnList = "POSITION_ID,GROUP_ID", unique = true)})
@org.hibernate.annotations.Table(comment = "岗位与岗位组关联表", appliesTo = "Y9_ORG_POSITIONS_GROUPS")
@NoArgsConstructor
@Data
public class Y9PositionsToGroups extends BaseEntity {
    
    private static final long serialVersionUID = 2899191697180789119L;

    /** 主键 */
    @Id
    @GenericGenerator(name = "PositionsToGroups", strategy = "native")
    @GeneratedValue(generator = "PositionsToGroups")
    @Column(name = "ID")
    @Comment("主键")
    private Integer id;

    /** 用户组id */
    @Column(name = "GROUP_ID", length = 38, nullable = false)
    @Comment("用户组id")
    private String groupId;

    /** 岗位ID */
    @Column(name = "POSITION_ID", length = 38, nullable = false)
    @Comment("岗位ID")
    private String positionId;

    /** 用户组排序号 */
    @Column(name = "GROUP_ORDER", nullable = false)
    @Comment("用户组排序号")
    private Integer groupOrder = 0;

    /** 人员排序号 */
    @Column(name = "POSITION_ORDER", nullable = false)
    @Comment("人员排序号")
    private Integer positionOrder = 0;

}