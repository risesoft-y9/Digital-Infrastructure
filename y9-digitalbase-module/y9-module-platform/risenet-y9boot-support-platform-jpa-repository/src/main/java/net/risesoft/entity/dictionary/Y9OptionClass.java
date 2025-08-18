package net.risesoft.entity.dictionary;

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
@DynamicUpdate
@Comment("字典类型表")
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