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
 * 字典类型表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_OPTIONCLASS")
@org.hibernate.annotations.Table(comment = "字典类型表", appliesTo = "Y9_ORG_OPTIONCLASS")
@NoArgsConstructor
@Data
public class Y9OptionClass extends BaseEntity {
    
    private static final long serialVersionUID = -5901383621072805572L;

    /** 主键，类型名称 */
    @Id
    @Column(name = "TYPE", length = 255, nullable = false)
    @Comment("主键，类型名称")
    @NotBlank
    private String type;

    /** 中文名称 */
    @Column(name = "NAME", length = 255, nullable = false)
    @Comment("中文名称")
    @NotBlank
    private String name;

}