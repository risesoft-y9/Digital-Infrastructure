package net.risesoft.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;
import net.risesoft.consts.DefaultConsts;

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
@DynamicUpdate
@Comment("职位表")
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
    @Column(name = "NAME", length = 255, nullable = false, unique = true)
    @Comment("名称")
    private String name;

    /** 排序号 */
    @Column(name = "TAB_INDEX", nullable = false)
    @Comment("排序号")
    private Integer tabIndex = DefaultConsts.TAB_INDEX;

}