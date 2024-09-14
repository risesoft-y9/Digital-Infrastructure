package net.risesoft.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;

/**
 * 部门信息配置表
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_DEPARTMENT_PROP")
@DynamicUpdate
@Comment("部门信息配置表")
@NoArgsConstructor
@Data
public class Y9DepartmentProp extends BaseEntity {

    private static final long serialVersionUID = 1239795207430901518L;

    /** 唯一标示 */
    @Id
    @Column(name = "ID", length = 38, nullable = false, unique = true)
    @Comment("唯一标示")
    private String id;

    /** 部门唯一标示 */
    @Column(name = "DEPT_ID", length = 50, nullable = false)
    @Comment("部门唯一标示")
    private String deptId;

    /** 组织唯一标示 */
    @Column(name = "ORG_BASE_ID", length = 50, nullable = false)
    @Comment("组织唯一标示")
    private String orgBaseId;

    /** 类别 */
    @Column(name = "CATEGORY", nullable = false)
    @Comment("类别")
    private Integer category;

    /** 排序号 */
    @Column(name = "TAB_INDEX", nullable = false)
    @Comment("排序号")
    private Integer tabIndex = 0;

}