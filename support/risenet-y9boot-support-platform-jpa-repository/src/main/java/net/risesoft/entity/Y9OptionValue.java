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
 * 字典数据表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_OPTIONVALUE")
@org.hibernate.annotations.Table(comment = "字典数据表", appliesTo = "Y9_ORG_OPTIONVALUE")
@NoArgsConstructor
@Data
public class Y9OptionValue extends BaseEntity {

    private static final long serialVersionUID = 497989593405504925L;

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

    /** 主键名称 */
    @NotBlank
    @Column(name = "NAME", length = 255, nullable = false)
    @Comment("主键名称")
    private String name;

    /** 排序号 */
    @Column(name = "TAB_INDEX", nullable = false)
    @Comment("排序号")
    private Integer tabIndex = 0;

    /** 字典类型 */
    @NotBlank
    @Column(name = "TYPE", length = 255, nullable = false)
    @Comment("字典类型")
    private String type;

}