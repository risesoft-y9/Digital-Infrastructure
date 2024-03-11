package net.risesoft.enums.platform;

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

    /** 组织机构树，包含机构、部门、用户组、岗位、人员 */
    TREE_TYPE_ORG("tree_type_org", "组织机构树，包含机构、部门、用户组、岗位、人员"),

    /** 人员组织机构树，包含机构、部门、用户组、人员 */
    TREE_TYPE_ORG_PERSON("tree_type_org_person", "人员组织机构树，包含机构、部门、用户组、人员"),

    /** 岗位组织机构树，包含机构、部门、用户组、岗位 */
    TREE_TYPE_ORG_POSITION("tree_type_org_position", "岗位组织机构树，包含机构、部门、用户组、岗位"),

    /** 三员树，包含机构、部门、三员 */
    TREE_TYPE_ORG_MANAGER("tree_type_org_manager", "三员树，包含机构、部门、三员"),

    /** 委办局树，包含机构、委办局 */
    TREE_TYPE_BUREAU("tree_type_bureau", "委办局树，包含机构、委办局"),

    /** 部门树，包含机构、部门 */
    TREE_TYPE_DEPT("tree_type_dept", "部门树，包含机构、部门"),

    /** 用户组树，包含机构、部门、用户组 */
    TREE_TYPE_GROUP("tree_type_group", "用户组树，包含机构、部门、用户组"),

    /** 岗位树，包含机构、部门、岗位 */
    TREE_TYPE_POSITION("tree_type_position", "岗位树，包含机构、部门、岗位"),

    /** 人员树，包含机构、部门、人员 */
    TREE_TYPE_PERSON("tree_type_person", "人员树，包含机构、部门、人员");

    private final String value;
    private final String description;

}
