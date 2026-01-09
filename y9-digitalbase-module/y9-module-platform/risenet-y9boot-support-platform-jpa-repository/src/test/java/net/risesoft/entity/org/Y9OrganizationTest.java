package net.risesoft.entity.org;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.risesoft.consts.DefaultConsts;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.model.platform.org.Organization;
import net.risesoft.y9.exception.Y9BusinessException;

class Y9OrganizationTest {

    private Organization organization;
    private Integer nextTabIndex = 10;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setId("testId");
        organization.setName("测试组织");
        organization.setDescription("测试组织描述");
        organization.setTenantId("tenant123");
        organization.setCustomId("custom123");
        organization.setEnName("Test Organization");
        organization.setOrganizationCode("ORG001");
        organization.setOrganizationType("TYPE_A");
        organization.setVirtual(false);
        organization.setDisabled(false); // 默认启用
        organization.setProperties("{\"key\":\"value\"}");
        organization.setTabIndex(5);
    }

    @Test
    void testConstructorWithOrganization() {
        Y9Organization y9Organization = new Y9Organization(organization, nextTabIndex);

        assertEquals("testId", y9Organization.getId());
        assertEquals("测试组织", y9Organization.getName());
        assertEquals("测试组织描述", y9Organization.getDescription());
        assertEquals("tenant123", y9Organization.getTenantId());
        assertEquals("custom123", y9Organization.getCustomId());
        assertEquals("Test Organization", y9Organization.getEnName());
        assertEquals("ORG001", y9Organization.getOrganizationCode());
        assertEquals("TYPE_A", y9Organization.getOrganizationType());
        assertEquals(Boolean.FALSE, y9Organization.getVirtual());
        assertEquals("{\"key\":\"value\"}", y9Organization.getProperties());
        assertEquals(Integer.valueOf(5), y9Organization.getTabIndex());
        assertEquals(OrgTypeEnum.ORGANIZATION, y9Organization.getOrgType());
        assertNotNull(y9Organization.getDn());
        assertTrue(y9Organization.getDn().contains("测试组织"));
        assertNotNull(y9Organization.getGuidPath());
        assertEquals("testId", y9Organization.getGuidPath());
    }

    @Test
    void testConstructorWithOrganization_TabIndexIsDefault_UseNextTabIndex() {
        organization.setTabIndex(DefaultConsts.TAB_INDEX); // 默认值为0
        Y9Organization y9Organization = new Y9Organization(organization, nextTabIndex);

        assertEquals(nextTabIndex, y9Organization.getTabIndex());
    }

    @Test
    void testConstructorWithOrganization_TabIndexIsNotDefault_KeepOriginalValue() {
        organization.setTabIndex(7);
        Y9Organization y9Organization = new Y9Organization(organization, nextTabIndex);

        assertEquals(Integer.valueOf(7), y9Organization.getTabIndex());
    }

    @Test
    void testConstructorSetsOrgTypeCorrectly() {
        Y9Organization y9Organization = new Y9Organization(organization, nextTabIndex);

        assertEquals(OrgTypeEnum.ORGANIZATION, y9Organization.getOrgType());
    }

    @Test
    void testGetParentIdAlwaysReturnsNull() {
        Y9Organization y9Organization = new Y9Organization(organization, nextTabIndex);

        assertNull(y9Organization.getParentId());
    }

    @Test
    void testChangeProperties() {
        Y9Organization y9Organization = new Y9Organization(organization, nextTabIndex);
        String newProperties = "{\"updated\":\"true\"}";

        y9Organization.changeProperties(newProperties);

        assertEquals(newProperties, y9Organization.getProperties());
    }

    @Test
    void testChangeDisabled_DisableWhenAllDescendantsAreDisabled() {
        Y9Organization y9Organization = new Y9Organization(organization, nextTabIndex);
        y9Organization.setDisabled(false); // 先设置为启用状态 (disabled=false)

        Boolean result = y9Organization.changeDisabled(true); // 尝试禁用，且所有后代已禁用

        assertTrue(result); // 结果应该是禁用状态 (true)
        assertTrue(y9Organization.getDisabled()); // 实际状态应该是禁用 (true)
    }

    @Test
    void testChangeDisabled_EnableWhenAllDescendantsAreDisabled() {
        Y9Organization y9Organization = new Y9Organization(organization, nextTabIndex);
        y9Organization.setDisabled(true); // 先设置为禁用状态 (disabled=true)

        Boolean result = y9Organization.changeDisabled(true); // 尝试启用，且所有后代已禁用

        assertFalse(result); // 结果应该是启用状态 (false)
        assertFalse(y9Organization.getDisabled()); // 实际状态应该是启用 (false)
    }

    @Test
    void testChangeDisabled_AttemptDisableWhenSomeDescendantsAreEnabled_ShouldThrowException() {
        // 注意：根据changeDisabled方法的逻辑，只有当试图将一个启用的组织禁用，
        // 且并非所有后代都被禁用时，才会抛出异常
        Y9Organization y9Organization = new Y9Organization(organization, nextTabIndex);
        y9Organization.setDisabled(false); // 先设置为启用状态 (disabled=false)

        // 尝试禁用，但不是所有后代都被禁用（第二个参数为false）- 这应该抛出异常
        Y9BusinessException exception = assertThrows(Y9BusinessException.class, () -> {
            y9Organization.changeDisabled(false);
        });

        // 验证错误码是否为预期的
        assertEquals(OrgUnitErrorCodeEnum.NOT_ALL_DESCENDENTS_DISABLED.getCode(), exception.getCode());
    }

    @Test
    void testUpdateOrganization() {
        Y9Organization y9Organization = new Y9Organization(organization, nextTabIndex);
        String originalDn = y9Organization.getDn();
        String originalGuidPath = y9Organization.getGuidPath();

        // 创建新的组织对象用于更新
        Organization newOrganization = new Organization();
        newOrganization.setName("更新后的组织");
        newOrganization.setDescription("更新后的描述");
        newOrganization.setEnName("Updated Organization");
        newOrganization.setOrganizationCode("ORG002");
        newOrganization.setOrganizationType("TYPE_B");
        newOrganization.setVirtual(true);
        newOrganization.setProperties("{\"updated\":true}");
        newOrganization.setTabIndex(15);

        y9Organization.update(newOrganization);

        // 验证基本属性已更新
        assertEquals("更新后的组织", y9Organization.getName());
        assertEquals("更新后的描述", y9Organization.getDescription());
        assertEquals("Updated Organization", y9Organization.getEnName());
        assertEquals("ORG002", y9Organization.getOrganizationCode());
        assertEquals("TYPE_B", y9Organization.getOrganizationType());
        assertEquals(Boolean.TRUE, y9Organization.getVirtual());
        assertEquals("{\"updated\":true}", y9Organization.getProperties());
        assertEquals(Integer.valueOf(15), y9Organization.getTabIndex());

        // 验证 DN 被重新计算，GUID 路径保持不变（因为ID没变）
        assertNotEquals(originalDn, y9Organization.getDn());
        assertEquals(originalGuidPath, y9Organization.getGuidPath());
        assertTrue(y9Organization.getDn().contains("更新后的组织"));
    }

    @Test
    void testChangeTabIndex() {
        Y9Organization y9Organization = new Y9Organization(organization, nextTabIndex);

        y9Organization.changeTabIndex(25);

        assertEquals(Integer.valueOf(25), y9Organization.getTabIndex());
    }

    @Test
    void testVirtualFieldDefaultValue() {
        Y9Organization y9Organization = new Y9Organization(organization, nextTabIndex);

        assertEquals(Boolean.FALSE, y9Organization.getVirtual());
    }

    @Test
    void testInheritanceFromY9OrgBase() {
        Y9Organization y9Organization = new Y9Organization(organization, nextTabIndex);

        // 验证继承的属性
        assertEquals("tenant123", y9Organization.getTenantId());
        assertEquals(Boolean.FALSE, y9Organization.getDisabled());
    }
}