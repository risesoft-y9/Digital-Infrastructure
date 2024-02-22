package net.risesoft.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import lombok.Data;

import net.risesoft.enums.platform.OrgTypeEnum;

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
@Comment("岗位表")
@Data
public class Y9Position extends Y9OrgBase {

    private static final long serialVersionUID = -5753173583033676828L;

    public Y9Position() {
        super.setOrgType(OrgTypeEnum.POSITION);
    }

    /** 父节点id */
    @Column(name = "PARENT_ID", length = 38, nullable = false)
    @Comment("父节点id")
    private String parentId;

    /** 职位id */
    @Column(name = "JOB_ID", length = 38, nullable = false)
    @Comment("职位id")
    private String jobId;

    /** 职位名称 */
    @Column(name = "JOB_NAME", length = 255, nullable = false)
    @Comment("职位名称")
    private String JobName;

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