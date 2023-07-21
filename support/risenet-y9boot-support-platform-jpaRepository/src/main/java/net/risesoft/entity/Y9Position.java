package net.risesoft.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import lombok.Data;

import net.risesoft.enums.OrgTypeEnum;

/**
 * 岗位实体
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_POSITION")
@org.hibernate.annotations.Table(comment = "岗位表", appliesTo = "Y9_ORG_POSITION")
@Data
public class Y9Position extends Y9OrgBase {
    
    private static final long serialVersionUID = -5753173583033676828L;

    public Y9Position() {
        super.setOrgType(OrgTypeEnum.POSITION.getEnName());
    }

    /** 职位id */
    @Column(name = "JOB_ID", length = 38, nullable = false)
    @Comment("职位id")
    private String jobId;

    /** 职务 */
    @Column(name = "DUTY", length = 255)
    @Comment("职务")
    private String duty;

    /** 职务级别 */
    @ColumnDefault("0")
    @Column(name = "DUTY_LEVEL", nullable = false)
    @Comment("职务级别")
    private Integer dutyLevel = 0;

    /** 职级名称 */
    @Column(name = "DUTY_LEVEL_NAME", length = 255)
    @Comment("职级名称")
    private String dutyLevelName;

    /** 职务类型 */
    @Column(name = "DUTY_TYPE", length = 255)
    @Comment("职务类型")
    private String dutyType;

    /** 排序序列号 */
    @Column(name = "ORDERED_PATH", length = 500)
    @Comment("排序序列号")
    private String orderedPath;

    /** 互斥的岗位Id列表，之间用逗号分割 */
    @Column(name = "EXCLUSIVE_IDS", length = 500)
    @Comment("互斥的岗位Id列表，之间用逗号分割")
    private String exclusiveIds;

    /** 岗位容量，默认容量为1，即一人一岗 */
    @ColumnDefault("1")
    @Column(name = "CAPACITY", nullable = false)
    @Comment("岗位容量，默认容量为1，即一人一岗")
    private Integer capacity = 1;
    
    /** 当前岗位人数，小于或等于岗位容量 */
    @ColumnDefault("0")
    @Column(name = "HEAD_COUNT", nullable = false)
    @Comment("岗位当前人数，小于或等于岗位容量")
    private Integer headCount = 0;
}