package net.risesoft.controller.resource;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.enums.platform.ApiAccessControlType;
import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.resource.Y9ApiAccessControl;
import net.risesoft.y9public.service.resource.Y9ApiAccessControlService;

import cn.hutool.core.util.DesensitizedUtil;

/**
 * api访问控制控制器
 *
 * @author shidaobang
 * @date 2024/11/26
 * @since 9.6.8
 */
@RestController
@RequestMapping(value = "/api/rest/apiAccessControl", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
@Validated
@IsAnyManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.OPERATION_SYSTEM_MANAGER,
    ManagerLevelEnum.SECURITY_MANAGER, ManagerLevelEnum.OPERATION_SECURITY_MANAGER})
public class ApiAccessControlController {

    private final Y9ApiAccessControlService y9ApiAccessControlService;

    /**
     * 根据类型列出访问控制列表
     *
     * @param type 访问控制类型
     * @return {@code Y9Result<List<Y9ApiAccessControl>> }
     */
    @RiseLog(operationName = "根据类型列出访问控制列表", operationType = OperationTypeEnum.BROWSE)
    @GetMapping("/list")
    public Y9Result<List<Y9ApiAccessControl>> list(ApiAccessControlType type) {
        List<Y9ApiAccessControl> y9ApiAccessControlList = y9ApiAccessControlService.listByType(type);
        if (ApiAccessControlType.APP_ID_SECRET.equals(type) && (ManagerLevelEnum.SYSTEM_MANAGER
            .equals(Y9LoginUserHolder.getUserInfo().getManagerLevel())
            || ManagerLevelEnum.OPERATION_SYSTEM_MANAGER.equals(Y9LoginUserHolder.getUserInfo().getManagerLevel()))) {
            for (Y9ApiAccessControl y9ApiAccessControl : y9ApiAccessControlList) {
                y9ApiAccessControl.setValue(DesensitizedUtil.password(y9ApiAccessControl.getValue()));
            }
        }
        return Y9Result.success(y9ApiAccessControlList, "查询成功");
    }

    /**
     * 保存或修改访问控制记录
     *
     * @param apiAccessControl 访问控制
     * @return {@code Y9Result<Y9Job>}
     */
    @RiseLog(operationName = "保存或修改访问控制记录", operationType = OperationTypeEnum.MODIFY)
    @PostMapping("/saveOrUpdate")
    public Y9Result<Y9ApiAccessControl> saveOrUpdate(@Validated Y9ApiAccessControl apiAccessControl) {
        Y9ApiAccessControl y9ApiAccessControl = y9ApiAccessControlService.saveOrUpdate(apiAccessControl);
        return Y9Result.success(y9ApiAccessControl, "操作成功");
    }

    /**
     * 保存 appId appSecret 对
     *
     * @param apiAccessControl 访问控制
     * @return {@code Y9Result<Y9ApiAccessControl> }
     */
    @RiseLog(operationName = "保存 appId appSecret 对", operationType = OperationTypeEnum.MODIFY)
    @PostMapping("/saveAppIdSecret")
    public Y9Result<Y9ApiAccessControl> saveAppIdSecret(@Validated Y9ApiAccessControl apiAccessControl) {
        Y9ApiAccessControl y9ApiAccessControl = y9ApiAccessControlService.saveAppIdSecret(apiAccessControl);
        return Y9Result.success(y9ApiAccessControl, "操作成功");
    }

    /**
     * 修改启用状态
     *
     * @param id ID
     * @return {@code Y9Result<Y9ApiAccessControl> }
     */
    @IsAnyManager({ManagerLevelEnum.SECURITY_MANAGER, ManagerLevelEnum.OPERATION_SECURITY_MANAGER})
    @RiseLog(operationName = "修改启用状态", operationType = OperationTypeEnum.MODIFY)
    @PostMapping("/changeEnabled")
    public Y9Result<Y9ApiAccessControl> changeEnabled(String id) {
        Y9ApiAccessControl y9ApiAccessControl = y9ApiAccessControlService.changeEnabled(id);
        return Y9Result.success(y9ApiAccessControl, "操作成功");
    }

    /**
     * 根据id删除访问控制记录
     *
     * @param id ID
     * @return {@code Y9Result<Object> }
     */
    @RiseLog(operationName = "根据id删除访问控制记录", operationType = OperationTypeEnum.DELETE)
    @PostMapping("/delete")
    public Y9Result<Object> deleteById(String id) {
        y9ApiAccessControlService.delete(id);
        return Y9Result.successMsg("删除成功");
    }
}
