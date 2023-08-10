package net.risesoft.entity.relation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

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
@Comment("人员用户组关联表")
@NoArgsConstructor
@Data
public class Y9PersonsToGroups extends BaseEntity {

    private static final long serialVersionUID = 2899191697180789119L;

    /** 主键 */
    @Id
    @GenericGenerator(name = "PersonsToGroups", strategy = "native")
    @GeneratedValue(generator = "PersonsToGroups")
    @Column(name = "ID")
    @Comment("主键")
    private Integer id;

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