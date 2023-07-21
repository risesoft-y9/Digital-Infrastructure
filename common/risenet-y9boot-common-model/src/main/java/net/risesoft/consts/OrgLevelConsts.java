package net.risesoft.consts;

import net.risesoft.enums.OrgTypeEnum;

/**
 * 组织层次常量
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
public class OrgLevelConsts {

    /** 机构层次 组织机构 */
    public static final String ORGANIZATION = "o=";

    /** 部门，单位 */
    public static final String UNIT = "ou=";

    /** 角色，岗位，用户组，人员 */
    public static final String CN = "cn=";

    /** 机构层次分隔符 */
    public static final String SEPARATOR = ",";

    /**
     * 根据组织节点类型得到
     *
     * @param orgType 组织类型
     * @return {@link String}
     */
    public static String getOrgLevel(OrgTypeEnum orgType) {
        String orgLevel = null;
        switch (orgType) {
            case ORGANIZATION:
                orgLevel = ORGANIZATION;
                break;
            case DEPARTMENT:
                orgLevel = UNIT;
                break;
            default:
                orgLevel = CN;
                break;
        }
        return orgLevel;
    }

    private OrgLevelConsts() {
        throw new IllegalStateException("OrgLevelConsts class");
    }
}
