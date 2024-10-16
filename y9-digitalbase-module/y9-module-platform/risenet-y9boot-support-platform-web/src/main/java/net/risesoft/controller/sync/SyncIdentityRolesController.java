package net.risesoft.controller.sync;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9Organization;
import net.risesoft.enums.platform.TenantTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.identity.IdentityRoleCalculator;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.tenant.Y9Tenant;
import net.risesoft.y9public.service.tenant.Y9TenantService;

/**
 * 同步人员/岗位角色信息
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@RestController
@RequestMapping("/sync")
@Slf4j
@RequiredArgsConstructor
public class SyncIdentityRolesController {
    private final FastDateFormat fdf = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private final Y9TenantService y9TenantService;
    private final Y9OrganizationService y9OrganizationService;

    private final IdentityRoleCalculator identityRoleCalculator;

    /**
     * 同步所有租户的所有人员/岗位角色对应表
     * 
     * @return {@code Y9Result<Object>}
     */
    @RiseLog()
    @RequestMapping("/identityRoles")
    public Y9Result<Object> syncIdentityRoles() {
        double start = System.currentTimeMillis();
        LOGGER.info("更新人员/岗位角色开始时间：{}", fdf.format(new Date()));

        List<Y9Tenant> y9TenantList = y9TenantService.listByTenantType(TenantTypeEnum.TENANT);
        for (Y9Tenant y9Tenant : y9TenantList) {
            Y9LoginUserHolder.setTenantId(y9Tenant.getId());
            for (Y9Organization y9Organization : y9OrganizationService.list()) {
                identityRoleCalculator.recalculateByOrgUnitId(y9Organization.getId());
            }
        }

        double end = System.currentTimeMillis();
        double time = end - start;
        LOGGER.info("更新人员/岗位角色完成时间：{}", fdf.format(new Date()));
        LOGGER.info("更新人员/岗位角色所用时间：{}", time);
        return Y9Result.success("同步所有租户的所有人员/岗位角色对应表完成！");
    }

    /**
     * 同步某租户下某组织节点或其下所有人员/岗位角色对应表
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点id
     * @return {@code Y9Result<Object>}
     */
    @RiseLog()
    @RequestMapping("/identityRoles/{tenantId}/{orgUnitId}")
    public Y9Result<Object> syncIdentityRolesByOrgUnitId(@PathVariable String tenantId,
        @PathVariable String orgUnitId) {
        double start = System.currentTimeMillis();
        LOGGER.info("更新人员/岗位角色开始时间：{},租户id--->{}", fdf.format(new Date()), tenantId);

        Y9LoginUserHolder.setTenantId(tenantId);
        identityRoleCalculator.recalculateByOrgUnitId(orgUnitId);

        double end = System.currentTimeMillis();
        double time = end - start;
        LOGGER.info("更新人员/岗位角色所用时间：{}", time);
        LOGGER.info("更新人员/岗位角色完成时间：{}", fdf.format(new Date()));
        return Y9Result.success("更新人员/岗位角色完成！");
    }

    /**
     * 同步某租户下所有人员/岗位角色对应表
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<Object>}
     */
    @RiseLog()
    @RequestMapping("/identityRoles/{tenantId}")
    public Y9Result<Object> syncIdentityRolesByTenantId(@PathVariable String tenantId) {
        double start = System.currentTimeMillis();
        LOGGER.info("更新人员/岗位角色开始时间：{},租户id--->{}", fdf.format(new Date()), tenantId);

        Y9LoginUserHolder.setTenantId(tenantId);
        for (Y9Organization y9Organization : y9OrganizationService.list()) {
            identityRoleCalculator.recalculateByOrgUnitId(y9Organization.getId());
        }

        double end = System.currentTimeMillis();
        double time = end - start;
        LOGGER.info("更新人员/岗位角色所用时间：{}", time);
        LOGGER.info("更新人员/岗位角色完成时间：{}", fdf.format(new Date()));
        return Y9Result.success();
    }
}
