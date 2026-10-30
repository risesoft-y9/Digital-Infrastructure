package net.risesoft.controller.manager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.platform.org.Manager;
import net.risesoft.model.platform.org.PasswordStrength;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.Y9ManagerService;
import net.risesoft.service.setting.PasswordPolicyService;
import net.risesoft.vo.manager.PersonalCenterManagerVO;
import net.risesoft.y9.Y9LoginUserHolder;

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
@IsAnyManager({ManagerLevelEnum.TENANT_SYSTEM_MANAGER, ManagerLevelEnum.TENANT_SECURITY_MANAGER,
    ManagerLevelEnum.TENANT_AUDIT_MANAGER, ManagerLevelEnum.SYSTEM_VENDOR})
public class PersonalCenterController {

    private final Y9ManagerService y9ManagerService;
    private final PasswordPolicyService passwordPolicyService;

    /**
     * 获取当前登录管理员的个人资料及密码、日志审查周期信息。
     *
     * @return {@code Y9Result<PersonalCenterManagerVO>}
     */
    @RiseLog(operationName = "获取当前管理员个人信息")
    @GetMapping("/getCurrentManager")
    public Y9Result<PersonalCenterManagerVO> getCurrentManager() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PersonalCenterManagerVO personalCenterManagerVO = new PersonalCenterManagerVO();

        Manager manager = y9ManagerService.getById(Y9LoginUserHolder.getUserInfo().getPersonId());
        int passwordModifiedCycle = y9ManagerService.getPasswordModifiedCycle(manager.getManagerLevel());
        int reviewLogCycle = y9ManagerService.getReviewLogCycle(manager.getManagerLevel());
        personalCenterManagerVO.setManager(manager);
        personalCenterManagerVO.setPasswordModifiedCycle(passwordModifiedCycle);
        personalCenterManagerVO.setReviewLogCycle(reviewLogCycle);
        if (manager.getLastModifyPasswordTime() != null) {
            Date modifyPwdTime = manager.getLastModifyPasswordTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(modifyPwdTime);
            calendar.add(Calendar.DAY_OF_MONTH, passwordModifiedCycle);
            personalCenterManagerVO.setNextModifyPwdTime(sdf.format(calendar.getTime()));
        }
        if (manager.getLastReviewLogTime() != null) {
            Date checkTime = manager.getLastReviewLogTime();
            Calendar checkCalendar = Calendar.getInstance();
            checkCalendar.setTime(checkTime);
            checkCalendar.add(Calendar.DAY_OF_MONTH, reviewLogCycle);
            personalCenterManagerVO.setNextCheckTime(sdf.format(checkCalendar.getTime()));
        }
        return Y9Result.success(personalCenterManagerVO, "获取当前管理员个人信息成功");
    }

    /**
     * 按管理员改密周期检查当前登录账号的密码是否过期。
     *
     * @return {@code Y9Result<Boolean>} 为密码已过期 false 为密码未过期
     */
    @RiseLog(operationName = "检查密码是否过期")
    @GetMapping("/isPasswordExpired")
    public Y9Result<Boolean> isPasswordExpired() {
        return Y9Result.success(y9ManagerService.isPasswordExpired(Y9LoginUserHolder.getUserInfo().getPersonId()),
            "检查密码是否过期操作成功");
    }

    /**
     * 获取当前租户的密码强度要求。
     *
     * @return 当前租户的密码强度要求
     */
    @RiseLog(operationName = "检查密码是否过期")
    @GetMapping("/getPasswordStrength")
    public Y9Result<PasswordStrength> getPasswordStrength() {
        return Y9Result.success(passwordPolicyService.getStrength());
    }

    /**
     * 从登录上下文获取当前管理员身份，校验原密码和新密码强度后修改密码。
     *
     * @param oldPassword 原密码（明文），必填
     * @param newPassword 人员新密码
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationType = OperationTypeEnum.MODIFY, operationName = "修改密码")
    @PostMapping(value = "/modifyPassword")
    public Y9Result<String> modifyPassword(@RequestParam @NotBlank String oldPassword,
        @RequestParam @NotBlank String newPassword) {
        y9ManagerService.changePassword(Y9LoginUserHolder.getUserInfo().getPersonId(), oldPassword, newPassword);
        return Y9Result.successMsg("修改成功");
    }

    /**
     * 保存个人资料，用户 ID 取自登录上下文。
     *
     * @param manager 管理员资料
     * @return 更新后的当前管理员信息
     */
    @RiseLog(operationType = OperationTypeEnum.MODIFY, operationName = "更新个人数据")
    @PostMapping(value = "/updateManager")
    public Y9Result<Manager> updateManager(Manager manager) {
        String managerId = Y9LoginUserHolder.getUserInfo().getPersonId();
        y9ManagerService.getById(managerId);
        manager.setId(managerId);
        return Y9Result.success(y9ManagerService.saveOrUpdate(manager), "修改成功");
    }

}
