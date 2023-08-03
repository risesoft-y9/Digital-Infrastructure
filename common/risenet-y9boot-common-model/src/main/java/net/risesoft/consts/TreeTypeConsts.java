package net.risesoft.consts;

/**
 * 树类型常量
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
public class TreeTypeConsts {

    /** 组织机构树，显示 机构、部门、用户组、岗位、人员 */
    public static final String TREE_TYPE_ORG = "tree_type_org";

    /** 人员组织机构树，显示 机构、部门、用户组、人员 */
    public static final String TREE_TYPE_ORG_PERSON = "tree_type_org_person";

    /** 岗位组织机构树，显示 机构、部门、用户组、岗位 */
    public static final String TREE_TYPE_ORG_POSITION = "tree_type_org_position";

    /** 三员树，显示 机构、部门、三员 */
    public static final String TREE_TYPE_ORG_MANAGER = "tree_type_org_manager";

    /** 委办局树，显示 机构、委办局 */
    public static final String TREE_TYPE_BUREAU = "tree_type_bureau";

    /** 部门树， 显示 机构、部门 */
    public static final String TREE_TYPE_DEPT = "tree_type_dept";

    /** 用户组树， 显示 机构、部门、用户组 */
    public static final String TREE_TYPE_GROUP = "tree_type_group";

    /** 岗位树， 显示 机构、部门、岗位 */
    public static final String TREE_TYPE_POSITION = "tree_type_position";

    /** 人员树，显示 机构、部门、人员 */
    public static final String TREE_TYPE_PERSON = "tree_type_person";

    private TreeTypeConsts() {
        throw new IllegalStateException("TreeTypeConsts class");
    }
}
