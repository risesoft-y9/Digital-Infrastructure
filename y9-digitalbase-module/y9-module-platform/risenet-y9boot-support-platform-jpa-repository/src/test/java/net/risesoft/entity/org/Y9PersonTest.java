package net.risesoft.entity.org;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.enums.platform.org.SexEnum;
import net.risesoft.model.platform.org.Person;
import net.risesoft.y9.util.signing.Y9MessageDigestUtil;

class Y9PersonTest {

    private Person personModel;
    private Y9Organization parentOrg;
    private Y9Department parentDept;
    private List<Y9OrgBase> ancestorList;

    @BeforeEach
    void setUp() {
        // 初始化Person模型对象
        personModel = new Person();
        personModel.setId("person123");
        personModel.setName("张三");
        personModel.setLoginName("zhangsan");
        personModel.setMobile("13800138000");
        personModel.setEmail("zhangsan@example.com");
        personModel.setPassword("plainPassword123");
        personModel.setSex(SexEnum.MALE);

        // 初始化父级组织
        parentOrg = new Y9Organization();
        parentOrg.setId("org123");
        parentOrg.setName("测试组织");
        parentOrg.setDn("o=测试组织");
        parentOrg.setGuidPath("org123"); // 设置GUID路径

        // 初始化父级部门
        parentDept = new Y9Department();
        parentDept.setId("dept123");
        parentDept.setName("测试部门");
        parentDept.setDn("ou=测试部门,o=测试组织");
        parentDept.setGuidPath("org123,dept123"); // 设置GUID路径

        // 初始化祖先节点列表
        ancestorList = new ArrayList<>();
        ancestorList.add(parentDept);
        ancestorList.add(parentOrg);
    }

    @Test
    void testConstructorWithPersonAndParent() {
        String defaultPassword = "default123";
        Y9Person person = new Y9Person(personModel, parentDept, ancestorList, 1, defaultPassword);

        // 验证基本属性复制
        assertEquals("张三", person.getName());
        assertEquals("zhangsan", person.getLoginName());
        assertEquals("13800138000", person.getMobile());
        assertEquals("zhangsan@example.com", person.getEmail());
        assertEquals(SexEnum.MALE, person.getSex());

        // 验证ID生成
        assertNotNull(person.getId());

        // 验证DN生成
        assertEquals("cn=张三,ou=测试部门,o=测试组织", person.getDn());

        // 验证GUID_PATH生成 - 应该是父节点guidPath + 当前节点ID
        assertEquals("org123,dept123,person123", person.getGuidPath());

        // 验证密码加密
        assertTrue(Y9MessageDigestUtil.bcryptMatch("plainPassword123", person.getPassword()));

        // 验证是否在编状态
        assertEquals(Integer.valueOf(1), person.getOfficial());

        // 验证父节点ID
        assertEquals("dept123", person.getParentId());

        // 验证排序路径
        assertEquals("00000,00000,00001", person.getOrderedPath());
    }

    @Test
    void testUpdatePerson() {
        String defaultPassword = "default123";
        Y9Person person = new Y9Person(personModel, parentDept, ancestorList, 1, defaultPassword);

        // 准备更新数据
        Person updatedPerson = new Person();
        updatedPerson.setName("李四");
        updatedPerson.setLoginName("lisi");
        updatedPerson.setMobile("13900139000");
        updatedPerson.setEmail("lisi@example.com");

        // 执行更新
        person.update(updatedPerson, parentOrg, ancestorList);

        // 验证更新后的属性
        assertEquals("李四", person.getName());
        assertEquals("lisi", person.getLoginName());
        assertEquals("13900139000", person.getMobile());
        assertEquals("lisi@example.com", person.getEmail());

        // 验证DN更新
        assertEquals("cn=李四,o=测试组织", person.getDn());

        // 验证GUID_PATH更新 - 应该是父节点guidPath + 当前节点ID
        assertEquals("org123,person123", person.getGuidPath());

        // 验证排序路径更新 - 应该包含父节点的tabIndex和当前节点的tabIndex
        assertEquals("00000,00000,00001", person.getOrderedPath()); // 现在父节点tabIndex是0，当前节点是1
    }

    @Test
    void testChangePassword() {
        String defaultPassword = "default123";
        Y9Person person = new Y9Person(personModel, parentDept, ancestorList, 1, defaultPassword);

        String oldPassword = "plainPassword123";
        String newPassword = "newPassword456";

        // 更改密码
        person.changePassword(oldPassword, newPassword);

        // 验证新密码是否正确加密
        assertTrue(Y9MessageDigestUtil.bcryptMatch("newPassword456", person.getPassword()));
    }

    @Test
    void testChangePasswordWithoutOldPassword() {
        String defaultPassword = "default123";
        Y9Person person = new Y9Person(personModel, parentDept, ancestorList, 1, defaultPassword);

        String newPassword = "newPassword456";

        // 更改密码，不提供旧密码（应成功更改）
        person.changePassword(null, newPassword);

        // 验证新密码是否正确加密
        assertTrue(Y9MessageDigestUtil.bcryptMatch("newPassword456", person.getPassword()));
    }

    @Test
    void testChangePasswordWrongOldPassword() {
        String defaultPassword = "default123";
        Y9Person person = new Y9Person(personModel, parentDept, ancestorList, 1, defaultPassword);

        String wrongOldPassword = "wrongPassword";
        String newPassword = "newPassword456";

        // 尝试使用错误的旧密码更改密码，应该抛出异常
        assertThrows(net.risesoft.y9.exception.Y9BusinessException.class, () -> {
            person.changePassword(wrongOldPassword, newPassword);
        });
    }

    @Test
    void testChangeParent() {
        String defaultPassword = "default123";
        Y9Person person = new Y9Person(personModel, parentDept, ancestorList, 1, defaultPassword);

        // 记录原始值
        String originalDn = person.getDn();
        String originalGuidPath = person.getGuidPath();

        // 更改父节点
        int nextSubTabIndex = 2;
        person.changeParent(parentOrg, nextSubTabIndex, ancestorList);

        // 验证父节点ID更改
        assertEquals("org123", person.getParentId());

        // 验证DN更新
        assertEquals("cn=张三,o=测试组织", person.getDn());
        assertNotEquals(originalDn, person.getDn());

        // 验证GUID_PATH更新 - 应该是父节点guidPath + 当前节点ID
        assertEquals("org123,person123", person.getGuidPath());
        assertNotEquals(originalGuidPath, person.getGuidPath());

        // 验证tabIndex更新
        assertEquals(Integer.valueOf(2), person.getTabIndex());

        // 验证排序路径更新 - 应该是父节点tabIndex(0) + 当前节点tabIndex(2)
        assertEquals("00000,00000,00002", person.getOrderedPath());
    }

    @Test
    void testChangeDisabled() {
        String defaultPassword = "default123";
        Y9Person person = new Y9Person(personModel, parentDept, ancestorList, 1, defaultPassword);

        // 初始状态应该是启用的
        assertFalse(person.getDisabled());

        // 更改禁用状态
        Boolean newStatus = person.changeDisabled();

        // 验证状态已更改
        assertTrue(person.getDisabled());
        assertTrue(newStatus);
        assertEquals(Boolean.TRUE, newStatus);

        // 再次更改状态
        Boolean anotherStatus = person.changeDisabled();
        assertFalse(person.getDisabled());
        assertFalse(anotherStatus);
    }

    @Test
    void testChangeTabIndex() {
        String defaultPassword = "default123";
        Y9Person person = new Y9Person(personModel, parentDept, ancestorList, 1, defaultPassword);

        // 初始tabIndex
        assertEquals(Integer.valueOf(1), person.getTabIndex());

        // 更改tabIndex
        person.changeTabIndex(5, ancestorList);

        // 验证tabIndex更改
        assertEquals(Integer.valueOf(5), person.getTabIndex());

        // 验证排序路径也相应更新 - 应该是父节点tabIndex(0) + 当前节点tabIndex(5)
        assertEquals("00000,00000,00005", person.getOrderedPath());
    }

    @Test
    void testEmailValidation() {
        // 设置空邮箱
        personModel.setEmail("");
        String defaultPassword = "default123";
        Y9Person person = new Y9Person(personModel, parentDept, ancestorList, 1, defaultPassword);

        // 验证空邮箱被设置为null
        assertNull(person.getEmail());

        // 测试更新时邮箱为空的情况
        Person updatedPerson = new Person();
        updatedPerson.setName("李四");
        updatedPerson.setEmail("");

        person.update(updatedPerson, parentOrg, ancestorList);
        assertNull(person.getEmail());
    }

    @Test
    void testPasswordEncryptionForImportedPasswords() {
        String importedBcryptPassword = "$2a$10$TeJsAc449N14ebVjhL8rPenixB.fzzcJpfB6M7YuJW/OQtWoTiDXG"; // 这是一个有效的BCrypt哈希
        personModel.setPassword(importedBcryptPassword);

        String defaultPassword = "default123";
        Y9Person person = new Y9Person(personModel, parentDept, ancestorList, 1, defaultPassword);

        // 验证导入的BCrypt密码不会被重复加密，所以值会变化，但应该能被匹配
        assertTrue(person.getPassword().startsWith("$2a$10$")); // 检查是否是BCrypt格式
        assertEquals(importedBcryptPassword, person.getPassword());
    }

    @Test
    void testOrderedPathGeneration() {
        String defaultPassword = "default123";
        Y9Person person = new Y9Person(personModel, parentDept, ancestorList, 3, defaultPassword);

        // 验证排序路径生成
        assertEquals("00000,00000,00003", person.getOrderedPath());

        // 测试更新时排序路径的变化
        person.changeTabIndex(7, ancestorList);
        assertEquals("00000,00000,00007", person.getOrderedPath());
    }

    @Test
    void testOrgTypeInitialization() {
        String defaultPassword = "default123";
        Y9Person person = new Y9Person(personModel, parentDept, ancestorList, 1, defaultPassword);

        // 验证组织类型初始化为PERSON
        assertEquals(OrgTypeEnum.PERSON, person.getOrgType());
    }
}