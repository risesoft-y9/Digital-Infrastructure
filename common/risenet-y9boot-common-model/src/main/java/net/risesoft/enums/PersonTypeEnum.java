package net.risesoft.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
public enum PersonTypeEnum {
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
        PERSON_TYPE_MAP.put(ADMIN.getEnName(), ADMIN.getName());
        PERSON_TYPE_MAP.put(DEPARTMENT.getEnName(), DEPARTMENT.getName());
        PERSON_TYPE_MAP.put(EXPERT.getEnName(), EXPERT.getName());
        PERSON_TYPE_MAP.put(USER.getEnName(), USER.getName());
    }

    private final String name;
    private final String enName;
}