package net.risesoft.entity.org;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.enums.platform.org.SexEnum;
import net.risesoft.model.platform.org.Manager;
import net.risesoft.y9.util.signing.Y9MessageDigestUtil;

class Y9ManagerTest {

    @Test
    void testConstructorWithParameters() {
        // 准备构造函数所需的参数
        // 按照组织架构层级：Y9Organization(根组织) -> Y9Department(部门) -> Y9Manager(三员)
        Y9Organization organization = new Y9Organization();
        organization.setId("organization-id-123");
        organization.setName("Root Organization");
        organization.setDn("o=Root Organization");
        organization.setGuidPath("organization-id-123");

        Integer nextSubTabIndex = 1;

        // 使用组织作为父节点
        Y9Department department = new Y9Department();
        department.setId("department-id-456");
        department.setName("Sub Department");
        department.setDn("ou=Sub Department,o=Root Organization");
        department.setGuidPath("organization-id-123,department-id-456");

        List<Y9OrgBase> ancestorList = Arrays.asList(department, organization);
        String defaultPassword = "defaultPassword123";

        // 准备Manager模型对象
        Manager manager = new Manager();
        manager.setName("Test Manager");
        manager.setLoginName("test_login");
        manager.setEmail("test@example.com");
        manager.setMobile("13800138000");
        manager.setSex(SexEnum.MALE);
        manager.setManagerLevel(ManagerLevelEnum.SYSTEM_MANAGER);
        manager.setGlobalManager(false);

        // 调用有参构造函数创建Y9Manager实例
        Y9Manager y9Manager = new Y9Manager(manager, department, nextSubTabIndex, ancestorList, defaultPassword);

        // 验证构造函数设置的各个字段
        assertThat(y9Manager).isNotNull();
        assertThat(y9Manager.getId()).isNotNull(); // ID由构造函数中的Y9IdGenerator生成
        assertThat(y9Manager.getTabIndex()).isEqualTo(nextSubTabIndex);
        assertThat(y9Manager.getDisabled()).isTrue(); // 构造函数中设置为true

        // 验证从Manager复制过来的属性
        assertThat(y9Manager.getName()).isEqualTo(manager.getName());
        assertThat(y9Manager.getLoginName()).isEqualTo(manager.getLoginName());
        assertThat(y9Manager.getEmail()).isEqualTo(manager.getEmail());
        assertThat(y9Manager.getMobile()).isEqualTo(manager.getMobile());
        assertThat(y9Manager.getSex()).isEqualTo(manager.getSex());
        assertThat(y9Manager.getManagerLevel()).isEqualTo(manager.getManagerLevel());
        assertThat(y9Manager.getGlobalManager()).isEqualTo(manager.isGlobalManager());

        // 验证密码加密
        assertThat(y9Manager.getPassword()).isNotNull();
        assertThat(Y9MessageDigestUtil.bcryptMatch(defaultPassword, y9Manager.getPassword())).isTrue();

        // 验证DN构建
        String expectedDn = "cn=" + manager.getName() + ",ou=Sub Department,o=Root Organization";
        assertThat(y9Manager.getDn()).isEqualTo(expectedDn);

        // 验证GUID路径构建
        // 三员的GUID路径应该是父节点(部门)的GUID路径加上自己的ID
        String expectedGuidPath = "organization-id-123,department-id-456," + y9Manager.getId();
        assertThat(y9Manager.getGuidPath()).isEqualTo(expectedGuidPath);

        // 验证有序路径构建
        // 根据Y9OrgUtil.buildOrderedPath方法，路径格式为：ancestor1.index,ancestor2.index,...,current.index
        // 由于列表被反转，所以是：organization.index,y9Manager.index
        String expectedOrderedPath = String.format("%05d,%05d,%05d", organization.getTabIndex(),
            department.getTabIndex(), y9Manager.getTabIndex());
        assertThat(y9Manager.getOrderedPath()).isEqualTo(expectedOrderedPath);

        // 验证组织类型
        assertThat(y9Manager.getOrgType()).isEqualTo(OrgTypeEnum.MANAGER);
    }

    @Test
    void testChangeDisabled() {
        Y9Manager manager = new Y9Manager();
        manager.setDisabled(false);

        Boolean newStatus = manager.changeDisabled();
        assertThat(newStatus).isTrue();
        assertThat(manager.getDisabled()).isTrue();

        newStatus = manager.changeDisabled();
        assertThat(newStatus).isFalse();
        assertThat(manager.getDisabled()).isFalse();
    }

    @Test
    void testChangePassword() {
        Y9Manager manager = new Y9Manager();
        String newPassword = "newSecurePassword456";

        Date beforeChange = new Date();
        manager.changePassword(newPassword);
        Date afterChange = new Date();

        assertThat(Y9MessageDigestUtil.bcryptMatch(newPassword, manager.getPassword())).isTrue();
        assertThat(manager.getLastModifyPasswordTime()).isNotNull();
        assertThat(manager.getLastModifyPasswordTime()).isAfterOrEqualTo(beforeChange);
        assertThat(manager.getLastModifyPasswordTime()).isBeforeOrEqualTo(afterChange);
    }

    @Test
    void testIsPasswordCorrect() {
        Y9Manager manager = new Y9Manager();
        String plainPassword = "plainPassword789";
        manager.setPassword(Y9MessageDigestUtil.bcrypt(plainPassword));

        assertThat(manager.isPasswordCorrect(plainPassword)).isTrue();
        assertThat(manager.isPasswordCorrect("wrongPassword")).isFalse();
    }

    @Test
    void testIsPasswordExpired() {
        Y9Manager manager = new Y9Manager();

        // 测试密码从未修改的情况
        assertThat(manager.isPasswordExpired(90)).isTrue();

        // 设置一个很早之前的密码修改时间
        Date oldDate = new Date(System.currentTimeMillis() - 100 * 24 * 60 * 60 * 1000L); // 100天前
        manager.setLastModifyPasswordTime(oldDate);
        assertThat(manager.isPasswordExpired(90)).isTrue(); // 90天周期，超过期限

        // 设置一个较近的密码修改时间
        Date recentDate = new Date(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000L); // 30天前
        manager.setLastModifyPasswordTime(recentDate);
        assertThat(manager.isPasswordExpired(90)).isFalse(); // 90天周期，未超期
    }

    @Test
    void testIsDeptManagerOfWithGlobalManager() {
        Y9Manager globalManager = new Y9Manager();
        globalManager.setGlobalManager(true);

        Y9OrgBase someParent = new Y9Department();
        someParent.setId("some-parent-id");
        Y9OrgBase someTarget = new Y9Department();
        someTarget.setId("some-target-id");

        // 全局管理员应该对任何目标组织都有管理权限
        assertThat(globalManager.isDeptManagerOf(someParent, someTarget)).isTrue();
    }

    @Test
    void testIsDeptManagerOfWithNonGlobalManager() {
        Y9Manager deptManager = new Y9Manager();
        deptManager.setGlobalManager(false);

        // 创建父组织和目标组织
        Y9OrgBase parentOrg = new Y9Department();
        parentOrg.setId("parent-org-id");
        parentOrg.setGuidPath("parent-org-id");

        Y9OrgBase targetOrg = new Y9Department();
        targetOrg.setId("target-org-id");
        targetOrg.setGuidPath("parent-org-id,target-org-id"); // targetOrg 是 parentOrg 的子组织

        // 当三员所在部门与目标组织相同时
        deptManager.setParentId(parentOrg.getId());
        assertThat(deptManager.isDeptManagerOf(parentOrg, parentOrg)).isTrue();

        // 当目标组织是三员所在部门的子部门时
        assertThat(deptManager.isDeptManagerOf(parentOrg, targetOrg)).isTrue();

        // 当目标组织不是三员所在部门或其子部门时
        Y9OrgBase otherOrg = new Y9Department();
        otherOrg.setId("other-org-id");
        otherOrg.setGuidPath("other-org-id");
        assertThat(deptManager.isDeptManagerOf(parentOrg, otherOrg)).isFalse();
    }

    @Test
    void testDefaultValues() {
        Y9Manager manager = new Y9Manager();

        // 验证默认值
        assertThat(manager.getSex()).isEqualTo(SexEnum.MALE); // 默认性别
        assertThat(manager.getManagerLevel()).isEqualTo(ManagerLevelEnum.GENERAL_USER); // 默认管理员类型
        assertThat(manager.getGlobalManager()).isFalse(); // 默认非全局管理员
        assertThat(manager.getDisabled()).isFalse(); // 通过构造函数外的创建，不会触发默认禁用逻辑
    }

}