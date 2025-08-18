package net.risesoft.enums.platform.org;

import lombok.AllArgsConstructor;
import lombok.Getter;

import net.risesoft.enums.ValuedEnum;

/**
 * 部门属性类型
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Getter
@AllArgsConstructor
public enum DepartmentPropCategoryEnum implements ValuedEnum<Integer> {
    /** 管理员 */
    ADMIN(1, "管理员"),
    /** 主管领导 */
    MANAGER(2, "主管领导"),
    /** 部门领导 */
    LEADER(3, "部门领导"),
    /** 副部门领导 */
    VICE_LEADER(4, "副部门领导"),
    /** 部门收发员 */
    DISPATCHER(5, "部门收发员"),
    /** 秘书 */
    SECRETARY(6, "秘书");

    private final Integer category;
    private final String name;

    @Override
    public Integer getValue() {
        return this.category;
    }
}
