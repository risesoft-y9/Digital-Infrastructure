package net.risesoft.enums.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

import net.risesoft.enums.ValuedEnum;

/**
 * 权限类型
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Getter
@AllArgsConstructor
public enum AuthorityEnum implements ValuedEnum<Integer> {
    /** 隐藏 */
    HIDDEN("隐藏", 0),
    /** 浏览 */
    BROWSE("浏览", 1),
    /** 维护 */
    MODIFY("维护", 2),
    /** 管理 */
    ADMIN("管理", 3);

    private final String name;
    private final Integer value;

}
