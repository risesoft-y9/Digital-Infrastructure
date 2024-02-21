package net.risesoft.enums.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 身份类型
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Getter
@AllArgsConstructor
public enum IdentityEnum {

    /** 人员 */
    PERSON("Person", 1),
    /** 岗位 */
    POSITION("Position", 2);

    private final String name;
    private final Integer value;
}