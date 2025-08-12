package net.risesoft.entity.relation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;

/**
 * 人员岗位关联表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_PERSONS_POSITIONS",
    indexes = {@Index(columnList = "POSITION_ID,PERSON_ID", unique = true),
        @Index(columnList = "PERSON_ID,POSITION_ID", unique = true)})
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "人员岗位关联表", appliesTo = "Y9_ORG_PERSONS_POSITIONS")
@NoArgsConstructor
@Data
public class Y9PersonsToPositions extends BaseEntity {

    private static final long serialVersionUID = 8926134052486805976L;

    /** 主键 */
    @Id
    @Column(name = "ID")
    @Comment("主键")
    private String id;

    /** 岗位ID */
    @Column(name = "POSITION_ID", length = 38, nullable = false)
    @Comment("岗位ID")
    private String positionId;

    /** 人员ID */
    @Column(name = "PERSON_ID", length = 38, nullable = false)
    @Comment("人员ID")
    private String personId;

    /** 同一个人员的岗位排序号 */
    @ColumnDefault("0")
    @Column(name = "POSITION_ORDER", nullable = false)
    @Comment("同一个人员的岗位排序号")
    private Integer positionOrder = 0;

    /** 同一个岗位的人员排序号 */
    @ColumnDefault("0")
    @Column(name = "PERSON_ORDER", nullable = false)
    @Comment("同一个岗位的人员排序号")
    private Integer personOrder = 0;

}