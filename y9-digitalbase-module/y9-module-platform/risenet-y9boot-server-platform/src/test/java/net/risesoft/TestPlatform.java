package net.risesoft;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.entity.org.Y9Department;
import net.risesoft.entity.org.Y9Job;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.permission.AuthorizationPrincipalTypeEnum;
import net.risesoft.model.platform.System;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.Group;
import net.risesoft.model.platform.org.Job;
import net.risesoft.model.platform.org.Organization;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.platform.resource.App;
import net.risesoft.model.platform.resource.Menu;
import net.risesoft.model.platform.resource.Operation;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.service.org.Y9GroupService;
import net.risesoft.service.org.Y9JobService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.service.permission.Y9AuthorizationService;
import net.risesoft.service.permission.cache.Y9PersonToResourceService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9Menu;
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
    private Y9PersonToResourceService y9PersonToResourceService;

    @Test
    public void test() throws InterruptedException {
        Y9LoginUserHolder.setTenantId(InitDataConsts.TENANT_ID);

        Organization organization = createTestOrganization();
        Department department = createTestDepartment(organization);
        // Y9Group group = createTestGroup(department);
        // Y9Job job = createTestJob();
        Person person = createTestPerson(department);
        // Y9Position position = createTestPosition(department, job);

        System system = y9SystemService.getById(InitDataConsts.SYSTEM_ID);
        App app = createTestApp(system);
        // Y9Menu menu = createTestMenu(app);
        // Y9Operation operation = createTestOperation(menu);

        saveAuthorization(person.getId(), AuthorizationPrincipalTypeEnum.PERSON, app.getId());

        LOGGER.debug("测试结果：{}", y9PersonToResourceService.list(person.getId()));
        Thread.sleep(1000);
        LOGGER.debug("测试结果：{}", y9PersonToResourceService.list(person.getId()));
        Assertions
            .assertTrue(y9PersonToResourceService.hasPermission(person.getId(), app.getId(), AuthorityEnum.BROWSE));

        y9OrganizationService.delete(organization.getId());
        y9AppService.delete(app.getId());
    }

    private Operation createTestOperation(Y9Menu menu) {
        Operation operation = new Operation();
        operation.setAppId(menu.getAppId());
        operation.setParentId(menu.getId());
        operation.setName("测试操作");
        operation.setSystemId(menu.getSystemId());
        return y9OperationService.saveOrUpdate(operation);
    }

    private Menu createTestMenu(Y9App app) {
        Menu menu = new Menu();
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

    private App createTestApp(System system) {
        App app = new App();
        app.setName("测试应用");
        app.setSystemId(system.getId());
        return y9AppService.saveOrUpdate(app);
    }

    private Position createTestPosition(Y9Department department, Y9Job job) {
        Position position = new Position();
        position.setJobId(job.getId());
        position.setParentId(department.getId());
        return y9PositionService.saveOrUpdate(position);
    }

    private Person createTestPerson(Department department) {
        Person person = new Person();
        person.setName("测试人员");
        person.setLoginName("testPerson");
        person.setMobile("13111111111");
        person.setParentId(department.getId());
        return y9PersonService.saveOrUpdate(person, null);
    }

    private Group createTestGroup(Y9Department department) {
        Group group = new Group();
        group.setName("测试用户组");
        group.setParentId(department.getId());
        return y9GroupService.saveOrUpdate(group);
    }

    private Job createTestJob() {
        Job job = new Job();
        job.setName("测试职位");
        job.setCode("test-job");
        return y9JobService.saveOrUpdate(job);
    }

    private Department createTestDepartment(Organization organization) {
        Department department = new Department();
        department.setName("测试部门");
        department.setParentId(organization.getId());
        return y9DepartmentService.saveOrUpdate(department);
    }

    private Organization createTestOrganization() {
        Organization organization = new Organization();
        organization.setName("测试组织");
        return y9OrganizationService.saveOrUpdate(organization);
    }

}
