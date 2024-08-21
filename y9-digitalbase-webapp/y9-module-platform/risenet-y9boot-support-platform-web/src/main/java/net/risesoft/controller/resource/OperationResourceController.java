package net.risesoft.controller.resource;

import jakarta.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.permission.annotation.IsManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9public.entity.resource.Y9Operation;
import net.risesoft.y9public.service.resource.Y9OperationService;

/**
 * 页面按钮资源管理
 *
 * @author shidaobang
 * @date 2022/3/10
 * @since 9.6.0
 */
@Validated
@RestController
@RequestMapping(value = "/api/rest/resource/operation", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@IsManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.OPERATION_SYSTEM_MANAGER})
public class OperationResourceController {

    private final Y9OperationService y9OperationService;

    /**
     * 删除操作按钮资源
     *
     * @param id 按钮资源id
     * @return {@code Y9Result<Object>}
     */
    @RiseLog(operationName = "删除操作按钮资源")
    @PostMapping(value = "/delete")
    public Y9Result<Object> delete(@RequestParam @NotBlank String id) {
        y9OperationService.delete(id);
        return Y9Result.successMsg("删除操作按钮资源成功");
    }

    /**
     * 禁用操作按钮资源
     *
     * @param id 按钮资源id
     * @return {@code Y9Result<Y9Operation>}
     */
    @RiseLog(operationName = "禁用操作按钮资源")
    @PostMapping(value = "/disable")
    public Y9Result<Y9Operation> disable(@RequestParam @NotBlank String id) {
        return Y9Result.success(y9OperationService.disable(id), "禁用操作按钮资源成功");
    }

    /**
     * 启用操作按钮资源
     *
     * @param id 按钮资源id
     * @return {@code Y9Result<Y9Operation>}
     */
    @RiseLog(operationName = "启用操作按钮资源")
    @PostMapping(value = "/enable")
    public Y9Result<Y9Operation> enable(@RequestParam @NotBlank String id) {
        return Y9Result.success(y9OperationService.enable(id), "启用操作按钮资源成功");
    }

    /**
     * 根据id获取操作按钮资源详情
     *
     * @param id 按钮资源id
     * @return {@code Y9Result<Y9Operation>}
     */
    @RiseLog(operationName = "根据id获取操作按钮资源详情")
    @GetMapping(value = "/{id}")
    @IsManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.SECURITY_MANAGER,
        ManagerLevelEnum.OPERATION_SYSTEM_MANAGER})
    public Y9Result<Y9Operation> getById(@PathVariable @NotBlank String id) {
        return Y9Result.success(y9OperationService.getById(id), "根据id获取操作按钮资源详情成功");
    }

    /**
     * 保存操作按钮资源
     *
     * @param y9Operation 操作按钮资源
     * @return {@code Y9Result<Y9Operation>}
     */
    @RiseLog(operationName = "保存操作按钮资源")
    @PostMapping(value = "/save")
    public Y9Result<Y9Operation> save(@Validated Y9Operation y9Operation) {
        Y9Operation savedOperation = y9OperationService.saveOrUpdate(y9Operation);
        return Y9Result.success(savedOperation, "保存操作按钮资源成功");
    }
}
