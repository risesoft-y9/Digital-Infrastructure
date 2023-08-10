package net.risesoft.entity.relation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

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
@Comment("人员岗位关联表")
@NoArgsConstructor
@Data
public class Y9PersonsToPositions extends BaseEntity {

    private static final long serialVersionUID = 8926134052486805976L;

    /** 主键 */
    @Id
    @GenericGenerator(name = "PersonsToPositions", strategy = "native")
    @GeneratedValue(generator = "PersonsToPositions")
    @Column(name = "ID")
    @Comment("主键")
    private Integer id;

    /** 岗位ID */
    @Column(name = "POSITION_ID", length = 38, nullable = false)
    @Comment("岗位ID")
    private String positionId;

    /** 人员ID */
    @Column(name = "PERSON_ID", length = 38, nullable = false)
    @Comment("人员ID")
    private String personId;

    /** 岗位排序号 */
    @ColumnDefault("0")
    @Column(name = "POSITION_ORDER", nullable = false)
    @Comment("岗位排序号")
    private Integer positionOrder = 0;

    /** 人员排序号 */
    @ColumnDefault("0")
    @Column(name = "PERSON_ORDER", nullable = false)
    @Comment("人员排序号")
    private Integer personOrder = 0;

}