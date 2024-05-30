package net.risesoft;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Person;
import net.risesoft.util.Y9OrgUtil;

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
        Y9Person y9Person = new Y9Person();

        assertTrue(Y9OrgUtil.isSameOf(y9Department, y9Department));
        assertFalse(Y9OrgUtil.isSameOf(y9Department, y9Person));
    }

}
