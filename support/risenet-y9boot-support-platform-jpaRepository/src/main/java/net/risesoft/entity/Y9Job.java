package net.risesoft.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;

/**
 * 职位表
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/9/19
 */
@Entity
@Table(name = "Y9_ORG_JOB")
@org.hibernate.annotations.Table(comment = "职位表", appliesTo = "Y9_ORG_JOB")
@NoArgsConstructor
@Data
public class Y9Job extends BaseEntity {

    private static final long serialVersionUID = 3545620512323461654L;
    
    /** 主键id */
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("主键id")
    private String id;

    /** 数据代码 */
    @NotBlank
    @Column(name = "CODE", length = 255, nullable = false)
    @Comment("数据代码")
    private String code;

    /** 名称 */
    @NotBlank
    @Column(name = "NAME", length = 255, nullable = false)
    @Comment("名称")
    private String name;

    /** 排序号 */
    @Column(name = "TAB_INDEX", nullable = false)
    @Comment("排序号")
    private Integer tabIndex = 0;

}