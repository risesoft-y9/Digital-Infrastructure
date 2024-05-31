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
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.permission.annotation.IsManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.service.resource.Y9MenuService;

/**
 * 菜单资源管理
 *
 * @author shidaobang
 * @date 2022/3/9
 * @since 9.6.0
 */
@Validated
@RestController
@RequestMapping(value = "/api/rest/resource/menu", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@IsManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.OPERATION_SYSTEM_MANAGER})
public class MenuResourceController {

    private final Y9MenuService y9MenuService;

    /**
     * 删除菜单资源
     *
     * @param id 菜单资源Id
     * @return
     */
    @RiseLog(operationName = "删除菜单资源")
    @PostMapping(value = "/delete")
    public Y9Result<Object> delete(@RequestParam @NotBlank String id) {
        y9MenuService.delete(id);
        return Y9Result.successMsg("删除菜单资源成功");
    }

    /**
     * 禁用菜单资源
     *
     * @param id 菜单资源Id
     * @return
     */
    @RiseLog(operationName = "禁用菜单资源")
    @PostMapping(value = "/disable")
    public Y9Result<Y9Menu> disable(@RequestParam @NotBlank String id) {
        return Y9Result.success(y9MenuService.disable(id), "禁用菜单资源成功");
    }

    /**
     * 启用菜单资源
     *
     * @param id 菜单资源Id
     * @return
     */
    @RiseLog(operationName = "启用菜单资源")
    @PostMapping(value = "/enable")
    public Y9Result<Y9Menu> enable(@RequestParam @NotBlank String id) {
        return Y9Result.success(y9MenuService.enable(id), "启用菜单资源成功");
    }

    /**
     * 根据id获取菜单资源详情
     *
     * @param id 菜单资源Id
     * @return
     */
    @RiseLog(operationName = "根据id获取菜单资源详情")
    @GetMapping(value = "/{id}")
    @IsManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.SECURITY_MANAGER,
        ManagerLevelEnum.OPERATION_SYSTEM_MANAGER})
    public Y9Result<Y9Menu> getById(@PathVariable @NotBlank String id) {
        return Y9Result.success(y9MenuService.getById(id), "根据id获取菜单资源详情成功");
    }

    /**
     * 保存菜单资源
     *
     * @param appResource 菜单资源
     * @return
     */
    @RiseLog(operationName = "保存菜单资源")
    @PostMapping(value = "/save")
    public Y9Result<Y9Menu> save(@Validated Y9Menu appResource) {
        Y9Menu savedMenu = y9MenuService.saveOrUpdate(appResource);
        return Y9Result.success(savedMenu, "保存菜单资源成功");
    }

}