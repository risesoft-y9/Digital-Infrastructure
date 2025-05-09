package net.risesoft.controller.resource;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.service.resource.Y9AppService;

/**
 * 应用资源管理
 *
 * @author shidaobang
 * @author mengjuhua
 * @date 2022/3/4
 * @since 9.6.0
 */
@Validated
@RestController
@RequestMapping(value = "/api/rest/resource/app", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
@IsAnyManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.OPERATION_SYSTEM_MANAGER})
public class AppResourceController {

    private final Y9AppService y9AppService;

    /**
     * 批量删除应用
     *
     * @param ids 应用id数组
     * @return {@code Y9Result<Object>}
     */
    @RiseLog(operationName = "删除应用")
    @PostMapping(value = "/delete")
    public Y9Result<Object> delete(@RequestParam(name = "ids") @NotEmpty List<String> ids) {
        y9AppService.delete(ids);
        return Y9Result.successMsg("成功删除应用");
    }

    /**
     * 批量禁用应用
     *
     * @param ids 应用id数组
     * @return {@code Y9Result<List<Y9App>>}
     */
    @RiseLog(operationName = "禁用应用")
    @PostMapping(value = "/disable")
    public Y9Result<List<Y9App>> disable(@RequestParam(name = "ids") @NotEmpty List<String> ids) {
        return Y9Result.success(y9AppService.disable(ids), "成功禁用应用");
    }

    /**
     * 批量启用应用
     *
     * @param ids 应用id数组
     * @return {@code Y9Result<List<Y9App>>}
     */
    @RiseLog(operationName = "启用应用")
    @PostMapping(value = "/enable")
    public Y9Result<List<Y9App>> enable(@RequestParam(name = "ids") @NotEmpty List<String> ids) {
        return Y9Result.success(y9AppService.enable(ids), "成功启用应用");
    }

    /**
     * 根据应用id获取应用详情
     *
     * @param id 应用资源id
     * @return {@code Y9Result<Y9App>}
     */
    @RiseLog(operationName = "根据应用id获取应用详情")
    @GetMapping(value = "/{id}")
    @IsAnyManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.SECURITY_MANAGER,
        ManagerLevelEnum.OPERATION_SYSTEM_MANAGER})
    public Y9Result<Y9App> getById(@PathVariable @NotBlank String id) {
        return Y9Result.success(y9AppService.getById(id), "成功获取应用详情");
    }

    /**
     * 根据系统id，获取应用列表
     *
     * @param systemId 系统id
     * @return {@code Y9Result<List<Y9App>>}
     */
    @RiseLog(operationName = "根据系统id，获取应用列表")
    @GetMapping(value = "/listBySystemId")
    public Y9Result<List<Y9App>> listBySystemId(@RequestParam @NotBlank String systemId) {
        List<Y9App> apps = y9AppService.listBySystemId(systemId);
        return Y9Result.success(apps, "成功获取应用列表成功");
    }

    /**
     * 分页获取应用列表
     *
     * @param pageQuery 分页
     * @param systemId 系统id
     * @param name 应用名称
     * @return {@code Y9Page<Y9App>}
     */
    @RiseLog(operationName = "分页获取应用列表")
    @GetMapping(value = "/page")
    public Y9Page<Y9App> pageBySystemId(Y9PageQuery pageQuery, @RequestParam String systemId, String name) {
        Page<Y9App> appResourcePage = y9AppService.page(pageQuery, systemId, name);
        return Y9Page.success(pageQuery.getPage(), appResourcePage.getTotalPages(), appResourcePage.getTotalElements(),
            appResourcePage.getContent());
    }

    /**
     * 保存应用
     *
     * @param y9App 应用信息
     * @return {@code Y9Result<Y9App>}
     */
    @RiseLog(operationName = "保存应用")
    @PostMapping(value = "/save")
    public Y9Result<Y9App> save(@Validated Y9App y9App) {
        Y9App savedApp = y9AppService.saveAndRegister4Tenant(y9App);
        return Y9Result.success(savedApp, "成功保存应用");
    }

    /**
     * 保存应用排序
     *
     * @param appIds 应用id数组
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "保存应用排序", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveOrder")
    public Y9Result<String> saveOrder(@RequestParam @NotEmpty String[] appIds) {
        y9AppService.saveOrder(appIds);
        return Y9Result.success(null, "系统应用成功！");
    }
}