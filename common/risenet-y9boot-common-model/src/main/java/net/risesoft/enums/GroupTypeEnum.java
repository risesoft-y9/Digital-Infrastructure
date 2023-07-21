package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 组类型
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Getter
@AllArgsConstructor
public enum GroupTypeEnum {
    /** 岗位组 */
    POSITION("position"),
    /** 人员组 */
    PERSON("person");
    
    private final String name;
}
