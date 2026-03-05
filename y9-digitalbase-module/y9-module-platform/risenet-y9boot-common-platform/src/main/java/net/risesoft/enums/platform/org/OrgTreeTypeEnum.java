package net.risesoft.enums.platform.org;

import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import net.risesoft.enums.ValuedEnum;

/**
 * 树类型
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@RequiredArgsConstructor
@Getter
public enum OrgTreeTypeEnum implements ValuedEnum<String> {

    /**
     * 组织机构树，包含机构、部门、用户组、岗位、人员
     */
    TREE_TYPE_ORG("tree_type_org",
        Set.of(OrgTypeEnum.ORGANIZATION, OrgTypeEnum.DEPARTMENT, OrgTypeEnum.GROUP, OrgTypeEnum.POSITION,
            OrgTypeEnum.PERSON),
        "组织机构树，包含机构、部门、用户组、岗位、人员"),

    /**
     * 人员组织机构树，包含机构、部门、用户组、人员
     */
    TREE_TYPE_ORG_PERSON("tree_type_org_person",
        Set.of(OrgTypeEnum.ORGANIZATION, OrgTypeEnum.DEPARTMENT, OrgTypeEnum.GROUP, OrgTypeEnum.PERSON),
        "人员组织机构树，包含机构、部门、用户组、人员"),

    /**
     * 岗位组织机构树，包含机构、部门、用户组、岗位
     */
    TREE_TYPE_ORG_POSITION("tree_type_org_position",
        Set.of(OrgTypeEnum.ORGANIZATION, OrgTypeEnum.DEPARTMENT, OrgTypeEnum.GROUP, OrgTypeEnum.POSITION),
        "岗位组织机构树，包含机构、部门、用户组、岗位"),

    /**
     * 三员树，包含机构、部门、三员
     */
    TREE_TYPE_ORG_MANAGER("tree_type_org_manager",
        Set.of(OrgTypeEnum.ORGANIZATION, OrgTypeEnum.DEPARTMENT, OrgTypeEnum.MANAGER), "三员树，包含机构、部门、三员"),

    /**
     * 部门树，包含机构、部门
     */
    TREE_TYPE_DEPT("tree_type_dept", Set.of(OrgTypeEnum.ORGANIZATION, OrgTypeEnum.DEPARTMENT), "部门树，包含机构、部门"),

    /**
     * 用户组树，包含机构、部门、用户组
     */
    TREE_TYPE_GROUP("tree_type_group", Set.of(OrgTypeEnum.ORGANIZATION, OrgTypeEnum.DEPARTMENT, OrgTypeEnum.GROUP),
        "用户组树，包含机构、部门、用户组"),

    /**
     * 岗位树，包含机构、部门、岗位
     */
    TREE_TYPE_POSITION("tree_type_position",
        Set.of(OrgTypeEnum.ORGANIZATION, OrgTypeEnum.DEPARTMENT, OrgTypeEnum.POSITION), "岗位树，包含机构、部门、岗位"),

    /**
     * 人员树，包含机构、部门、人员
     */
    TREE_TYPE_PERSON("tree_type_person", Set.of(OrgTypeEnum.ORGANIZATION, OrgTypeEnum.DEPARTMENT, OrgTypeEnum.PERSON),
        "人员树，包含机构、部门、人员"),;

    private final String value;
    private final Set<OrgTypeEnum> orgTypes;
    private final String description;

    public boolean include(OrgTypeEnum orgType) {
        return orgTypes.contains(orgType);
    }
}
