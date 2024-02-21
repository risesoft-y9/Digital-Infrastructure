package net.risesoft.enums.platform;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.risesoft.enums.ValuedEnum;

/**
 * 组织节点类型
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Getter
@AllArgsConstructor
public enum OrgTypeEnum implements ValuedEnum<String> {
    /** 组织机构 */
    ORGANIZATION("Organization", "组织机构"),
    /** 部门 */
    DEPARTMENT("Department", "部门"),
    /** 用户组 */
    GROUP("Group", "用户组"),
    /** 岗位 */
    POSITION("Position", "岗位"),
    /** 人员 */
    PERSON("Person", "人员"),
    /** 三员管理员 */
    MANAGER("Manager", "三员管理员"),

    /** 自定义用户组 */
    CUSTOM_GROUP("CustomGroup", "自定义用户组"),
    /** 自定义用户组成员 */
    CUSTOM_GROUP_MEMBER("CustomGroupMember", "自定义用户组成员"),
    /** 通讯录组 */
    CONTACT_GROUP("ContactGroup", "通讯录组"),
    /** 通讯录组成员 */
    CONTACT("Contact", "通讯录组成员");

    /** RC8当前版本(用于数据库存版本号) */
    public static final Integer Y9_VERSION = 0;

    public static final Map<String, String> ORG_TYPE_MAP = new HashMap<>(16);
    static {
        ORG_TYPE_MAP.put(ORGANIZATION.getEnName(), ORGANIZATION.getName());
        ORG_TYPE_MAP.put(DEPARTMENT.getEnName(), DEPARTMENT.getName());
        ORG_TYPE_MAP.put(GROUP.getEnName(), GROUP.getName());
        ORG_TYPE_MAP.put(POSITION.getEnName(), POSITION.getName());
        ORG_TYPE_MAP.put(PERSON.getEnName(), PERSON.getName());
        ORG_TYPE_MAP.put(MANAGER.getEnName(), MANAGER.getName());
    }

    public static OrgTypeEnum getByEnName(String enName) {
        for (OrgTypeEnum orgTypeEnum : OrgTypeEnum.values()) {
            if (orgTypeEnum.getEnName().equals(enName)) {
                return orgTypeEnum;
            }
        }
        throw new IllegalArgumentException("enName is invalid");
    }

    private final String enName;
    private final String name;

    @Override
    public String getValue() {
        return this.enName;
    }
}