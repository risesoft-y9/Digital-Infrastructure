package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 拒绝类型
 * 
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum ItemPermissionEnum {
    /** 退回 */
    ROLE(1, "角色"),
    /** 收回 */
    DEPARTMENT(2, "部门"),
    /**  */
    USER(3, "人员"),
    /**  */
    DYNAMICROLE(4, "动态角色"),
    /**  */
    GROUP(5, "用户组"),
    /**  */
    POSITION(6, "岗位"),
    /**  */
    CUSTOMGROUP(7, "自定义用户组"),
    /**  */
    ORGANIZATION(8, "组织机构"),
    /**  */
    ORGANIZATION_VIRTUAL(9, "虚拟组织机构"),
    /**  */
    DEPARTMENT_VIRTUAL(10, "虚拟组织机构下的部门");

    private final Integer value;
    private final String name;
}
