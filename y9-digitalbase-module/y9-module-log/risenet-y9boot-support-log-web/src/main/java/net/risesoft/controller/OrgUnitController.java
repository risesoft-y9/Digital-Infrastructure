package net.risesoft.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.enums.platform.TenantTypeEnum;
import net.risesoft.log.LogLevelEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.platform.Tenant;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9EnumUtil;

import y9.client.rest.platform.org.OrgUnitApiClient;
import y9.client.rest.platform.org.OrganizationApiClient;
import y9.client.rest.platform.tenant.TenantApiClient;

/**
 * 组织管理
 *
 * @author mengjuhua
 *
 */
@RestController
@RequestMapping(value = "/admin/orgUnit")
@RequiredArgsConstructor
public class OrgUnitController {

    private final OrganizationApiClient organizationManager;
    private final OrgUnitApiClient orgUnitManager;
    private final TenantApiClient tenantApiClient;

    /**
     * 获取租户组织机构列表
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<Organization>>}
     */
    @RiseLog(moduleName = "日志系统", operationName = "获取租户组织机构列表", logLevel = LogLevelEnum.RSLOG)
    @RequestMapping(value = "/getTenantOrganization")
    public Y9Result<List<Organization>> getTenantORGOrganization(String tenantId) {
        if (StringUtils.isBlank(tenantId)) {
            tenantId = Y9LoginUserHolder.getTenantId();
        }
        List<Organization> org = organizationManager.list(tenantId).getData();
        return Y9Result.success(org);
    }

    /**
     * 根据租户类型获取所有租户信息
     *
     * @param tenantType 租户类型
     * @return {@code Y9Result<List<Organization>>}
     */
    @RiseLog(moduleName = "日志系统", operationName = "根据租户类型获取所有租户信息", logLevel = LogLevelEnum.RSLOG)
    @RequestMapping(value = "/getTenantTreeByTenantType")
    public Y9Result<List<Organization>> getTenantTreeByTenantType(@RequestParam Integer tenantType) {
        List<Tenant> tenants =
            tenantApiClient.listByTenantType(Y9EnumUtil.valueOf(TenantTypeEnum.class, tenantType)).getData();
        List<Organization> organizationList = new ArrayList<>();
        if (!tenants.isEmpty()) {
            for (Tenant tenant : tenants) {
                if (tenant.getTenantType().equals(tenantType)) {
                    Y9LoginUserHolder.setTenantId(tenant.getId());
                    List<Organization> list = organizationManager.list(tenant.getId()).getData();
                    organizationList.addAll(list);
                }
            }
        }
        return Y9Result.success(organizationList);
    }

    /**
     * 获取组织架构树
     *
     * @param tenantId 租户id
     * @param id 父节点id
     * @param treeType 数类型
     * @return {@code Y9Result<List<OrgUnit>>}
     */
    @RiseLog(moduleName = "日志系统", operationName = "获取组织架构树", logLevel = LogLevelEnum.RSLOG)
    @RequestMapping(value = "/getTree")
    public Y9Result<List<OrgUnit>> getTree(String tenantId, @RequestParam String id,
        @RequestParam OrgTreeTypeEnum treeType) {
        if (StringUtils.isBlank(tenantId)) {
            tenantId = Y9LoginUserHolder.getTenantId();
        }
        List<OrgUnit> orgUnitList = orgUnitManager.getSubTree(tenantId, id, treeType).getData();
        return Y9Result.success(orgUnitList);
    }
}
