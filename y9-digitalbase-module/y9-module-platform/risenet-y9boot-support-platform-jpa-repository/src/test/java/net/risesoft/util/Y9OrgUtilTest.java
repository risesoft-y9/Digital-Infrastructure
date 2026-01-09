package net.risesoft.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.risesoft.entity.org.Y9Department;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.enums.platform.org.OrgTypeEnum;

/**
 * Y9OrgUtil 单元测试
 *
 * @author shidaobang
 * @date 2025/12/18
 */
public class Y9OrgUtilTest {

    @Test
    public void testIsCurrentOrAncestorChanged() {
        Y9Person originPerson = new Y9Person();
        originPerson.setDn("cn=测试人员,ou=测试部门,o=测试组织");
        originPerson.setGuidPath("1,2,3");
        Y9Person updatePerson = new Y9Person();
        updatePerson.setDn("cn=测试人员,ou=测试部门,o=测试组织");
        updatePerson.setGuidPath("1,2,3");

        assertFalse(Y9OrgUtil.isCurrentOrAncestorChanged(originPerson, updatePerson));

        updatePerson.setDn("cn=测试人员1,ou=测试部门,o=测试组织");
        assertTrue(Y9OrgUtil.isCurrentOrAncestorChanged(originPerson, updatePerson));

        updatePerson.setDn("cn=测试人员,ou=测试部门1,o=测试组织");
        assertTrue(Y9OrgUtil.isCurrentOrAncestorChanged(originPerson, updatePerson));

        updatePerson.setDn("cn=测试人员,ou=测试部门,o=测试组织");
        updatePerson.setGuidPath("1,4,3");
        assertTrue(Y9OrgUtil.isCurrentOrAncestorChanged(originPerson, updatePerson));
    }

    @Test
    public void testIsAncestorOf() {
        Y9Department y9Department = new Y9Department();
        y9Department.setGuidPath("1,2");
        Y9Person y9Person = new Y9Person();
        y9Person.setGuidPath("1,2,3");

        assertTrue(Y9OrgUtil.isAncestorOf(y9Department, y9Person));

        y9Person.setGuidPath("1,2,5,3");
        assertTrue(Y9OrgUtil.isAncestorOf(y9Department, y9Person));

        y9Person.setGuidPath("1,4,3");
        assertFalse(Y9OrgUtil.isAncestorOf(y9Department, y9Person));

        assertFalse(Y9OrgUtil.isAncestorOf(y9Department, y9Department));
    }

    @Test
    public void testIsDescendantOf() {
        Y9Department y9Department = new Y9Department();
        y9Department.setGuidPath("1,2");
        Y9Person y9Person = new Y9Person();
        y9Person.setGuidPath("1,2,3");

        assertTrue(Y9OrgUtil.isDescendantOf(y9Person, y9Department));

        y9Person.setGuidPath("1,2,5,3");
        assertTrue(Y9OrgUtil.isDescendantOf(y9Person, y9Department));

        y9Person.setGuidPath("1,4,3");
        assertFalse(Y9OrgUtil.isDescendantOf(y9Person, y9Department));

        assertFalse(Y9OrgUtil.isDescendantOf(y9Department, y9Department));
    }

    @Test
    public void testIsMoved() {
        Y9Person originPerson = new Y9Person();
        originPerson.setParentId("1");
        Y9Person updatePerson = new Y9Person();
        updatePerson.setParentId("1");

        assertFalse(Y9OrgUtil.isMoved(originPerson, updatePerson));

        updatePerson.setParentId("2");
        assertTrue(Y9OrgUtil.isMoved(originPerson, updatePerson));
    }

    @Test
    public void testIsRenamed() {
        Y9Person originPerson = new Y9Person();
        originPerson.setName("test");
        Y9Person updatePerson = new Y9Person();
        updatePerson.setName("test");

        assertFalse(Y9OrgUtil.isRenamed(originPerson, updatePerson));

        updatePerson.setName("test2");
        assertTrue(Y9OrgUtil.isRenamed(originPerson, updatePerson));
    }

    @Test
    public void testIsSameOf() {
        Y9Department y9Department = new Y9Department();
        y9Department.setId("1");
        Y9Person y9Person = new Y9Person();
        y9Person.setId("2");

        assertTrue(Y9OrgUtil.isSameOf(y9Department, y9Department));
        assertFalse(Y9OrgUtil.isSameOf(y9Department, y9Person));
    }

    @Test
    public void testIsTabIndexChanged() {
        Y9Person originPerson = new Y9Person();
        originPerson.setTabIndex(1);
        Y9Person updatePerson = new Y9Person();
        updatePerson.setTabIndex(1);

        assertFalse(Y9OrgUtil.isTabIndexChanged(originPerson, updatePerson));

        updatePerson.setTabIndex(2);
        assertTrue(Y9OrgUtil.isTabIndexChanged(originPerson, updatePerson));
    }

    @Test
    public void testBuildDn() {
        // 测试带有父DN的情况
        String dn = Y9OrgUtil.buildDn(OrgTypeEnum.PERSON, "测试人员", "ou=测试部门,o=测试组织");
        assertEquals("cn=测试人员,ou=测试部门,o=测试组织", dn);

        // 测试没有父DN的情况
        String dnWithoutParent = Y9OrgUtil.buildDn(OrgTypeEnum.ORGANIZATION, "测试组织", null);
        assertEquals("o=测试组织", dnWithoutParent);

        // 测试不同组织类型
        String departmentDn = Y9OrgUtil.buildDn(OrgTypeEnum.DEPARTMENT, "测试部门", "o=测试组织");
        assertEquals("ou=测试部门,o=测试组织", departmentDn);
    }

    @Test
    public void testBuildGuidPath() {
        // 测试带有父GUID路径的情况
        String guidPath = Y9OrgUtil.buildGuidPath("1,2", "3");
        assertEquals("1,2,3", guidPath);

        // 测试没有父GUID路径的情况
        String guidPathWithoutParent = Y9OrgUtil.buildGuidPath(null, "1");
        assertEquals("1", guidPathWithoutParent);

        // 测试空字符串父GUID路径的情况
        String guidPathWithEmptyParent = Y9OrgUtil.buildGuidPath("", "1");
        assertEquals("1", guidPathWithEmptyParent);
    }

    @Test
    public void testDnToNamePath() {
        // 测试正常转换
        String namePath = Y9OrgUtil.dnToNamePath("cn=测试人员,ou=测试部门,o=测试组织");
        assertEquals("测试组织 > 测试部门 > 测试人员", namePath);

        // 测试自定义分隔符
        String namePathWithSeparator = Y9OrgUtil.dnToNamePath("cn=测试人员,ou=测试部门,o=测试组织", "-");
        assertEquals("测试组织-测试部门-测试人员", namePathWithSeparator);

        // 测试空DN
        assertNull(Y9OrgUtil.dnToNamePath(null));
        assertNull(Y9OrgUtil.dnToNamePath(""));
    }
}