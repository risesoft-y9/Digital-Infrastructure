package net.risesoft.controller.manager;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Manager;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.user.UserInfo;
import net.risesoft.permission.annotation.IsManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.Y9ManagerService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 子域三员管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 * @since 9.6.0
 */
@Validated
@RestController
@RequestMapping(value = "/api/rest/deptManager", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class DeptManagerController {

    private final Y9ManagerService y9ManagerService;

    /**
     * 禁用/解除禁用三员
     *
     * @param id id
     * @return {@link Y9Result}<{@link Y9Manager}>
     */
    @RiseLog(operationName = "禁用/解除禁用三员", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/changeDisabled")
    @IsManager(ManagerLevelEnum.SECURITY_MANAGER)
    public Y9Result<Y9Manager> changeDisabled(@RequestParam @NotBlank String id) {
        Y9Manager y9Manager = y9ManagerService.changeDisabled(id);
        return Y9Result.success(y9Manager, "禁用人员成功");
    }

    /**
     * 根据部门id，验证该成员是否部门管理员
     *
     * @param deptId 部门id
     * @return
     */
    @RiseLog(operationName = "根据部门id，验证该成员是否部门管理员", operationType = OperationTypeEnum.BROWSE)
    @PostMapping(value = "/checkDeptManager")
    @IsManager(ManagerLevelEnum.SYSTEM_MANAGER)
    public Y9Result<Boolean> checkDeptManager(@RequestParam @NotBlank String deptId) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        return Y9Result.success(y9ManagerService.isDeptManager(userInfo.getPersonId(), deptId));
    }

    /**
     * 判断登录名是否可用
     *
     * @param personId 人员id
     * @param loginName 登录名称
     * @return
     */
    @RiseLog(operationName = "判断登录名是否可用")
    @RequestMapping(value = "/checkLoginName")
    @IsManager(ManagerLevelEnum.SYSTEM_MANAGER)
    public Y9Result<Boolean> checkLoginName(@RequestParam String personId, @RequestParam @NotBlank String loginName) {
        return Y9Result.success(y9ManagerService.isLoginNameAvailable(personId, loginName), "判断登录名是否可用成功");
    }

    /**
     * 根据人员id，获取人员信息
     *
     * @param managerId 管理员id
     * @return
     */
    @RiseLog(operationName = "根据人员id，获取人员信息")
    @RequestMapping(value = "/getManagerById")
    @IsManager(ManagerLevelEnum.SYSTEM_MANAGER)
    public Y9Result<Y9Manager> getManagerById(@RequestParam @NotBlank String managerId) {
        return Y9Result.success(y9ManagerService.getById(managerId), "根据人员id，获取人员信息成功！");
    }

    /**
     * 根据父节点id，获取人员列表
     *
     * @param parentId 父节点id
     * @return
     * @since 9.6.1
     */
    @RiseLog(operationName = "获取人员列表")
    @RequestMapping(value = "/listManagersByParentId")
    @IsManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.SECURITY_MANAGER})
    public Y9Result<List<Y9Manager>> listManagersByParentId(@RequestParam @NotBlank String parentId) {
        return Y9Result.success(y9ManagerService.listByParentId(parentId), "获取人员列表成功！");
    }

    /**
     * 根据id数组，删除人员
     *
     * @param ids 人员id数组
     * @return
     */
    @RiseLog(operationName = "删除部门管理员", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/remove")
    @IsManager(ManagerLevelEnum.SYSTEM_MANAGER)
    public Y9Result<String> remove(@RequestParam(value = "ids") @NotEmpty List<String> ids) {
        y9ManagerService.delete(ids);
        return Y9Result.successMsg("删除人员成功");
    }

    /**
     * 重置密码
     *
     * @param personId 人员id
     * @return
     */
    @RiseLog(operationName = "重置密码", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/resetPassword")
    @IsManager(ManagerLevelEnum.SYSTEM_MANAGER)
    public Y9Result<String> resetPassword(@NotBlank @RequestParam String personId) {
        y9ManagerService.resetDefaultPassword(personId);
        return Y9Result.successMsg("重置密码成功");
    }

    /**
     * 新建或者更新部门管理员
     *
     * @param manager 部门管理员实体
     * @return
     */
    @RiseLog(operationName = "新建或者更新部门管理员信息", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/saveOrUpdate")
    @IsManager(ManagerLevelEnum.SYSTEM_MANAGER)
    public Y9Result<Y9Manager> saveOrUpdate(Y9Manager manager) {
        Y9Manager y9Manager = y9ManagerService.saveOrUpdate(manager);
        return Y9Result.success(y9Manager, "保存人员信息成功");
    }
}
