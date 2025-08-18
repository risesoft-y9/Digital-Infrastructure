package net.risesoft.enums.platform.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;

import net.risesoft.enums.ValuedEnum;

/**
 * 授权主体类型
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Getter
@AllArgsConstructor
public enum AuthorizationPrincipalTypeEnum implements ValuedEnum<Integer> {
    /** 角色 */
    ROLE("角色", 0),
    /** 岗位 */
    POSITION("岗位", 1),
    /** 人员 */
    PERSON("人员", 2),
    /** 用户组 */
    GROUP("用户组", 3),
    /** 部门 */
    DEPARTMENT("部门", 4),
    /** 组织机构 */
    ORGANIZATION("组织机构", 5);

    private final String name;
    private final Integer value;

    public static AuthorizationPrincipalTypeEnum getByName(String name) {
        for (AuthorizationPrincipalTypeEnum orgTypeEnum : AuthorizationPrincipalTypeEnum.values()) {
            if (orgTypeEnum.getName().equals(name)) {
                return orgTypeEnum;
            }
        }
        throw new IllegalArgumentException("enName is invalid");
    }
}
