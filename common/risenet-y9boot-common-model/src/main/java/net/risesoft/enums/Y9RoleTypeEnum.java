package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色类型
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Getter
@AllArgsConstructor
public enum Y9RoleTypeEnum {
    /** 角色 */
    ROLE("role"),
    /** 文件夹 */
    FOLDER("folder");

    private final String value;
}
