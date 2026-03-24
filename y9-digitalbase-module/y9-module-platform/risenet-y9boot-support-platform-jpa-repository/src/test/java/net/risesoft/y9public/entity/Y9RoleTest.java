package net.risesoft.y9public.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.risesoft.consts.DefaultConsts;
import net.risesoft.consts.InitDataConsts;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.model.platform.Role;

/**
 * Y9Role业务规则单元测试
 *
 * @author Codex
 * @date 2026/03/24
 */
class Y9RoleTest {

    private Y9Role y9Role;

    @BeforeEach
    void setUp() {
        y9Role = new Y9Role();
    }

    @Test
    void testConstructorWithParentRole() {
        Y9Role parentRole = new Y9Role();
        parentRole.setId("parent123");
        parentRole.setDn("cn=上级角色,cn=根角色");
        parentRole.setGuidPath("root,parent123");

        Role role = new Role();
        role.setId("role123");
        role.setName("测试角色");
        role.setDescription("测试描述");

        Y9Role newRole = new Y9Role(role, parentRole, 5, "tenant123");

        assertEquals("role123", newRole.getId());
        assertEquals("测试角色", newRole.getName());
        assertEquals("测试描述", newRole.getDescription());
        assertEquals("parent123", newRole.getParentId());
        assertEquals(5, newRole.getTabIndex());
        assertEquals("tenant123", newRole.getTenantId());
        assertEquals("cn=测试角色,cn=上级角色,cn=根角色", newRole.getDn());
        assertEquals("root,parent123,role123", newRole.getGuidPath());
    }

    @Test
    void testConstructorWithoutParentRole() {
        Role role = new Role();
        role.setId("role123");
        role.setName("应用根角色");
        role.setParentId("app123");
        role.setAppId("");
        role.setSystemId(" ");

        Y9Role newRole = new Y9Role(role, null, 3, "tenant123");

        assertEquals("role123", newRole.getId());
        assertEquals("应用根角色", newRole.getName());
        assertEquals("app123", newRole.getParentId());
        assertEquals(3, newRole.getTabIndex());
        assertEquals("tenant123", newRole.getTenantId());
        assertEquals("cn=应用根角色", newRole.getDn());
        assertEquals("role123", newRole.getGuidPath());
        assertNull(newRole.getAppId());
        assertNull(newRole.getSystemId());
    }

    @Test
    void testConstructorGeneratesIdAndClearsOperationTenant() {
        Role role = new Role();
        role.setName("运维角色");

        Y9Role newRole = new Y9Role(role, null, 7, InitDataConsts.OPERATION_TENANT_ID);

        assertNotNull(newRole.getId());
        assertFalse(newRole.getId().isBlank());
        assertEquals("运维角色", newRole.getName());
        assertEquals(7, newRole.getTabIndex());
        assertNull(newRole.getTenantId());
        assertEquals("cn=运维角色", newRole.getDn());
        assertEquals(newRole.getId(), newRole.getGuidPath());
    }

    @Test
    void testChangeParent() {
        Y9Role newParent = new Y9Role();
        newParent.setId("newParent");
        newParent.setDn("cn=新上级,cn=根角色");
        newParent.setGuidPath("root,newParent");

        y9Role.setId("role123");
        y9Role.setName("测试角色");
        y9Role.setParentId("oldParent");
        y9Role.setDn("cn=旧角色,cn=旧上级");
        y9Role.setGuidPath("oldParent,role123");

        y9Role.changeParent(newParent, 11);

        assertEquals("newParent", y9Role.getParentId());
        assertEquals(11, y9Role.getTabIndex());
        assertEquals("cn=测试角色,cn=新上级,cn=根角色", y9Role.getDn());
        assertEquals("root,newParent,role123", y9Role.getGuidPath());
    }

    @Test
    void testUpdateWithParentRole() {
        Y9Role parentRole = new Y9Role();
        parentRole.setId("parent999");
        parentRole.setDn("cn=新上级,cn=根角色");
        parentRole.setGuidPath("root,parent999");

        y9Role.setId("role123");
        y9Role.setName("原角色");
        y9Role.setDescription("原描述");
        y9Role.setCustomId("custom-old");
        y9Role.setParentId("oldParent");
        y9Role.setTenantId("tenant-old");
        y9Role.setProperties("old-properties");
        y9Role.setDn("cn=原角色,cn=旧上级");
        y9Role.setGuidPath("oldParent,role123");

        Role updatedRole = new Role();
        updatedRole.setName("更新后角色");
        updatedRole.setProperties("new-properties");
        updatedRole.setType(RoleTypeEnum.FOLDER);
        updatedRole.setDynamic(true);

        y9Role.update(updatedRole, parentRole, "tenant456");

        assertEquals("role123", y9Role.getId());
        assertEquals("更新后角色", y9Role.getName());
        assertEquals("原描述", y9Role.getDescription());
        assertEquals("custom-old", y9Role.getCustomId());
        assertEquals("new-properties", y9Role.getProperties());
        assertEquals(RoleTypeEnum.FOLDER, y9Role.getType());
        assertTrue(y9Role.getDynamic());
        assertEquals("tenant456", y9Role.getTenantId());
        assertEquals("parent999", y9Role.getParentId());
        assertEquals("cn=更新后角色,cn=新上级,cn=根角色", y9Role.getDn());
        assertEquals("root,parent999,role123", y9Role.getGuidPath());
    }

    @Test
    void testUpdateWithoutParentRole() {
        y9Role.setId("role123");
        y9Role.setName("原角色");
        y9Role.setDescription("原描述");
        y9Role.setParentId("oldParent");
        y9Role.setAppId("oldApp");
        y9Role.setSystemId("oldSystem");
        y9Role.setTenantId("oldTenant");
        y9Role.setDn("cn=原角色,cn=旧上级");
        y9Role.setGuidPath("oldParent,role123");

        Role updatedRole = new Role();
        updatedRole.setName("系统根角色");
        updatedRole.setParentId("system123");
        updatedRole.setAppId("");
        updatedRole.setSystemId(" ");

        y9Role.update(updatedRole, null, InitDataConsts.OPERATION_TENANT_ID);

        assertEquals("role123", y9Role.getId());
        assertEquals("系统根角色", y9Role.getName());
        assertEquals("原描述", y9Role.getDescription());
        assertEquals("system123", y9Role.getParentId());
        assertNull(y9Role.getTenantId());
        assertEquals("cn=系统根角色", y9Role.getDn());
        assertEquals("role123", y9Role.getGuidPath());
        assertNull(y9Role.getAppId());
        assertNull(y9Role.getSystemId());
    }

    @Test
    void testChangeProperties() {
        assertNull(y9Role.getProperties());

        y9Role.changeProperties("k1=v1");

        assertEquals("k1=v1", y9Role.getProperties());
    }

    @Test
    void testChangeTabIndex() {
        assertEquals(DefaultConsts.TAB_INDEX, y9Role.getTabIndex());

        y9Role.changeTabIndex(99);

        assertEquals(99, y9Role.getTabIndex());
    }

    @Test
    void testCompareTo() {
        Y9Role left = new Y9Role();
        left.setTabIndex(1);
        Y9Role right = new Y9Role();
        right.setTabIndex(3);

        assertTrue(left.compareTo(right) < 0);
        assertTrue(right.compareTo(left) > 0);
    }

    @Test
    void testNoArgsConstructor() {
        Y9Role role = new Y9Role();

        assertNotNull(role);
        assertEquals(RoleTypeEnum.ROLE, role.getType());
        assertFalse(role.getDynamic());
        assertEquals(DefaultConsts.TAB_INDEX, role.getTabIndex());
    }
}
