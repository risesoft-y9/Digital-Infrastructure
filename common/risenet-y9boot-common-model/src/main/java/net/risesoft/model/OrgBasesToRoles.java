package net.risesoft.model;

import java.io.Serializable;

import lombok.Data;

import net.risesoft.enums.OrgTypeEnum;

/**
 * 组织关联角色
 *
 * @author shidaobang
 * @date 2022/09/14
 */
@Data
public class OrgBasesToRoles implements Serializable {
    private static final long serialVersionUID = -6536266072250897376L;

    private Integer id;

    /** 角色id */
    private String roleId;

    /** 人员或部门组织机构等唯一标识 */
    private String orgId;

    /**
     * 组织类型
     *
     * {@link OrgTypeEnum}
     */
    protected String orgType;

    /** 关联排序号 */
    private Integer orgOrder;

    /** 父节点唯一标识 */
    private String parentId;

    /** 是否为负角色关联 */
    private Boolean negative = Boolean.FALSE;
}
