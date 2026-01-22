package net.risesoft.controller;

import javax.validation.constraints.NotBlank;

import org.apache.commons.io.FilenameUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.platform.tenant.Tenant;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;
import net.risesoft.y9public.service.tenant.Y9TenantService;

/**
 * 租户管理
 */
@RestController
@RequestMapping(value = "/api/rest/y9Tenant", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@IsAnyManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.OPERATION_SYSTEM_MANAGER})
public class TenantController {

    private final Y9FileStoreService y9FileStoreService;

    private final Y9TenantService y9TenantService;

    private final Y9Properties y9Properties;

    /**
     * 根据id，获取租户信息
     *
     * @param id 租户id
     * @return {@code Y9Result<Tenant>}
     */
    @RiseLog(operationName = "获取租户信息")
    @RequestMapping(value = "getTenantById")
    public Y9Result<Tenant> getTenantById(@NotBlank String id) {
        Tenant tenant = y9TenantService.getById(id);
        return Y9Result.success(tenant, "获取租户信息成功！");
    }

    /**
     * 上传租户Logo信息
     *
     * @param tenantId 租户id
     * @param file logo文件
     * @return {@code Y9Result<String>}
     * @throws Exception IO异常
     */
    @RiseLog(operationType = OperationTypeEnum.LOGIN, operationName = "上传租户Logo信息")
    @PostMapping(value = "/uploadTenantLogoIcon")
    public Y9Result<String> uploadTenantLogoIcon(String tenantId, @RequestParam MultipartFile file) throws Exception {
        String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), "tenantLogo", tenantId);
        String originalFilename = file.getOriginalFilename();
        String fileName = FilenameUtils.getName(originalFilename);
        Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file.getInputStream(), fullPath, fileName);
        String logoUrl = y9Properties.getCommon().getOrgBaseUrl() + "/s/" + y9FileStore.getRealFileName();
        return Y9Result.success(logoUrl, "上传租户Logo成功！");
    }

    /**
     * 保存租户编辑的信息
     *
     * @param tenant 租户信息
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationType = OperationTypeEnum.ADD, operationName = "保存租户编辑的信息")
    @PostMapping(value = "/saveTenant")
    public Y9Result<String> saveTenant(Tenant tenant) {
        y9TenantService.saveAndInitDataSource(tenant);
        return Y9Result.successMsg("保存成功！");
    }

}
