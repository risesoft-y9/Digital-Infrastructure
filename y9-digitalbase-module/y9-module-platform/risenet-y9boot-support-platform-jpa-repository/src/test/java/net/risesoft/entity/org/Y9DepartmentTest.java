package net.risesoft.entity.org;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.risesoft.consts.DefaultConsts;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.model.platform.org.Department;
import net.risesoft.y9.exception.Y9BusinessException;

/**
 * Y9Department业务规则单元测试
 *
 * @author shidaobang
 * @date 2026/01/08
 */
class Y9DepartmentTest {

    private Y9Department department;

    @BeforeEach
    void setUp() {
        department = new Y9Department();
    }

    @Test
    void testConstructorWithDepartmentAndParent() {
        // 测试使用Department对象和父节点创建Y9Department的构造函数
        // 部门可以作为中间节点，所以它可以有父节点（可以是组织机构或其他部门）
        Y9Department parentDept = new Y9Department();  // 父节点是另一个部门
        parentDept.setId("parent456");
        parentDept.setDn("ou=上级部门,o=组织机构");
        parentDept.setGuidPath("org123,parent456"); // 父部门的guidPath

        Department departmentModel = new Department();
        departmentModel.setName("测试部门");
        departmentModel.setDescription("测试部门描述");
        departmentModel.setId("test123"); // 设置一个ID以避免调用ID生成器

        Y9Department newDepartment = new Y9Department(departmentModel, parentDept, 5);

        assertEquals("测试部门", newDepartment.getName());
        assertEquals("测试部门描述", newDepartment.getDescription());
        assertEquals("parent456", newDepartment.getParentId());
        assertEquals("test123", newDepartment.getId()); // 应该保持原始ID
        assertNotEquals(DefaultConsts.TAB_INDEX, newDepartment.getTabIndex()); // tabIndex应该被设置为nextSubTabIndex
        assertEquals(5, newDepartment.getTabIndex());
        // 验证guidPath的构建是否正确：父节点guidPath + 当前节点ID
        assertEquals("org123,parent456,test123", newDepartment.getGuidPath());
        // 验证dn的构建是否正确：当前节点类型+名称 + 父节点dn
        assertEquals("ou=测试部门,ou=上级部门,o=组织机构", newDepartment.getDn());
    }

    @Test
    void testConstructorWithOrganizationAsParent() {
        // 测试使用组织机构作为父节点创建Y9Department
        // 部门可以以组织机构作为父节点（作为第一层部门）
        Y9Organization parentOrg = new Y9Organization();
        parentOrg.setId("org789");
        parentOrg.setDn("o=组织机构");
        parentOrg.setGuidPath("org789"); // 组织机构的guidPath只包含自己的ID

        Department departmentModel = new Department();
        departmentModel.setName("一级部门");
        departmentModel.setDescription("组织机构下的第一个部门");
        departmentModel.setId("dept123");

        Y9Department newDepartment = new Y9Department(departmentModel, parentOrg, 1);

        assertEquals("一级部门", newDepartment.getName());
        assertEquals("组织机构下的第一个部门", newDepartment.getDescription());
        assertEquals("org789", newDepartment.getParentId());
        assertEquals("dept123", newDepartment.getId());
        assertEquals(1, newDepartment.getTabIndex());
        // 验证guidPath的构建：父节点guidPath + 当前节点ID
        assertEquals("org789,dept123", newDepartment.getGuidPath());
        // 验证dn的构建：当前节点类型+名称 + 父节点dn
        assertEquals("ou=一级部门,o=组织机构", newDepartment.getDn());
    }

    @Test
    void testChangeParent_MoveToSubDepartmentNotPermitted() {
        // 测试不允许移动到子部门的业务规则（防止循环引用）
        // 场景：当前部门A试图移动到部门B下，但部门B本身就是部门A的子树中的节点
        // 这种情况会形成循环引用，所以必须阻止
        
        // 当前部门A的ID是"deptA"
        String departmentId = "deptA";
        department.setId(departmentId);
        department.setName("部门A");
        department.setGuidPath("orgRoot,parentOrg,deptA");
        
        // 构造一个目标父节点B，它实际上是部门A的子节点
        Y9Department targetParentB = new Y9Department(); 
        targetParentB.setId("deptB");
        targetParentB.setName("部门B");
        targetParentB.setGuidPath("orgRoot,deptA,deptB"); 
        targetParentB.setDn("ou=部门B,ou=部门A,o=组织");

        // 尝试将部门A移动到部门B下（部门B是部门A的子树成员），应该抛出异常
        Y9BusinessException exception =
            assertThrows(Y9BusinessException.class, () -> department.changeParent(targetParentB, 10));

        assertEquals(10004, exception.getCode());
    }

    @Test
    void testChangeParent_MoveToDifferentParent_DepartmentAsParent() {
        // 测试正常移动到不同父节点的业务规则 - 父节点为部门
        Y9Department newParent = new Y9Department();
        newParent.setId("newParent");
        newParent.setDn("ou=上级部门,o=组织机构");
        newParent.setGuidPath("org123,parentDept"); // 父部门的guidPath
        newParent.setParentId("parent999");

        department.setId("department123");
        department.setName("测试部门");
        department.setDn("ou=原部门名,o=原组织");
        department.setGuidPath("oldParent,department123"); // 原来的guidPath

        // 正常移动到新父节点
        department.changeParent(newParent, 20);

        assertEquals("newParent", department.getParentId());
        assertEquals(20, department.getTabIndex());
        assertEquals("ou=测试部门,ou=上级部门,o=组织机构", department.getDn());
        assertEquals("org123,parentDept,department123", department.getGuidPath());
    }

    @Test
    void testChangeParent_MoveToDifferentParent_OrganizationAsParent() {
        // 测试正常移动到不同父节点的业务规则 - 父节点为组织机构
        Y9Organization newParent = new Y9Organization();
        newParent.setId("orgParent");
        newParent.setDn("o=顶级组织");
        newParent.setGuidPath("orgParent");

        department.setId("department123");
        department.setName("测试部门");
        department.setDn("ou=原部门名,o=原组织");
        department.setGuidPath("oldParent,department123");

        // 正常移动到新父节点
        department.changeParent(newParent, 25);

        assertEquals("orgParent", department.getParentId());
        assertEquals(25, department.getTabIndex());
        assertEquals("ou=测试部门,o=顶级组织", department.getDn());
        assertEquals("orgParent,department123", department.getGuidPath());
    }

    @Test
    void testUpdateDepartment() {
        // 测试更新部门信息的业务规则
        Y9Organization parentOrg = new Y9Organization();
        parentOrg.setId("parent999");
        parentOrg.setDn("o=上级组织机构");
        parentOrg.setGuidPath("parent999");

        department.setId("department999");
        department.setName("原部门名");
        department.setDn("ou=原部门名,o=原组织");
        department.setGuidPath("oldParent,department999");

        Department updatedDepartment = new Department();
        updatedDepartment.setName("更新后部门名");
        updatedDepartment.setDescription("更新后描述");

        // 执行更新
        department.update(updatedDepartment, parentOrg);

        assertEquals("更新后部门名", department.getName());
        assertEquals("更新后描述", department.getDescription());
        // 验证dn现在应该是当前部门名称与父节点dn的组合
        assertEquals("ou=更新后部门名,o=上级组织机构", department.getDn());
        // 验证guidPath现在应该是父节点的guidPath + 当前部门ID
        assertEquals("parent999,department999", department.getGuidPath());
    }

    @Test
    void testUpdateDepartment_NameChange() {
        // 测试仅更改部门名称时dn的变化
        Y9Organization parentOrg = new Y9Organization();
        parentOrg.setId("org111");
        parentOrg.setDn("o=主组织");
        parentOrg.setGuidPath("org111");

        department.setId("dept222");
        department.setName("原部门");
        department.setDn("ou=原部门,o=主组织"); // 原来的dn
        department.setGuidPath("org111,dept222");

        // 更新部门名称
        Department updatedDepartment = new Department();
        updatedDepartment.setName("新部门名");
        updatedDepartment.setDescription("部门描述");

        department.update(updatedDepartment, parentOrg);

        // 验证名称更改后dn也相应变化
        assertEquals("新部门名", department.getName());
        assertEquals("ou=新部门名,o=主组织", department.getDn()); // dn应该反映新名称
        assertEquals("org111,dept222", department.getGuidPath()); // guidPath不应改变
    }

    @Test
    void testChangeTabIndex() {
        // 测试改变tabIndex的业务规则
        assertEquals(DefaultConsts.TAB_INDEX, department.getTabIndex());

        department.changeTabIndex(99);
        assertEquals(99, department.getTabIndex());
    }

    @Test
    void testChangeProperties() {
        // 测试改变扩展属性的业务规则
        assertNull(department.getProperties());

        department.changeProperties("newProperties");
        assertEquals("newProperties", department.getProperties());
    }

    @Test
    void testChangeDisabled_EnableWhenAllDescendantsDisabled() {
        // 测试启用部门的业务规则 - 当所有后代节点都已禁用时
        department.setDisabled(true); // 当前部门是禁用状态(true)，所以目标是启用(false)

        // 目标状态是!this.disabled = !true = false (启用)
        // 如果目标是启用(false)，条件 "if (targetStatus && !isAllDescendantsDisabled)" 不成立
        // 因为targetStatus是false，所以不会抛出异常，直接切换到启用状态
        Boolean result = department.changeDisabled(true); // 启用，并且所有后代都已禁用

        assertFalse(result); // 因为当前是禁用(true)，目标是启用(false)，所以返回false
        assertFalse(department.getDisabled()); // 部门现在是启用状态
    }

    @Test
    void testChangeDisabled_DisableWhenNotAllDescendantsDisabled() {
        // 测试禁用部门的业务规则 - 当存在未禁用的后代节点时
        department.setDisabled(false); // 当前部门是启用状态(false)，所以目标是禁用(true)

        // 目标状态是!this.disabled = !false = true (禁用)
        // 如果目标是禁用(true)且不是所有后代都已禁用(false)，则条件成立，抛出异常
        Y9BusinessException exception = assertThrows(Y9BusinessException.class, () -> department.changeDisabled(false));

        assertEquals(10023, exception.getCode());
    }

    @Test
    void testChangeDisabled_EnableWhenNotAllDescendantsDisabled() {
        // 测试启用部门的业务规则 - 即使存在未禁用的后代节点也可以启用
        department.setDisabled(true); // 当前部门是禁用状态(true)，所以目标是启用(false)

        // 目标状态是!this.disabled = !true = false (启用)
        // 条件 "if (targetStatus && !isAllDescendantsDisabled)" 不成立，因为targetStatus是false
        // 所以不会抛出异常，可以直接启用
        Boolean result = department.changeDisabled(false); // 启用，但并非所有后代都已禁用

        assertFalse(result); // 返回的是目标状态，即false
        assertFalse(department.getDisabled()); // 部门现在是启用状态
    }

    @Test
    void testOrgTypeSetInConstructor() {
        // 测试在构造函数中设置组织类型为DEPARTMENT
        Y9Department dept = new Y9Department();
        assertEquals(OrgTypeEnum.DEPARTMENT, dept.getOrgType());
    }

    @Test
    void testNoArgsConstructor() {
        // 测试无参构造函数
        Y9Department dept = new Y9Department();
        assertNotNull(dept);
        assertEquals(OrgTypeEnum.DEPARTMENT, dept.getOrgType());
        assertFalse(dept.getDisabled());
    }
}