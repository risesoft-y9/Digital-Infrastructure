package net.risesoft.controller.resource;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.Y9PublishService;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.service.resource.Y9SystemService;
import net.risesoft.y9public.service.tenant.Y9TenantSystemService;

/**
 * 系统管理
 *
 * @author shidaobang
 * @date 2022/3/2
 * @since 9.6.0
 */
@Validated
@RestController
@RequestMapping(value = "/api/rest/system", produces = "application/json")
@RequiredArgsConstructor
public class SystemController {

    private final Y9SystemService y9SystemService;
    private final Y9TenantSystemService y9TenantSystemService;
    private final Y9PublishService y9PublishService;

    /**
     * 删除系统
     *
     * @param id 系统id
     * @return
     */
    @RiseLog(operationName = "删除系统")
    @PostMapping(value = "/delete")
    public Y9Result<Object> delete(@RequestParam @NotBlank String id) {
        y9SystemService.delete(id);
        y9TenantSystemService.deleteByTenantIdAndSystemId(Y9LoginUserHolder.getTenantId(), id);
        return Y9Result.successMsg("删除系统成功");
    }

    /**
     * 禁用系统
     *
     * @param id 系统id
     * @return
     */
    @RiseLog(operationName = "禁用系统")
    @PostMapping(value = "/disable")
    public Y9Result<Y9System> disable(@RequestParam @NotBlank String id) {
        return Y9Result.success(y9SystemService.disable(id), "禁用系统成功");
    }

    /**
     * 启用系统
     *
     * @param id 系统id
     * @return
     */
    @RiseLog(operationName = "启用系统")
    @PostMapping(value = "/enable")
    public Y9Result<Y9System> enable(@RequestParam @NotBlank String id) {
        return Y9Result.success(y9SystemService.enable(id), "启用系统成功");
    }

    /**
     * 根据系统id获取系统详情
     *
     * @param id 系统id
     * @return
     */
    @RiseLog(operationName = "根据系统id获取系统详情")
    @GetMapping(value = "/{id}")
    public Y9Result<Y9System> getById(@PathVariable @NotBlank String id) {
        return Y9Result.success(y9SystemService.getById(id), "根据系统id获取系统详情成功");
    }

    /**
     * 获取系统列表
     *
     * @return
     */
    @RiseLog(operationName = "获取系统列表")
    @GetMapping(value = "/list")
    public Y9Result<List<Y9System>> listAll() {
        return Y9Result.success(y9SystemService.listAll(), "获取系统列表成功");
    }

    /**
     * 根据系统中文名称，模糊搜索系统列表
     *
     * @param systemCnName 系统中文名称
     * @return
     */
    @RiseLog(operationName = "根据系统中文名称，模糊搜索系统列表")
    @GetMapping(value = "/listByCnName")
    public Y9Result<List<Y9System>> listByCnName(String systemCnName) {
        return Y9Result.success(y9SystemService.listByCnNameContaining(systemCnName), "获取系统列表成功");
    }

    /**
     * 保存系统
     *
     * @param y9System 系统
     * @return
     */
    @RiseLog(operationName = "保存系统")
    @PostMapping(value = "/save")
    public Y9Result<Y9System> save(@Validated Y9System y9System) {
        Y9System savedSystem = y9SystemService.saveOrUpdate(y9System);
        // TODO move to Service?
        y9TenantSystemService.registerSystemForTenant(Y9LoginUserHolder.getTenantId(), savedSystem.getId());
        return Y9Result.success(savedSystem, "保存系统成功");
    }

    /**
     * 保存系统排序
     *
     * @param systemIds 系统id数组
     * @return
     */
    @RiseLog(operationName = "保存系统排序", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveOrder")
    public Y9Result<String> saveOrder(@RequestParam String[] systemIds) {
        y9SystemService.saveOrder(systemIds);
        return Y9Result.success(null, "系统排序成功！");
    }
}