package net.risesoft.controller.manager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;

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
import net.risesoft.permission.annotation.IsManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.Y9ManagerService;

/**
 * 个人中心
 *
 * @author yihong
 * @date 2022/06/01
 * @since 9.6.0
 */
@RestController
@RequestMapping(value = "/api/rest/personalCenter", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
@IsManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.SECURITY_MANAGER, ManagerLevelEnum.AUDIT_MANAGER})
public class PersonalCenterController {

    private final Y9ManagerService y9ManagerService;

    /**
     * 校验密码
     *
     * @param personId 人员id
     * @param password 密码（明文）
     * @return {@code Y9Result<Boolean>}
     */
    @RiseLog(operationName = "校验密码")
    @RequestMapping("/checkPassword")
    public Y9Result<Boolean> checkPassword(@RequestParam @NotBlank String personId,
        @RequestParam @NotBlank String password) {
        return Y9Result.success(y9ManagerService.checkPassword(personId, password), "校验密码操作成功");
    }

    /**
     * 获取管理员信息
     *
     * @param managerId 管理员id
     * @return {@code Y9Result<Map<String, Object>>}
     */
    @RiseLog(operationName = "根据人员id，获取人员信息")
    @RequestMapping(value = "/getManagerById")
    public Y9Result<Map<String, Object>> getManagerById(@RequestParam @NotBlank String managerId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> map = new HashMap<>(8);

        Y9Manager manager = y9ManagerService.getById(managerId);
        map.put("manager", manager);
        if (manager.getLastModifyPasswordTime() != null) {
            Date modifyPwdTime = manager.getLastModifyPasswordTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(modifyPwdTime);
            calendar.add(Calendar.DAY_OF_MONTH, y9ManagerService.getPasswordModifiedCycle(manager.getManagerLevel()));
            map.put("nextModifyPwdTime", sdf.format(calendar.getTime()));
        }
        if (manager.getLastReviewLogTime() != null) {
            Date checkTime = manager.getLastReviewLogTime();
            Calendar checkCalendar = Calendar.getInstance();
            checkCalendar.setTime(checkTime);
            checkCalendar.add(Calendar.DAY_OF_MONTH, y9ManagerService.getReviewLogCycle(manager.getManagerLevel()));
            map.put("nextCheckTime", sdf.format(checkCalendar.getTime()));
        }
        return Y9Result.success(map, "根据人员id，获取人员信息成功");
    }

    /**
     * 检查三员密码是否过期（三员密码需定期修改）
     *
     * @param id 管理员id
     * @return {@code Y9Result<Boolean>} 为密码已过期 false 为密码未过期
     */
    @RiseLog(operationName = "检查密码是否过期")
    @RequestMapping("/isPasswordExpired")
    public Y9Result<Boolean> isPasswordExpired(@RequestParam @NotBlank String id) {
        return Y9Result.success(y9ManagerService.isPasswordExpired(id), "检查密码是否过期操作成功");
    }

    /**
     * 更新个人数据
     *
     * @param personId 人员信息
     * @param newPassword 人员新密码
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationType = OperationTypeEnum.MODIFY, operationName = "修改密码")
    @PostMapping(value = "/modifyPassword")
    public Y9Result<String> modifyPassword(@RequestParam @NotBlank String personId,
        @RequestParam @NotBlank String newPassword) {
        y9ManagerService.changePassword(personId, newPassword);
        return Y9Result.successMsg("修改成功");
    }

    /**
     * 更新个人数据
     *
     * @param y9Manager 人员信息
     * @return {@code Y9Result<Y9Manager>}
     */
    @RiseLog(operationType = OperationTypeEnum.MODIFY, operationName = "更新个人数据")
    @PostMapping(value = "/updateManager")
    public Y9Result<Y9Manager> updateManager(Y9Manager y9Manager) {
        Y9Manager manager = y9ManagerService.saveOrUpdate(y9Manager);
        return Y9Result.success(manager, "修改成功");
    }

}
