package net.risesoft.enums.platform.org;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

import net.risesoft.enums.ValuedEnum;

/**
 * 人员类型
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Getter
@AllArgsConstructor
public enum PersonTypeEnum implements ValuedEnum<String> {
    /** 管理员用户 */
    ADMIN("adminPerson", "管理员用户"),
    /** 单位用户 */
    DEPARTMENT("deptPerson", "单位用户"),
    /** 个人用户 */
    USER("userPerson", "个人用户"),
    /** 专家用户 */
    EXPERT("expertPerson", "专家用户");

    public static final Map<String, String> PERSON_TYPE_MAP = new HashMap<String, String>(16);

    static {
        PERSON_TYPE_MAP.put(ADMIN.getValue(), ADMIN.getName());
        PERSON_TYPE_MAP.put(DEPARTMENT.getValue(), DEPARTMENT.getName());
        PERSON_TYPE_MAP.put(EXPERT.getValue(), EXPERT.getName());
        PERSON_TYPE_MAP.put(USER.getValue(), USER.getName());
    }

    private final String value;
    private final String name;

}