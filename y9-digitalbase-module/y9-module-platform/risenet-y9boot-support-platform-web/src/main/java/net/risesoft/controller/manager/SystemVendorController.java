package net.risesoft.controller.manager;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.platform.System;
import net.risesoft.model.platform.org.Manager;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.Y9ManagerService;
import net.risesoft.service.relation.Y9SystemVendorService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.service.resource.Y9SystemService;
import net.risesoft.y9public.service.tenant.Y9TenantSystemService;

/**
 * 系统开发商管理
 *
 * @author shidaobang
 * @date 2026/8/31
 */
@Validated
@RestController
@RequestMapping(value = "/api/rest/systemVendor", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SystemVendorController {

    private final Y9ManagerService y9ManagerService;
    private final Y9SystemVendorService y9SystemVendorService;
    private final Y9SystemService y9SystemService;
    private final Y9TenantSystemService y9TenantSystemService;

    /** 判断登录名是否可用 */
    @RiseLog(operationName = "判断系统开发商登录名是否可用")
    @RequestMapping(value = "/checkLoginName")
    @IsAnyManager(ManagerLevelEnum.TENANT_SYSTEM_MANAGER)
    public Y9Result<Boolean> checkLoginName(@RequestParam String managerId, @RequestParam @NotBlank String loginName) {
        return Y9Result.success(y9ManagerService.isLoginNameAvailable(managerId, loginName), "判断登录名是否可用成功");
    }

    /** 获取系统开发商信息 */
    @RiseLog(operationName = "获取系统开发商信息")
    @RequestMapping(value = "/getById")
    @IsAnyManager(ManagerLevelEnum.TENANT_SYSTEM_MANAGER)
    public Y9Result<Manager> getById(@RequestParam @NotBlank String managerId) {
        return Y9Result.success(y9ManagerService.getById(managerId), "获取系统开发商信息成功！");
    }

    /** 获取系统开发商列表 */
    @RiseLog(operationName = "获取系统开发商列表")
    @RequestMapping(value = "/list")
    @IsAnyManager(ManagerLevelEnum.TENANT_SYSTEM_MANAGER)
    public Y9Result<List<Manager>> list(@RequestParam(required = false) String name) {
        List<Manager> vendorList = y9ManagerService.listByManagerLevel(ManagerLevelEnum.SYSTEM_VENDOR);
        if (StringUtils.isNotBlank(name)) {
            vendorList = vendorList.stream()
                .filter(vendor -> StringUtils.containsIgnoreCase(vendor.getName(), name)
                    || StringUtils.containsIgnoreCase(vendor.getLoginName(), name))
                .collect(Collectors.toList());
        }
        return Y9Result.success(vendorList, "获取系统开发商列表成功！");
    }

    /** 获取系统开发商已管理的系统 */
    @RiseLog(operationName = "获取系统开发商已管理的系统")
    @RequestMapping(value = "/listSystems")
    @IsAnyManager(ManagerLevelEnum.TENANT_SYSTEM_MANAGER)
    public Y9Result<List<System>> listSystems(@RequestParam @NotBlank String managerId) {
        List<String> systemIds = y9SystemVendorService.listSystemIdByManagerId(managerId);
        return Y9Result.success(y9SystemService.listByIds(systemIds), "获取系统开发商的系统列表成功！");
    }

    /** 获取系统开发商尚未管理的租户系统 */
    @RiseLog(operationName = "获取系统开发商可添加的系统")
    @RequestMapping(value = "/listUnrelatedSystems")
    @IsAnyManager(ManagerLevelEnum.TENANT_SYSTEM_MANAGER)
    public Y9Result<List<System>> listUnrelatedSystems(@RequestParam @NotBlank String managerId) {
        List<String> relatedSystemIds = y9SystemVendorService.listSystemIdByManagerId(managerId);
        List<System> systemList = y9TenantSystemService.listSystemByTenantId(Y9LoginUserHolder.getTenantId())
            .stream()
            .filter(system -> !relatedSystemIds.contains(system.getId()))
            .collect(Collectors.toList());
        return Y9Result.success(systemList, "获取系统开发商可添加的系统成功！");
    }

    /** 删除系统开发商 */
    @RiseLog(operationName = "删除系统开发商", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/remove")
    @IsAnyManager(ManagerLevelEnum.TENANT_SYSTEM_MANAGER)
    public Y9Result<String> remove(@RequestParam @NotEmpty List<String> ids) {
        y9ManagerService.delete(ids);
        return Y9Result.successMsg("删除系统开发商成功");
    }

    /** 解除系统开发商和系统的关联 */
    @RiseLog(operationName = "解除系统开发商和系统的关联", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/removeSystem")
    @IsAnyManager(ManagerLevelEnum.TENANT_SYSTEM_MANAGER)
    public Y9Result<String> removeSystem(@RequestParam @NotBlank String managerId,
        @RequestParam @NotBlank String systemId) {
        y9SystemVendorService.deleteByManagerIdAndSystemId(managerId, systemId);
        return Y9Result.successMsg("解除关联成功");
    }

    /** 添加系统开发商和系统的关联 */
    @RiseLog(operationName = "添加系统开发商和系统的关联", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/saveSystems")
    @IsAnyManager(ManagerLevelEnum.TENANT_SYSTEM_MANAGER)
    public Y9Result<String> saveSystems(@RequestParam @NotBlank String managerId,
        @RequestParam @NotEmpty List<String> systemIds) {
        List<String> tenantSystemIds = y9TenantSystemService.listSystemIdByTenantId(Y9LoginUserHolder.getTenantId());
        if (!tenantSystemIds.containsAll(systemIds)) {
            return Y9Result.failure("只能添加当前租户已租用的系统");
        }
        y9SystemVendorService.save(managerId, systemIds);
        return Y9Result.successMsg("添加系统成功");
    }

    /** 新建或者更新系统开发商 */
    @RiseLog(operationName = "新建或者更新系统开发商", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/saveOrUpdate")
    @IsAnyManager(ManagerLevelEnum.TENANT_SYSTEM_MANAGER)
    public Y9Result<Manager> saveOrUpdate(Manager vendor) {
        vendor.setManagerLevel(ManagerLevelEnum.SYSTEM_VENDOR);
        return Y9Result.success(y9ManagerService.saveOrUpdate(vendor), "保存系统开发商成功");
    }

}
