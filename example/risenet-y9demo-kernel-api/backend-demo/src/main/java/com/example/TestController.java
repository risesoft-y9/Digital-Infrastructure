package com.example;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.org.OrgUnitApi;
import net.risesoft.api.org.OrganizationApi;
import net.risesoft.api.permission.PersonResourceApi;
import net.risesoft.api.resource.AppApi;
import net.risesoft.consts.TreeTypeConsts;
import net.risesoft.enums.AuthorityEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.App;
import net.risesoft.model.Menu;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Organization;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;

@RestController
public class TestController {

    private final OrgUnitApi orgUnitApi;
    private final OrganizationApi organizationApi;
    private final AppApi appApi;
    private final PersonResourceApi personResourceApi;

    public TestController(OrgUnitApi orgUnitApi, OrganizationApi organizationApi, AppApi appApi, PersonResourceApi personResourceApi) {
        this.orgUnitApi = orgUnitApi;
        this.organizationApi = organizationApi;
        this.appApi = appApi;
        this.personResourceApi = personResourceApi;
    }

    public void demo() {
        // 获取所有组织机构 组织机构树第一层
        List<Organization> organizationList = organizationApi.listByType(Y9LoginUserHolder.getTenantId(), false);
        for (Organization organization : organizationList) {
            // 组织机构树第二层
            List<OrgUnit> orgUnitList1 = orgUnitApi.getSubTree(Y9LoginUserHolder.getTenantId(), organization.getId(), TreeTypeConsts.TREE_TYPE_ORG);
            // 组织岗位树第二层
            List<OrgUnit> orgUnitList2 = orgUnitApi.getSubTree(Y9LoginUserHolder.getTenantId(), organization.getId(), TreeTypeConsts.TREE_TYPE_POSITION);
        }

    }

    /**
     * 获取当前登陆用户有权限的菜单
     *
     * @return
     */
    @GetMapping("/getMenus")
    @RiseLog(operationName = "获取当前登陆用户有权限的菜单", moduleName = "日志测试系统")
    public Y9Result<List<Menu>> getMenus() {
        // 租户id
        String tenantId = Y9LoginUserHolder.getTenantId();
        // 用户id
        String personId = Y9LoginUserHolder.getPersonId();
        // 当前系统所有应用
        List<App> appList = appApi.listBySystemName(Y9Context.getSystemName());
        // 获取当前系统第一个应用，当前用户有权限的菜单
        List<Menu> menuList = new ArrayList<>();
        if (!appList.isEmpty()) {
            menuList = personResourceApi.listSubMenus(tenantId, personId, AuthorityEnum.BROWSE.getValue(), appList.get(0).getId());
        }
        return Y9Result.success(menuList);
    }

    /**
     * 获取当前登陆用户父节点信息（可能为组织机构或部门）
     *
     * @return
     */
    @GetMapping("/parent")
    @RiseLog(operationName = "获取当前登陆用户父节点信息", moduleName = "日志测试系统")
    public Y9Result<OrgUnit> getParent() {
        // 租户id
        String tenantId = Y9LoginUserHolder.getTenantId();
        // 父节点id
        String deptId = Y9LoginUserHolder.getDeptId();
        return Y9Result.success(orgUnitApi.getOrgUnit(tenantId, deptId));
    }

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    @GetMapping("/userInfo")
    public Y9Result<UserInfo> getUserInfo() {
        return Y9Result.success(Y9LoginUserHolder.getUserInfo());
    }

}
