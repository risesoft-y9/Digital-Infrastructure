package net.risesoft;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.entity.org.Y9Department;
import net.risesoft.entity.org.Y9Group;
import net.risesoft.entity.org.Y9Job;
import net.risesoft.entity.org.Y9Organization;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.permission.AuthorizationPrincipalTypeEnum;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.service.org.Y9GroupService;
import net.risesoft.service.org.Y9JobService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.service.permission.Y9AuthorizationService;
import net.risesoft.service.permission.cache.Y9PersonToResourceAndAuthorityService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.entity.resource.Y9Operation;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.service.resource.Y9AppService;
import net.risesoft.y9public.service.resource.Y9MenuService;
import net.risesoft.y9public.service.resource.Y9OperationService;
import net.risesoft.y9public.service.resource.Y9SystemService;

@SpringBootTest
@Slf4j
public class TestPlatform {

    @Autowired
    private Y9OrganizationService y9OrganizationService;
    @Autowired
    private Y9DepartmentService y9DepartmentService;
    @Autowired
    private Y9PositionService y9PositionService;
    @Autowired
    private Y9GroupService y9GroupService;
    @Autowired
    private Y9PersonService y9PersonService;
    @Autowired
    private Y9JobService y9JobService;

    @Autowired
    private Y9SystemService y9SystemService;
    @Autowired
    private Y9AppService y9AppService;
    @Autowired
    private Y9MenuService y9MenuService;
    @Autowired
    private Y9OperationService y9OperationService;

    @Autowired
    private Y9AuthorizationService y9AuthorizationService;
    @Autowired
    private Y9PersonToResourceAndAuthorityService y9PersonToResourceAndAuthorityService;

    @Test
    public void test() throws InterruptedException {
        Y9LoginUserHolder.setTenantId(InitDataConsts.TENANT_ID);

        Y9Organization organization = createTestOrganization();
        Y9Department department = createTestDepartment(organization);
        // Y9Group group = createTestGroup(department);
        // Y9Job job = createTestJob();
        Y9Person person = createTestPerson(department);
        // Y9Position position = createTestPosition(department, job);

        Y9System system = y9SystemService.getById(InitDataConsts.SYSTEM_ID);
        Y9App app = createTestApp(system);
        // Y9Menu menu = createTestMenu(app);
        // Y9Operation operation = createTestOperation(menu);

        saveAuthorization(person.getId(), AuthorizationPrincipalTypeEnum.PERSON, app.getId());

        LOGGER.debug("测试结果：{}", y9PersonToResourceAndAuthorityService.list(person.getId()));
        Thread.sleep(1000);
        LOGGER.debug("测试结果：{}", y9PersonToResourceAndAuthorityService.list(person.getId()));
        Assertions.assertTrue(
            y9PersonToResourceAndAuthorityService.hasPermission(person.getId(), app.getId(), AuthorityEnum.BROWSE));

        y9OrganizationService.delete(organization.getId());
        y9AppService.delete(app.getId());
    }

    private Y9Operation createTestOperation(Y9Menu menu) {
        Y9Operation operation = new Y9Operation();
        operation.setAppId(menu.getAppId());
        operation.setParentId(menu.getId());
        operation.setName("测试操作");
        operation.setSystemId(menu.getSystemId());
        return y9OperationService.saveOrUpdate(operation);
    }

    private Y9Menu createTestMenu(Y9App app) {
        Y9Menu menu = new Y9Menu();
        menu.setName("测试菜单");
        menu.setAppId(app.getId());
        menu.setParentId(app.getId());
        menu.setSystemId(app.getSystemId());
        return y9MenuService.saveOrUpdate(menu);
    }

    private void saveAuthorization(String principleId, AuthorizationPrincipalTypeEnum principleType,
        String resourceId) {
        y9AuthorizationService.save(AuthorityEnum.BROWSE, principleId, principleType, new String[] {resourceId});
    }

    private Y9App createTestApp(Y9System system) {
        Y9App app = new Y9App();
        app.setName("测试应用");
        app.setSystemId(system.getId());
        return y9AppService.saveOrUpdate(app);
    }

    private Y9Position createTestPosition(Y9Department department, Y9Job job) {
        Y9Position y9Position = new Y9Position();
        y9Position.setJobId(job.getId());
        y9Position.setParentId(department.getId());
        return y9PositionService.saveOrUpdate(y9Position);
    }

    private Y9Person createTestPerson(Y9Department department) {
        Y9Person y9Person = new Y9Person();
        y9Person.setName("测试人员");
        y9Person.setLoginName("testPerson");
        y9Person.setMobile("13111111111");
        y9Person.setParentId(department.getId());
        return y9PersonService.saveOrUpdate(y9Person, null);
    }

    private Y9Group createTestGroup(Y9Department department) {
        Y9Group group = new Y9Group();
        group.setName("测试用户组");
        group.setParentId(department.getId());
        return y9GroupService.saveOrUpdate(group);
    }

    private Y9Job createTestJob() {
        Y9Job job = new Y9Job();
        job.setName("测试职位");
        job.setCode("test-job");
        return y9JobService.saveOrUpdate(job);
    }

    private Y9Department createTestDepartment(Y9Organization organization) {
        Y9Department department = new Y9Department();
        department.setName("测试部门");
        department.setParentId(organization.getId());
        return y9DepartmentService.saveOrUpdate(department);
    }

    private Y9Organization createTestOrganization() {
        Y9Organization organization = new Y9Organization();
        organization.setName("测试组织");
        return y9OrganizationService.saveOrUpdate(organization);
    }

}
