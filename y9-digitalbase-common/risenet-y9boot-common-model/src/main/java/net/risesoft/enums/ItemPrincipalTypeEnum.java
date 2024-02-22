package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事项表单类型
 * 
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum ItemPrincipalTypeEnum {
    /** 部门 */
    DEPT(2, "部门"),
    /** 人员 */
    PERSON(3, "人员"),
    /** 岗位 */
    POSITION(6, "岗位"),
    /** 自定义用户组 */
    CUSTOMGROUP(7, "自定义用户组");

    private final Integer value;
    private final String name;
}
