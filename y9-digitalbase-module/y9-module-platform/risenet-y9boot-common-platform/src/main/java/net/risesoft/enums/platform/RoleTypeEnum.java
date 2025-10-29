package net.risesoft.enums.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

import net.risesoft.enums.ValuedEnum;

/**
 * 角色类型
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@AllArgsConstructor
@Getter
public enum RoleTypeEnum implements ValuedEnum<String> {
    /** 角色 */
    ROLE("role"),
    /** 文件夹 */
    FOLDER("folder");

    private final String value;

}
