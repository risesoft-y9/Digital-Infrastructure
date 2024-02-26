package net.risesoft.model.dataservice;

import java.io.Serializable;

import lombok.Data;

/**
 * 团队管理表
 *
 */
@Data
public class TeamInfoModel implements Serializable {

    private static final long serialVersionUID = -8086386577853572160L;

    /**
     * 主键id
     */
    private String id;

    /**
     * 人员名称/部门名称
     */
    private String personName;

    /**
     * 业务领域
     */
    private String business;

    /**
     * 核心能力
     */
    private String ability;

    /**
     * 人力资源人员id/经营人
     */
    private String personId;

    /**
     * 组织架构部门id
     */
    private String deptId;

    /**
     * 类型id，0-团队信息，1-团队人员信息
     */
    private String parentId;

    /**
     * 创建时间
     */
    private String createDate;

}
