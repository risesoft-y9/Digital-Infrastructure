package net.risesoft.entity.org;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.risesoft.consts.DefaultConsts;
import net.risesoft.enums.platform.org.GroupTypeEnum;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.model.platform.org.Group;

/**
 * Y9Group业务规则单元测试
 *
 * @author shidaobang
 * @date 2026/01/09
 */
class Y9GroupTest {

    private Y9Group group;

    @BeforeEach
    void setUp() {
        group = new Y9Group();
    }

    @Test
    void testConstructorWithGroupAndParent() {
        // 测试使用Group对象和父节点创建Y9Group的构造函数
        Y9Department parentDept = new Y9Department(); // 父节点是部门
        parentDept.setId("parent456");
        parentDept.setDn("ou=上级部门,o=组织机构");
        parentDept.setGuidPath("org123,parent456"); // 父部门的guidPath

        Group groupModel = new Group();
        groupModel.setName("测试用户组");
        groupModel.setDescription("测试用户组描述");
        groupModel.setId("group123"); // 设置一个ID以避免调用ID生成器
        groupModel.setType(GroupTypeEnum.PERSON); // 设置组类型为人员组

        Y9Group newGroup = new Y9Group(groupModel, parentDept, 5);

        assertEquals("测试用户组", newGroup.getName());
        assertEquals("测试用户组描述", newGroup.getDescription());
        assertEquals("parent456", newGroup.getParentId());
        assertEquals("group123", newGroup.getId()); // 应该保持原始ID
        assertEquals(GroupTypeEnum.PERSON, newGroup.getType());
        assertNotEquals(DefaultConsts.TAB_INDEX, newGroup.getTabIndex()); // tabIndex应该被设置为nextSubTabIndex
        assertEquals(5, newGroup.getTabIndex());
        // 验证guidPath的构建是否正确：父节点guidPath + 当前节点ID
        assertEquals("org123,parent456,group123", newGroup.getGuidPath());
        // 验证dn的构建是否正确：当前节点类型+名称 + 父节点dn
        assertEquals("cn=测试用户组,ou=上级部门,o=组织机构", newGroup.getDn());
    }

    @Test
    void testConstructorWithOrganizationAsParent() {
        // 测试使用组织机构作为父节点创建Y9Group
        // 用户组可以以组织机构作为父节点（作为第一层用户组）
        Y9Organization parentOrg = new Y9Organization();
        parentOrg.setId("org789");
        parentOrg.setDn("o=组织机构");
        parentOrg.setGuidPath("org789"); // 组织机构的guidPath只包含自己的ID

        Group groupModel = new Group();
        groupModel.setName("一级用户组");
        groupModel.setDescription("组织机构下的第一个用户组");
        groupModel.setId("group123");
        groupModel.setType(GroupTypeEnum.POSITION); // 设置组类型为岗位组

        Y9Group newGroup = new Y9Group(groupModel, parentOrg, 1);

        assertEquals("一级用户组", newGroup.getName());
        assertEquals("组织机构下的第一个用户组", newGroup.getDescription());
        assertEquals("org789", newGroup.getParentId());
        assertEquals("group123", newGroup.getId());
        assertEquals(1, newGroup.getTabIndex());
        assertEquals(GroupTypeEnum.POSITION, newGroup.getType());
        // 验证guidPath的构建：父节点guidPath + 当前节点ID
        assertEquals("org789,group123", newGroup.getGuidPath());
        // 验证dn的构建：当前节点类型+名称 + 父节点dn
        assertEquals("cn=一级用户组,o=组织机构", newGroup.getDn());
    }


    @Test
    void testChangeParent_MoveToDifferentParent_OrganizationAsParent() {
        // 测试正常移动到不同父节点的业务规则 - 父节点为组织机构
        Y9Organization newParent = new Y9Organization();
        newParent.setId("orgParent");
        newParent.setDn("o=顶级组织");
        newParent.setGuidPath("orgParent");

        group.setId("group123");
        group.setName("测试用户组");
        group.setDn("cn=原用户组名,o=原组织");
        group.setGuidPath("oldParent,group123");

        // 正常移动到新父节点
        group.changeParent(newParent, 25);

        assertEquals("orgParent", group.getParentId());
        assertEquals(25, group.getTabIndex());
        assertEquals("cn=测试用户组,o=顶级组织", group.getDn());
        assertEquals("orgParent,group123", group.getGuidPath());
    }

    @Test
    void testChangeParent_MoveToDifferentParent_DepartmentAsParent() {
        // 测试正常移动到不同父节点的业务规则 - 父节点为部门
        Y9Department newParent = new Y9Department();
        newParent.setId("deptParent");
        newParent.setDn("ou=上级部门,o=组织机构");
        newParent.setGuidPath("orgParent,deptParent");

        group.setId("group123");
        group.setName("测试用户组");
        group.setDn("cn=原用户组名,o=原组织");
        group.setGuidPath("oldParent,group123");

        // 正常移动到新父节点
        group.changeParent(newParent, 30);

        assertEquals("deptParent", group.getParentId());
        assertEquals(30, group.getTabIndex());
        assertEquals("cn=测试用户组,ou=上级部门,o=组织机构", group.getDn());
        assertEquals("orgParent,deptParent,group123", group.getGuidPath());
    }

    @Test
    void testUpdateGroup() {
        // 测试更新用户组信息的业务规则
        Y9Organization parentOrg = new Y9Organization();
        parentOrg.setId("parent999");
        parentOrg.setDn("o=上级组织机构");
        parentOrg.setGuidPath("parent999");

        group.setId("group999");
        group.setName("原用户组名");
        group.setDn("cn=原用户组名,o=原组织");
        group.setGuidPath("oldParent,group999");
        group.setType(GroupTypeEnum.PERSON);

        Group updatedGroup = new Group();
        updatedGroup.setName("更新后用户组名");
        updatedGroup.setDescription("更新后描述");
        updatedGroup.setType(GroupTypeEnum.POSITION);

        // 执行更新
        group.update(updatedGroup, parentOrg);

        assertEquals("更新后用户组名", group.getName());
        assertEquals("更新后描述", group.getDescription());
        assertEquals(GroupTypeEnum.POSITION, group.getType());
        // 验证dn现在应该是当前用户组名称与父节点dn的组合
        assertEquals("cn=更新后用户组名,o=上级组织机构", group.getDn());
        // 验证guidPath现在应该是父节点的guidPath + 当前用户组ID
        assertEquals("parent999,group999", group.getGuidPath());
    }

    @Test
    void testUpdateGroup_NameChange() {
        // 测试仅更改用户组名称时dn的变化
        Y9Organization parentOrg = new Y9Organization();
        parentOrg.setId("org111");
        parentOrg.setDn("o=主组织");
        parentOrg.setGuidPath("org111");

        group.setId("group222");
        group.setName("原用户组");
        group.setDn("cn=原用户组,o=主组织"); // 原来的dn
        group.setGuidPath("org111,group222");
        group.setType(GroupTypeEnum.PERSON);

        // 更新用户组名称
        Group updatedGroup = new Group();
        updatedGroup.setName("新用户组名");
        updatedGroup.setDescription("用户组描述");

        group.update(updatedGroup, parentOrg);

        // 验证名称更改后dn也相应变化
        assertEquals("新用户组名", group.getName());
        assertEquals("cn=新用户组名,o=主组织", group.getDn()); // dn应该反映新名称
        assertEquals("org111,group222", group.getGuidPath()); // guidPath不应改变
    }

    @Test
    void testChangeTabIndex() {
        // 测试改变tabIndex的业务规则
        assertEquals(DefaultConsts.TAB_INDEX, group.getTabIndex());

        group.changeTabIndex(99);
        assertEquals(99, group.getTabIndex());
    }

    @Test
    void testChangeProperties() {
        // 测试改变扩展属性的业务规则
        assertNull(group.getProperties());

        group.changeProperties("newProperties");
        assertEquals("newProperties", group.getProperties());
    }

    @Test
    void testChangeDisabled_Enable() {
        // 测试启用用户组的业务规则 - 当所有后代节点都已禁用时
        group.setDisabled(true); // 当前用户组是禁用状态(true)，所以目标是启用(false)

        Boolean result = group.changeDisabled(); // 切换禁用状态

        assertFalse(result); // 因为当前是禁用(true)，切换后是启用(false)，所以返回false
        assertFalse(group.getDisabled()); // 用户组现在是启用状态
    }

    @Test
    void testChangeDisabled_Disable() {
        // 测试禁用用户组的业务规则 - 当存在未禁用的后代节点时
        group.setDisabled(false); // 当前用户组是启用状态(false)，所以目标是禁用(true)

        Boolean result = group.changeDisabled(); // 切换禁用状态

        assertTrue(result.booleanValue()); // 因为当前是启用(false)，切换后是禁用(true)，所以返回true
        assertTrue(group.getDisabled()); // 用户组现在是启用状态
    }

    @Test
    void testOrgTypeSetInConstructor() {
        // 测试在构造函数中设置组织类型为GROUP
        Y9Group grp = new Y9Group();
        assertEquals(OrgTypeEnum.GROUP, grp.getOrgType());
    }

    @Test
    void testNoArgsConstructor() {
        // 测试无参构造函数
        Y9Group grp = new Y9Group();
        assertNotNull(grp);
        assertEquals(OrgTypeEnum.GROUP, grp.getOrgType());
        assertFalse(grp.getDisabled());
    }

    @Test
    void testGetType_DefaultValue() {
        // 测试type字段的默认值
        Y9Group grp = new Y9Group();
        assertEquals(GroupTypeEnum.PERSON, grp.getType());
    }
}