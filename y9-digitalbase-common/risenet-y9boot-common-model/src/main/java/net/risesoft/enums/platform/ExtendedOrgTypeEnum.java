package net.risesoft.enums.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 扩展组织类型枚举
 *
 * @author shidaobang
 * @date 2024/03/21
 */
@Getter
@AllArgsConstructor
public enum ExtendedOrgTypeEnum {
    /** 自定义用户组成员 */
    CUSTOM_GROUP_MEMBER("CustomGroupMember", "自定义用户组成员"),
    /** 通讯录组 */
    CONTACT_GROUP("ContactGroup", "通讯录组"),
    /** 通讯录组成员 */
    CONTACT("Contact", "通讯录组成员"),
    /** 自定义用户组 */
    CUSTOM_GROUP("CustomGroup", "自定义用户组");

    private final String enName;
    private final String name;

}
