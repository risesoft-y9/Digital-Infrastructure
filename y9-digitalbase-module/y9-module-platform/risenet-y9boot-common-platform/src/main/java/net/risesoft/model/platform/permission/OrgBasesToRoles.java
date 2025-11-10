package net.risesoft.model.platform.permission;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.model.BaseModel;

/**
 * 组织关联角色
 *
 * @author shidaobang
 * @date 2022/09/14
 */
@Data
public class OrgBasesToRoles extends BaseModel {
    private static final long serialVersionUID = -6536266072250897376L;

    private String id;

    /** 角色id */
    @NotBlank
    private String roleId;

    /** 人员或部门组织机构等唯一标识 */
    @NotBlank
    private String orgId;

    /**
     * 组织类型
     *
     * {@link OrgTypeEnum}
     */
    protected OrgTypeEnum orgType;

    /** 父节点唯一标识 */
    private String parentId;

    /** 关联排序号 */
    private Integer orgOrder;

    /** 是否为负角色关联 */
    private Boolean negative;
}
